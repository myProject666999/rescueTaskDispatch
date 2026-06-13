package com.rescue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rescue.entity.TaskEquipment;

import java.util.List;

public interface TaskEquipmentService extends IService<TaskEquipment> {

    List<TaskEquipment> getByTaskId(Long taskId);

    boolean checkoutEquipment(Long taskId, Long equipmentId, Integer quantity, Long operatorId, String operatorName);

    boolean returnEquipment(Long taskEquipmentId);
}
