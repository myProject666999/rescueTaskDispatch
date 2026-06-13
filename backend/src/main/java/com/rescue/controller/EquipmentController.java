package com.rescue.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rescue.common.Result;
import com.rescue.entity.Equipment;
import com.rescue.entity.TaskEquipment;
import com.rescue.service.EquipmentService;
import com.rescue.service.TaskEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipments")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private TaskEquipmentService taskEquipmentService;

    @GetMapping("/page")
    public Result<IPage<Equipment>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) String category,
                                         @RequestParam(required = false) String keyword) {
        return Result.success(equipmentService.page(pageNum, pageSize, category, keyword));
    }

    @GetMapping("/{id}")
    public Result<Equipment> getDetail(@PathVariable Long id) {
        return Result.success(equipmentService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Equipment equipment) {
        boolean success = equipmentService.save(equipment);
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping
    public Result<Void> update(@RequestBody Equipment equipment) {
        boolean success = equipmentService.updateById(equipment);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = equipmentService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PostMapping("/checkout")
    public Result<Void> checkout(@RequestParam Long taskId,
                                 @RequestParam Long equipmentId,
                                 @RequestParam Integer quantity,
                                 @RequestParam(required = false) Long operatorId,
                                 @RequestParam(required = false) String operatorName) {
        boolean success = taskEquipmentService.checkoutEquipment(taskId, equipmentId, quantity, operatorId, operatorName);
        return success ? Result.success() : Result.error("出库失败，库存不足");
    }

    @PostMapping("/return/{taskEquipmentId}")
    public Result<Void> returnBack(@PathVariable Long taskEquipmentId) {
        boolean success = taskEquipmentService.returnEquipment(taskEquipmentId);
        return success ? Result.success() : Result.error("归还失败");
    }

    @GetMapping("/task/{taskId}")
    public Result<List<TaskEquipment>> getByTaskId(@PathVariable Long taskId) {
        return Result.success(taskEquipmentService.getByTaskId(taskId));
    }
}
