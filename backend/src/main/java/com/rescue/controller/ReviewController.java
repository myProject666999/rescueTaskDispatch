package com.rescue.controller;

import com.rescue.common.Result;
import com.rescue.entity.TaskReview;
import com.rescue.entity.TaskTimeline;
import com.rescue.service.TaskReviewService;
import com.rescue.service.TaskTimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private TaskReviewService taskReviewService;

    @Autowired
    private TaskTimelineService taskTimelineService;

    @GetMapping("/task/{taskId}")
    public Result<TaskReview> getByTaskId(@PathVariable Long taskId) {
        return Result.success(taskReviewService.getByTaskId(taskId));
    }

    @PostMapping
    public Result<Void> save(@RequestBody TaskReview review) {
        boolean success = taskReviewService.createOrUpdateReview(review);
        return success ? Result.success() : Result.error("保存复盘失败");
    }

    @GetMapping("/task/{taskId}/timelines")
    public Result<List<TaskTimeline>> getTimelines(@PathVariable Long taskId) {
        return Result.success(taskTimelineService.getByTaskId(taskId));
    }
}
