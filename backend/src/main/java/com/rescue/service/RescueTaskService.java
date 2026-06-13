package com.rescue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rescue.dto.RescueTaskDTO;
import com.rescue.entity.Rescuer;
import com.rescue.entity.RescueTask;
import com.rescue.vo.TaskDetailVO;

import java.util.List;

public interface RescueTaskService extends IService<RescueTask> {

    IPage<RescueTask> page(Integer pageNum, Integer pageSize, String status, String taskType, String keyword);

    TaskDetailVO getDetail(Long id);

    RescueTask createTask(RescueTaskDTO dto);

    boolean dispatchTask(Long taskId);

    List<Rescuer> matchRescuers(Long taskId);

    boolean assignRescuer(Long taskId, Long rescuerId, String reason);

    boolean updateTaskStatus(Long id, String status);

    boolean cancelTask(Long id, String reason);
}
