package com.rescue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rescue.entity.TaskReview;
import com.rescue.entity.TaskTimeline;
import com.rescue.mapper.TaskReviewMapper;
import com.rescue.mapper.TaskTimelineMapper;
import com.rescue.service.TaskReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class TaskReviewServiceImpl extends ServiceImpl<TaskReviewMapper, TaskReview> implements TaskReviewService {

    @Autowired
    private TaskTimelineMapper taskTimelineMapper;

    @Override
    public TaskReview getByTaskId(Long taskId) {
        return baseMapper.selectByTaskId(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdateReview(TaskReview review) {
        if (review.getReviewTime() == null) {
            review.setReviewTime(LocalDateTime.now());
        }

        TaskReview existing = baseMapper.selectByTaskId(review.getTaskId());
        boolean result;
        if (existing != null) {
            review.setId(existing.getId());
            result = this.updateById(review);
        } else {
            result = this.save(review);
        }

        if (result) {
            TaskTimeline timeline = new TaskTimeline();
            timeline.setTaskId(review.getTaskId());
            timeline.setEventType("TASK_REVIEW");
            timeline.setEventTitle("任务复盘");
            timeline.setEventDetail("完成复盘，救援效果：" + review.getRescueEffect());
            timeline.setOperatorId(review.getReviewerId());
            timeline.setOperatorName(review.getReviewerName());
            timeline.setEventTime(LocalDateTime.now());
            taskTimelineMapper.insert(timeline);
        }

        log.debug("任务复盘保存: taskId={}", review.getTaskId());
        return result;
    }
}
