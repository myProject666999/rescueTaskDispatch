package com.rescue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rescue.dto.RescuerDTO;
import com.rescue.entity.Rescuer;
import com.rescue.entity.RescuerSkill;
import com.rescue.entity.Skill;

import java.util.List;

public interface RescuerService extends IService<Rescuer> {

    IPage<Rescuer> page(Integer pageNum, Integer pageSize, String name, String availabilityStatus, String level);

    RescuerDTO getDetail(Long id);

    boolean addRescuer(RescuerDTO dto);

    boolean updateRescuer(RescuerDTO dto);

    boolean deleteRescuer(Long id);

    List<Skill> getRescuerSkills(Long rescuerId);

    boolean addRescuerSkill(Long rescuerId, Long skillId, String proficiency);

    boolean removeRescuerSkill(Long rescuerSkillId);

    List<Rescuer> getAvailableRescuersBySkillIds(List<Long> skillIds);

    boolean updateAvailabilityStatus(Long id, String status);
}
