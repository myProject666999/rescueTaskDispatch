package com.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rescue.entity.TaskEquipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskEquipmentMapper extends BaseMapper<TaskEquipment> {

    List<TaskEquipment> selectByTaskId(@Param("taskId") Long taskId);
}
