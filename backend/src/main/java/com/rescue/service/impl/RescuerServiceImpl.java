package com.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rescue.dto.RescuerDTO;
import com.rescue.entity.Rescuer;
import com.rescue.entity.RescuerSkill;
import com.rescue.entity.Skill;
import com.rescue.mapper.RescuerMapper;
import com.rescue.mapper.RescuerSkillMapper;
import com.rescue.mapper.SkillMapper;
import com.rescue.service.RescuerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RescuerServiceImpl extends ServiceImpl<RescuerMapper, Rescuer> implements RescuerService {

    @Autowired
    private RescuerSkillMapper rescuerSkillMapper;

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RESCUER_STATUS_KEY = "rescuer:status:";

    @Override
    public IPage<Rescuer> page(Integer pageNum, Integer pageSize, String name, String availabilityStatus, String level) {
        Page<Rescuer> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Rescuer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Rescuer::getName, name);
        }
        if (StringUtils.hasText(availabilityStatus)) {
            wrapper.eq(Rescuer::getAvailabilityStatus, availabilityStatus);
        }
        if (StringUtils.hasText(level)) {
            wrapper.eq(Rescuer::getLevel, level);
        }
        wrapper.orderByDesc(Rescuer::getCreatedTime);
        return this.page(page, wrapper);
    }

    @Override
    public RescuerDTO getDetail(Long id) {
        Rescuer rescuer = this.getById(id);
        if (rescuer == null) {
            return null;
        }
        RescuerDTO dto = new RescuerDTO();
        BeanUtils.copyProperties(rescuer, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRescuer(RescuerDTO dto) {
        Rescuer rescuer = new Rescuer();
        BeanUtils.copyProperties(dto, rescuer);
        boolean result = this.save(rescuer);
        if (result) {
            updateRedisStatus(rescuer.getId(), rescuer.getAvailabilityStatus());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRescuer(RescuerDTO dto) {
        Rescuer rescuer = new Rescuer();
        BeanUtils.copyProperties(dto, rescuer);
        boolean result = this.updateById(rescuer);
        if (result && dto.getAvailabilityStatus() != null) {
            updateRedisStatus(dto.getId(), dto.getAvailabilityStatus());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRescuer(Long id) {
        boolean result = this.removeById(id);
        if (result) {
            redisTemplate.delete(RESCUER_STATUS_KEY + id);
        }
        return result;
    }

    @Override
    public List<Skill> getRescuerSkills(Long rescuerId) {
        List<RescuerSkill> rescuerSkills = rescuerSkillMapper.selectByRescuerId(rescuerId);
        if (rescuerSkills == null || rescuerSkills.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> skillIds = rescuerSkills.stream().map(RescuerSkill::getSkillId).collect(Collectors.toList());
        return skillMapper.selectBatchIds(skillIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRescuerSkill(Long rescuerId, Long skillId, String proficiency) {
        RescuerSkill rescuerSkill = new RescuerSkill();
        rescuerSkill.setRescuerId(rescuerId);
        rescuerSkill.setSkillId(skillId);
        rescuerSkill.setProficiency(proficiency);
        return rescuerSkillMapper.insert(rescuerSkill) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRescuerSkill(Long rescuerSkillId) {
        return rescuerSkillMapper.deleteById(rescuerSkillId) > 0;
    }

    @Override
    public List<Rescuer> getAvailableRescuersBySkillIds(List<Long> skillIds) {
        return baseMapper.selectBySkillIds(skillIds, "ON_DUTY");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAvailabilityStatus(Long id, String status) {
        Rescuer rescuer = new Rescuer();
        rescuer.setId(id);
        rescuer.setAvailabilityStatus(status);
        boolean result = this.updateById(rescuer);
        if (result) {
            updateRedisStatus(id, status);
        }
        return result;
    }

    private void updateRedisStatus(Long rescuerId, String status) {
        try {
            redisTemplate.opsForValue().set(RESCUER_STATUS_KEY + rescuerId, status);
        } catch (Exception e) {
            log.error("更新Redis队员状态失败: rescuerId=" + rescuerId, e);
        }
    }
}
