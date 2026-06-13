package com.rescue.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVO implements Serializable {

    private TaskStats taskStats;

    private RescuerStats rescuerStats;

    private List<Map<String, Object>> recentTasks;

    private List<Map<String, Object>> taskStatusDistribution;

    private List<Map<String, Object>> taskTypeDistribution;

    @Data
    public static class TaskStats implements Serializable {
        private Integer total;
        private Integer pending;
        private Integer dispatched;
        private Integer inProgress;
        private Integer completed;
        private Integer cancelled;
    }

    @Data
    public static class RescuerStats implements Serializable {
        private Integer total;
        private Integer onDuty;
        private Integer resting;
        private Integer away;
        private Integer onTheWay;
        private Integer arrived;
    }
}
