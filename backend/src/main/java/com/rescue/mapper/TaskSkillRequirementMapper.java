package com.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rescue.entity.TaskSkillRequirement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskSkillRequirementMapper extends BaseMapper<TaskSkillRequirement> {

    List<TaskSkillRequirement> selectByTaskId(@Param("taskId") Long taskId);
}
