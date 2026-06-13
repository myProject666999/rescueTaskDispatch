package com.rescue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rescue.entity.Equipment;
import com.rescue.entity.TaskEquipment;
import com.rescue.entity.TaskTimeline;
import com.rescue.mapper.EquipmentMapper;
import com.rescue.mapper.TaskEquipmentMapper;
import com.rescue.mapper.TaskTimelineMapper;
import com.rescue.service.TaskEquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TaskEquipmentServiceImpl extends ServiceImpl<TaskEquipmentMapper, TaskEquipment> implements TaskEquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private TaskTimelineMapper taskTimelineMapper;

    @Override
    public List<TaskEquipment> getByTaskId(Long taskId) {
        return baseMapper.selectByTaskId(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkoutEquipment(Long taskId, Long equipmentId, Integer quantity, Long operatorId, String operatorName) {
        Equipment equip = equipmentMapper.selectById(equipmentId);
        if (equip == null || equip.getAvailableQuantity() < quantity) {
            log.warn("装备出库失败: equipmentId={}, 需要数量={}, 可用数量={}", equipmentId, quantity, equip != null ? equip.getAvailableQuantity() : 0);
            return false;
        }

        equip.setAvailableQuantity(equip.getAvailableQuantity() - quantity);
        equipmentMapper.updateById(equip);

        TaskEquipment te = new TaskEquipment();
        te.setTaskId(taskId);
        te.setEquipmentId(equipmentId);
        te.setQuantity(quantity);
        te.setOperatorId(operatorId);
        te.setOperatorName(operatorName);
        te.setCheckoutTime(LocalDateTime.now());
        te.setStatus("CHECKED_OUT");
        this.save(te);

        TaskTimeline timeline = new TaskTimeline();
        timeline.setTaskId(taskId);
        timeline.setEventType("EQUIPMENT_CHECKOUT");
        timeline.setEventTitle("装备出库");
        timeline.setEventDetail("领用 " + equip.getEquipName() + " " + quantity + equip.getUnit());
        timeline.setOperatorId(operatorId);
        timeline.setOperatorName(operatorName);
        timeline.setEventTime(LocalDateTime.now());
        taskTimelineMapper.insert(timeline);

        log.debug("装备出库成功: taskId={}, equipmentId={}, quantity={}", taskId, equipmentId, quantity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnEquipment(Long taskEquipmentId) {
        TaskEquipment te = this.getById(taskEquipmentId);
        if (te == null || "RETURNED".equals(te.getStatus())) {
            return false;
        }

        Equipment equip = equipmentMapper.selectById(te.getEquipmentId());
        if (equip != null) {
            equip.setAvailableQuantity(equip.getAvailableQuantity() + te.getQuantity());
            if (equip.getAvailableQuantity() > equip.getTotalQuantity()) {
                equip.setAvailableQuantity(equip.getTotalQuantity());
            }
            equipmentMapper.updateById(equip);
        }

        te.setStatus("RETURNED");
        te.setReturnTime(LocalDateTime.now());
        this.updateById(te);

        TaskTimeline timeline = new TaskTimeline();
        timeline.setTaskId(te.getTaskId());
        timeline.setEventType("EQUIPMENT_RETURN");
        timeline.setEventTitle("装备归还");
        timeline.setEventDetail("归还 " + (equip != null ? equip.getEquipName() : "") + " " + te.getQuantity() + (equip != null ? equip.getUnit() : ""));
        timeline.setOperatorId(te.getOperatorId());
        timeline.setOperatorName(te.getOperatorName());
        timeline.setEventTime(LocalDateTime.now());
        taskTimelineMapper.insert(timeline);

        return true;
    }
}
