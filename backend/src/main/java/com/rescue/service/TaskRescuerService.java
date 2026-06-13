package com.rescue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rescue.entity.TaskRescuer;

import java.util.List;

public interface TaskRescuerService extends IService<TaskRescuer> {

    List<TaskRescuer> getByTaskId(Long taskId);

    List<TaskRescuer> getByRescuerId(Long rescuerId);

    boolean updateResponseStatus(Long taskRescuerId, String status);

    int countByTaskIdAndStatus(Long taskId, String status);
}
