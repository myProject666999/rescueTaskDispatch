package com.rescue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rescue.entity.Equipment;

public interface EquipmentService extends IService<Equipment> {

    IPage<Equipment> page(Integer pageNum, Integer pageSize, String category, String keyword);

    boolean checkout(Long equipmentId, Integer quantity);

    boolean returnBack(Long equipmentId, Integer quantity);
}
