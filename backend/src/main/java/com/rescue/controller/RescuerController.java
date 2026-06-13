package com.rescue.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rescue.common.Result;
import com.rescue.dto.RescuerDTO;
import com.rescue.entity.Rescuer;
import com.rescue.entity.Skill;
import com.rescue.service.RescuerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rescuers")
public class RescuerController {

    @Autowired
    private RescuerService rescuerService;

    @GetMapping("/page")
    public Result<IPage<Rescuer>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String availabilityStatus,
                                       @RequestParam(required = false) String level) {
        return Result.success(rescuerService.page(pageNum, pageSize, name, availabilityStatus, level));
    }

    @GetMapping("/{id}")
    public Result<RescuerDTO> getDetail(@PathVariable Long id) {
        return Result.success(rescuerService.getDetail(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody RescuerDTO dto) {
        boolean success = rescuerService.addRescuer(dto);
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping
    public Result<Void> update(@RequestBody RescuerDTO dto) {
        boolean success = rescuerService.updateRescuer(dto);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = rescuerService.deleteRescuer(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @GetMapping("/{id}/skills")
    public Result<List<Skill>> getSkills(@PathVariable Long id) {
        return Result.success(rescuerService.getRescuerSkills(id));
    }

    @PostMapping("/{id}/skills")
    public Result<Void> addSkill(@PathVariable Long id,
                                 @RequestParam Long skillId,
                                 @RequestParam(defaultValue = "INTERMEDIATE") String proficiency) {
        boolean success = rescuerService.addRescuerSkill(id, skillId, proficiency);
        return success ? Result.success() : Result.error("添加技能失败");
    }

    @DeleteMapping("/skills/{rescuerSkillId}")
    public Result<Void> removeSkill(@PathVariable Long rescuerSkillId) {
        boolean success = rescuerService.removeRescuerSkill(rescuerSkillId);
        return success ? Result.success() : Result.error("移除技能失败");
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = rescuerService.updateAvailabilityStatus(id, status);
        return success ? Result.success() : Result.error("更新状态失败");
    }
}
