package com.rescue.controller;

import com.rescue.common.Result;
import com.rescue.entity.TaskRescuer;
import com.rescue.service.TaskRescuerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-rescuers")
public class TaskRescuerController {

    @Autowired
    private TaskRescuerService taskRescuerService;

    @GetMapping("/task/{taskId}")
    public Result<List<TaskRescuer>> getByTaskId(@PathVariable Long taskId) {
        return Result.success(taskRescuerService.getByTaskId(taskId));
    }

    @GetMapping("/rescuer/{rescuerId}")
    public Result<List<TaskRescuer>> getByRescuerId(@PathVariable Long rescuerId) {
        return Result.success(taskRescuerService.getByRescuerId(rescuerId));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = taskRescuerService.updateResponseStatus(id, status);
        return success ? Result.success() : Result.error("状态更新失败");
    }

    @GetMapping("/task/{taskId}/count")
    public Result<Integer> countByStatus(@PathVariable Long taskId, @RequestParam String status) {
        return Result.success(taskRescuerService.countByTaskIdAndStatus(taskId, status));
    }
}
