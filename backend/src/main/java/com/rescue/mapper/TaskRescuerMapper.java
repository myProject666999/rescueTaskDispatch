package com.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rescue.entity.TaskRescuer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskRescuerMapper extends BaseMapper<TaskRescuer> {

    List<TaskRescuer> selectByTaskId(@Param("taskId") Long taskId);

    List<TaskRescuer> selectByRescuerId(@Param("rescuerId") Long rescuerId);

    int countByTaskIdAndStatus(@Param("taskId") Long taskId, @Param("status") String status);
}
