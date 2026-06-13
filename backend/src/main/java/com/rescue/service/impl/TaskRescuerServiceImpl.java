package com.rescue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rescue.entity.TaskRescuer;
import com.rescue.entity.TaskTimeline;
import com.rescue.mapper.TaskRescuerMapper;
import com.rescue.mapper.TaskTimelineMapper;
import com.rescue.service.TaskRescuerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TaskRescuerServiceImpl extends ServiceImpl<TaskRescuerMapper, TaskRescuer> implements TaskRescuerService {

    @Autowired
    private TaskTimelineMapper taskTimelineMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<TaskRescuer> getByTaskId(Long taskId) {
        return baseMapper.selectByTaskId(taskId);
    }

    @Override
    public List<TaskRescuer> getByRescuerId(Long rescuerId) {
        return baseMapper.selectByRescuerId(rescuerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateResponseStatus(Long taskRescuerId, String status) {
        TaskRescuer tr = this.getById(taskRescuerId);
        if (tr == null) {
            return false;
        }

        tr.setResponseStatus(status);
        LocalDateTime now = LocalDateTime.now();

        switch (status) {
            case "RESPONDED":
                tr.setResponseTime(now);
                break;
            case "DEPARTED":
                tr.setDepartTime(now);
                break;
            case "ARRIVED":
                tr.setArriveTime(now);
                break;
            case "WITHDRAWN":
                tr.setWithdrawTime(now);
                break;
            case "REJECTED":
                break;
            default:
                break;
        }

        boolean result = this.updateById(tr);

        if (result) {
            try {
                String redisKey = "rescuer:response:" + tr.getTaskId() + ":" + tr.getRescuerId();
                redisTemplate.opsForValue().set(redisKey, status);
                log.debug("更新Redis队员响应状态: key={}, status={}", redisKey, status);
            } catch (Exception e) {
                log.error("更新Redis队员响应状态失败", e);
            }

            String eventTitle;
            String eventDetail;
            switch (status) {
                case "RESPONDED":
                    eventTitle = "队员响应";
                    eventDetail = "队员已响应，30分钟内可到达";
                    break;
                case "DEPARTED":
                    eventTitle = "队员出动";
                    eventDetail = "队员已出发前往救援";
                    break;
                case "ARRIVED":
                    eventTitle = "队员到场";
                    eventDetail = "队员已到达现场";
                    break;
                case "WITHDRAWN":
                    eventTitle = "队员撤离";
                    eventDetail = "队员已撤离";
                    break;
                case "REJECTED":
                    eventTitle = "队员拒绝";
                    eventDetail = "队员无法参与此次任务";
                    break;
                default:
                    eventTitle = "状态变更";
                    eventDetail = "响应状态变更为 " + status;
            }

            TaskTimeline timeline = new TaskTimeline();
            timeline.setTaskId(tr.getTaskId());
            timeline.setEventType("RESCUER_" + status);
            timeline.setEventTitle(eventTitle);
            timeline.setEventDetail(eventDetail);
            timeline.setOperatorId(tr.getRescuerId());
            timeline.setEventTime(now);
            taskTimelineMapper.insert(timeline);
        }

        return result;
    }

    @Override
    public int countByTaskIdAndStatus(Long taskId, String status) {
        return baseMapper.countByTaskIdAndStatus(taskId, status);
    }
}
