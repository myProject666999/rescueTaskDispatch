package com.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rescue.entity.TaskTimeline;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskTimelineMapper extends BaseMapper<TaskTimeline> {

    List<TaskTimeline> selectByTaskId(@Param("taskId") Long taskId);
}
