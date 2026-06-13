package com.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rescue.dto.RescueTaskDTO;
import com.rescue.entity.*;
import com.rescue.mapper.*;
import com.rescue.service.RescueTaskService;
import com.rescue.vo.TaskDetailVO;
import com.rescue.vo.TaskRescuerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RescueTaskServiceImpl extends ServiceImpl<RescueTaskMapper, RescueTask> implements RescueTaskService {

    @Autowired
    private TaskSkillRequirementMapper taskSkillRequirementMapper;

    @Autowired
    private TaskRescuerMapper taskRescuerMapper;

    @Autowired
    private TaskEquipmentMapper taskEquipmentMapper;

    @Autowired
    private TaskTimelineMapper taskTimelineMapper;

    @Autowired
    private TaskReviewMapper taskReviewMapper;

    @Autowired
    private RescuerMapper rescuerMapper;

    @Autowired
    private RescuerSkillMapper rescuerSkillMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TASK_NO_PREFIX = "RT";
    private static final AtomicInteger sequence = new AtomicInteger(0);

    @Override
    public IPage<RescueTask> page(Integer pageNum, Integer pageSize, String status, String taskType, String keyword) {
        Page<RescueTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<RescueTask> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(RescueTask::getStatus, status);
        }
        if (StringUtils.hasText(taskType)) {
            wrapper.eq(RescueTask::getTaskType, taskType);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(RescueTask::getTaskTitle, keyword)
                    .or().like(RescueTask::getLocation, keyword)
                    .or().like(RescueTask::getTaskNo, keyword));
        }
        wrapper.orderByDesc(RescueTask::getCreatedTime);
        return this.page(page, wrapper);
    }

    @Override
    public TaskDetailVO getDetail(Long id) {
        RescueTask task = this.getById(id);
        if (task == null) {
            return null;
        }

        TaskDetailVO vo = new TaskDetailVO();
        vo.setTask(task);
        vo.setSkillRequirements(taskSkillRequirementMapper.selectByTaskId(id));

        List<TaskRescuer> taskRescuers = taskRescuerMapper.selectByTaskId(id);
        List<TaskRescuerVO> rescuerVOs = new ArrayList<>();
        for (TaskRescuer tr : taskRescuers) {
            TaskRescuerVO trVO = new TaskRescuerVO();
            trVO.setTaskRescuer(tr);
            trVO.setRescuer(rescuerMapper.selectById(tr.getRescuerId()));
            rescuerVOs.add(trVO);
        }
        vo.setRescuers(rescuerVOs);

        vo.setEquipments(taskEquipmentMapper.selectByTaskId(id));
        vo.setTimelines(taskTimelineMapper.selectByTaskId(id));
        vo.setReview(taskReviewMapper.selectByTaskId(id));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueTask createTask(RescueTaskDTO dto) {
        RescueTask task = new RescueTask();
        BeanUtils.copyProperties(dto, task);
        task.setTaskNo(generateTaskNo());
        task.setStatus("PENDING");
        this.save(task);

        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            for (Long skillId : dto.getSkillIds()) {
                TaskSkillRequirement req = new TaskSkillRequirement();
                req.setTaskId(task.getId());
                req.setSkillId(skillId);
                req.setRequiredCount(1);
                req.setMinProficiency("BEGINNER");
                taskSkillRequirementMapper.insert(req);
            }
        }

        addTimeline(task.getId(), "TASK_CREATED", "任务创建",
                "任务 " + task.getTaskNo() + " 已创建", dto.getCreatorId(), null);

        log.debug("任务创建成功: id={}, taskNo={}", task.getId(), task.getTaskNo());
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean dispatchTask(Long taskId) {
        RescueTask task = this.getById(taskId);
        if (task == null || !"PENDING".equals(task.getStatus())) {
            return false;
        }

        List<Rescuer> matched = matchRescuers(taskId);
        if (matched.isEmpty()) {
            log.warn("任务 {} 没有匹配到可用队员", taskId);
            return false;
        }

        for (Rescuer rescuer : matched) {
            TaskRescuer tr = new TaskRescuer();
            tr.setTaskId(taskId);
            tr.setRescuerId(rescuer.getId());
            tr.setResponseStatus("NOTIFIED");
            tr.setEstimatedArrival(30);
            taskRescuerMapper.insert(tr);

            try {
                redisTemplate.opsForValue().set("rescuer:response:" + taskId + ":" + rescuer.getId(), "NOTIFIED");
            } catch (Exception e) {
                log.error("Redis写入队员响应状态失败", e);
            }
        }

        task.setStatus("DISPATCHED");
        task.setDispatchTime(LocalDateTime.now());
        this.updateById(task);

        addTimeline(taskId, "TASK_DISPATCHED", "任务派发",
                "已通知 " + matched.size() + " 名队员", task.getCreatorId(), null);

        log.debug("任务派发成功: taskId={}, matchedRescuers={}", taskId, matched.size());
        return true;
    }

    @Override
    public List<Rescuer> matchRescuers(Long taskId) {
        List<TaskSkillRequirement> requirements = taskSkillRequirementMapper.selectByTaskId(taskId);
        if (requirements == null || requirements.isEmpty()) {
            LambdaQueryWrapper<Rescuer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Rescuer::getAvailabilityStatus, "ON_DUTY");
            return rescuerMapper.selectList(wrapper);
        }

        List<Long> skillIds = requirements.stream()
                .map(TaskSkillRequirement::getSkillId)
                .collect(Collectors.toList());

        return rescuerMapper.selectBySkillIds(skillIds, "ON_DUTY");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRescuer(Long taskId, Long rescuerId, String reason) {
        LambdaQueryWrapper<TaskRescuer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskRescuer::getTaskId, taskId)
               .eq(TaskRescuer::getRescuerId, rescuerId);
        TaskRescuer existing = taskRescuerMapper.selectOne(wrapper);
        if (existing != null) {
            return false;
        }

        TaskRescuer tr = new TaskRescuer();
        tr.setTaskId(taskId);
        tr.setRescuerId(rescuerId);
        tr.setResponseStatus("NOTIFIED");
        tr.setAssignReason(reason);
        tr.setEstimatedArrival(30);
        taskRescuerMapper.insert(tr);

        try {
            redisTemplate.opsForValue().set("rescuer:response:" + taskId + ":" + rescuerId, "NOTIFIED");
        } catch (Exception e) {
            log.error("Redis写入队员响应状态失败", e);
        }

        Rescuer rescuer = rescuerMapper.selectById(rescuerId);
        addTimeline(taskId, "RESCUER_ASSIGNED", "分配队员",
                "分配队员 " + (rescuer != null ? rescuer.getName() : rescuerId), null, null);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskStatus(Long id, String status) {
        RescueTask task = this.getById(id);
        if (task == null) {
            return false;
        }

        task.setStatus(status);
        if ("IN_PROGRESS".equals(status) && task.getStartTime() == null) {
            task.setStartTime(LocalDateTime.now());
        }
        if ("COMPLETED".equals(status)) {
            task.setEndTime(LocalDateTime.now());
        }
        boolean result = this.updateById(task);

        addTimeline(id, "STATUS_CHANGE", "状态变更",
                "任务状态变更为 " + status, null, null);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelTask(Long id, String reason) {
        RescueTask task = this.getById(id);
        if (task == null) {
            return false;
        }

        task.setStatus("CANCELLED");
        task.setEndTime(LocalDateTime.now());
        this.updateById(task);

        addTimeline(id, "TASK_CANCELLED", "任务取消", reason, null, null);
        return true;
    }

    private String generateTaskNo() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return TASK_NO_PREFIX + LocalDateTime.now().format(fmt) + String.format("%03d", sequence.incrementAndGet() % 1000);
    }

    private void addTimeline(Long taskId, String eventType, String eventTitle, String eventDetail,
                             Long operatorId, String operatorName) {
        TaskTimeline timeline = new TaskTimeline();
        timeline.setTaskId(taskId);
        timeline.setEventType(eventType);
        timeline.setEventTitle(eventTitle);
        timeline.setEventDetail(eventDetail);
        timeline.setOperatorId(operatorId);
        timeline.setOperatorName(operatorName);
        timeline.setEventTime(LocalDateTime.now());
        taskTimelineMapper.insert(timeline);
    }
}
