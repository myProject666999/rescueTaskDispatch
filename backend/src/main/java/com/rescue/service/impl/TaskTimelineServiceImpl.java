package com.rescue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rescue.entity.TaskTimeline;
import com.rescue.mapper.TaskTimelineMapper;
import com.rescue.service.TaskTimelineService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskTimelineServiceImpl extends ServiceImpl<TaskTimelineMapper, TaskTimeline> implements TaskTimelineService {

    @Override
    public List<TaskTimeline> getByTaskId(Long taskId) {
        return baseMapper.selectByTaskId(taskId);
    }
}
