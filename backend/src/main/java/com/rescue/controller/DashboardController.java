package com.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rescue.common.Result;
import com.rescue.entity.RescueTask;
import com.rescue.entity.Rescuer;
import com.rescue.entity.TaskRescuer;
import com.rescue.mapper.RescueTaskMapper;
import com.rescue.mapper.RescuerMapper;
import com.rescue.mapper.TaskRescuerMapper;
import com.rescue.vo.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private RescueTaskMapper rescueTaskMapper;

    @Autowired
    private RescuerMapper rescuerMapper;

    @Autowired
    private TaskRescuerMapper taskRescuerMapper;

    @GetMapping
    public Result<DashboardVO> getDashboard() {
        DashboardVO vo = new DashboardVO();

        DashboardVO.TaskStats taskStats = new DashboardVO.TaskStats();
        taskStats.setTotal(Math.toIntExact(rescueTaskMapper.selectCount(new LambdaQueryWrapper<>())));
        taskStats.setPending(Math.toIntExact(rescueTaskMapper.selectCount(new LambdaQueryWrapper<RescueTask>().eq(RescueTask::getStatus, "PENDING"))));
        taskStats.setDispatched(Math.toIntExact(rescueTaskMapper.selectCount(new LambdaQueryWrapper<RescueTask>().eq(RescueTask::getStatus, "DISPATCHED"))));
        taskStats.setInProgress(Math.toIntExact(rescueTaskMapper.selectCount(new LambdaQueryWrapper<RescueTask>().eq(RescueTask::getStatus, "IN_PROGRESS"))));
        taskStats.setCompleted(Math.toIntExact(rescueTaskMapper.selectCount(new LambdaQueryWrapper<RescueTask>().eq(RescueTask::getStatus, "COMPLETED"))));
        taskStats.setCancelled(Math.toIntExact(rescueTaskMapper.selectCount(new LambdaQueryWrapper<RescueTask>().eq(RescueTask::getStatus, "CANCELLED"))));
        vo.setTaskStats(taskStats);

        DashboardVO.RescuerStats rescuerStats = new DashboardVO.RescuerStats();
        rescuerStats.setTotal(Math.toIntExact(rescuerMapper.selectCount(new LambdaQueryWrapper<>())));
        rescuerStats.setOnDuty(Math.toIntExact(rescuerMapper.selectCount(new LambdaQueryWrapper<Rescuer>().eq(Rescuer::getAvailabilityStatus, "ON_DUTY"))));
        rescuerStats.setResting(Math.toIntExact(rescuerMapper.selectCount(new LambdaQueryWrapper<Rescuer>().eq(Rescuer::getAvailabilityStatus, "RESTING"))));
        rescuerStats.setAway(Math.toIntExact(rescuerMapper.selectCount(new LambdaQueryWrapper<Rescuer>().eq(Rescuer::getAvailabilityStatus, "AWAY"))));
        rescuerStats.setOnTheWay(Math.toIntExact(taskRescuerMapper.selectCount(new LambdaQueryWrapper<TaskRescuer>().eq(TaskRescuer::getResponseStatus, "DEPARTED"))));
        rescuerStats.setArrived(Math.toIntExact(taskRescuerMapper.selectCount(new LambdaQueryWrapper<TaskRescuer>().eq(TaskRescuer::getResponseStatus, "ARRIVED"))));
        vo.setRescuerStats(rescuerStats);

        LambdaQueryWrapper<RescueTask> recentWrapper = new LambdaQueryWrapper<>();
        recentWrapper.orderByDesc(RescueTask::getCreatedTime).last("LIMIT 5");
        List<RescueTask> recentTaskList = rescueTaskMapper.selectList(recentWrapper);
        List<Map<String, Object>> recentTasks = new ArrayList<>();
        for (RescueTask task : recentTaskList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", task.getId());
            map.put("taskNo", task.getTaskNo());
            map.put("taskTitle", task.getTaskTitle());
            map.put("taskType", task.getTaskType());
            map.put("status", task.getStatus());
            map.put("dangerLevel", task.getDangerLevel());
            map.put("location", task.getLocation());
            map.put("createdTime", task.getCreatedTime());
            recentTasks.add(map);
        }
        vo.setRecentTasks(recentTasks);

        List<Map<String, Object>> statusDist = new ArrayList<>();
        String[] statusNames = {"PENDING", "DISPATCHED", "IN_PROGRESS", "COMPLETED", "CANCELLED"};
        for (String s : statusNames) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", s);
            m.put("value", rescueTaskMapper.selectCount(new LambdaQueryWrapper<RescueTask>().eq(RescueTask::getStatus, s)));
            statusDist.add(m);
        }
        vo.setTaskStatusDistribution(statusDist);

        List<Map<String, Object>> typeDist = new ArrayList<>();
        String[] typeNames = {"山地救援", "水域救援", "医疗救援", "综合救援"};
        for (String t : typeNames) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", t);
            m.put("value", rescueTaskMapper.selectCount(new LambdaQueryWrapper<RescueTask>().eq(RescueTask::getTaskType, t)));
            typeDist.add(m);
        }
        vo.setTaskTypeDistribution(typeDist);

        return Result.success(vo);
    }
}
