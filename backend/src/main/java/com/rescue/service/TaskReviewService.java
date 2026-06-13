package com.rescue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rescue.entity.TaskReview;

public interface TaskReviewService extends IService<TaskReview> {

    TaskReview getByTaskId(Long taskId);

    boolean createOrUpdateReview(TaskReview review);
}
