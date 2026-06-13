package com.rescue.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rescue.common.Result;
import com.rescue.dto.RescueTaskDTO;
import com.rescue.entity.Rescuer;
import com.rescue.entity.RescueTask;
import com.rescue.service.RescueTaskService;
import com.rescue.vo.TaskDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class RescueTaskController {

    @Autowired
    private RescueTaskService rescueTaskService;

    @GetMapping("/page")
    public Result<IPage<RescueTask>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) String taskType,
                                          @RequestParam(required = false) String keyword) {
        return Result.success(rescueTaskService.page(pageNum, pageSize, status, taskType, keyword));
    }

    @GetMapping("/{id}")
    public Result<TaskDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(rescueTaskService.getDetail(id));
    }

    @PostMapping
    public Result<RescueTask> create(@RequestBody RescueTaskDTO dto) {
        return Result.success(rescueTaskService.createTask(dto));
    }

    @PostMapping("/{id}/dispatch")
    public Result<Void> dispatch(@PathVariable Long id) {
        boolean success = rescueTaskService.dispatchTask(id);
        return success ? Result.success() : Result.error("派发失败，可能没有匹配的可用队员");
    }

    @GetMapping("/{id}/match-rescuers")
    public Result<List<Rescuer>> matchRescuers(@PathVariable Long id) {
        return Result.success(rescueTaskService.matchRescuers(id));
    }

    @PostMapping("/{id}/assign-rescuer")
    public Result<Void> assignRescuer(@PathVariable Long id,
                                      @RequestParam Long rescuerId,
                                      @RequestParam(required = false) String reason) {
        boolean success = rescueTaskService.assignRescuer(id, rescuerId, reason);
        return success ? Result.success() : Result.error("分配失败");
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = rescueTaskService.updateTaskStatus(id, status);
        return success ? Result.success() : Result.error("状态更新失败");
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        boolean success = rescueTaskService.cancelTask(id, reason);
        return success ? Result.success() : Result.error("取消失败");
    }
}
