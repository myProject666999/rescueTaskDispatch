package com.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rescue.entity.RescuerSkill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RescuerSkillMapper extends BaseMapper<RescuerSkill> {

    List<RescuerSkill> selectByRescuerId(@Param("rescuerId") Long rescuerId);

    List<Long> selectRescuerIdsBySkillId(@Param("skillId") Long skillId, @Param("minProficiency") String minProficiency);
}
