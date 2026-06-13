package com.rescue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rescue.entity.TaskTimeline;

import java.util.List;

public interface TaskTimelineService extends IService<TaskTimeline> {

    List<TaskTimeline> getByTaskId(Long taskId);
}
