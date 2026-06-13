package com.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rescue.entity.Rescuer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RescuerMapper extends BaseMapper<Rescuer> {

    List<Rescuer> selectBySkillIds(@Param("skillIds") List<Long> skillIds, @Param("availabilityStatus") String availabilityStatus);
}
