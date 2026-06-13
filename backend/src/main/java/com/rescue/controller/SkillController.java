package com.rescue.controller;

import com.rescue.common.Result;
import com.rescue.entity.Skill;
import com.rescue.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @GetMapping
    public Result<List<Skill>> list() {
        return Result.success(skillService.listAll());
    }

    @GetMapping("/type/{skillType}")
    public Result<List<Skill>> listByType(@PathVariable String skillType) {
        return Result.success(skillService.listByType(skillType));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Skill skill) {
        boolean success = skillService.save(skill);
        return success ? Result.success() : Result.error("添加失败");
    }

    @PutMapping
    public Result<Void> update(@RequestBody Skill skill) {
        boolean success = skillService.updateById(skill);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = skillService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
