package com.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rescue.entity.TaskReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskReviewMapper extends BaseMapper<TaskReview> {

    TaskReview selectByTaskId(@Param("taskId") Long taskId);
}
