package com.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rescue.entity.Skill;
import com.rescue.mapper.SkillMapper;
import com.rescue.service.SkillService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SkillServiceImpl extends ServiceImpl<SkillMapper, Skill> implements SkillService {

    @Override
    public List<Skill> listAll() {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Skill::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    public List<Skill> listByType(String skillType) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(skillType)) {
            wrapper.eq(Skill::getSkillType, skillType);
        }
        wrapper.orderByAsc(Skill::getSortOrder);
        return this.list(wrapper);
    }
}
