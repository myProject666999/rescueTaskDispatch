package com.rescue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rescue.entity.Skill;

import java.util.List;

public interface SkillService extends IService<Skill> {

    List<Skill> listAll();

    List<Skill> listByType(String skillType);
}
