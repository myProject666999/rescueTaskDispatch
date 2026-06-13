package com.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rescue.entity.Equipment;
import com.rescue.mapper.EquipmentMapper;
import com.rescue.service.EquipmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Override
    public IPage<Equipment> page(Integer pageNum, Integer pageSize, String category, String keyword) {
        Page<Equipment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(category)) {
            wrapper.eq(Equipment::getCategory, category);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Equipment::getEquipName, keyword)
                    .or().like(Equipment::getEquipCode, keyword));
        }
        wrapper.orderByAsc(Equipment::getCategory, Equipment::getEquipCode);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkout(Long equipmentId, Integer quantity) {
        Equipment equip = this.getById(equipmentId);
        if (equip == null || equip.getAvailableQuantity() < quantity) {
            return false;
        }
        equip.setAvailableQuantity(equip.getAvailableQuantity() - quantity);
        return this.updateById(equip);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnBack(Long equipmentId, Integer quantity) {
        Equipment equip = this.getById(equipmentId);
        if (equip == null) {
            return false;
        }
        equip.setAvailableQuantity(equip.getAvailableQuantity() + quantity);
        if (equip.getAvailableQuantity() > equip.getTotalQuantity()) {
            equip.setAvailableQuantity(equip.getTotalQuantity());
        }
        return this.updateById(equip);
    }
}
