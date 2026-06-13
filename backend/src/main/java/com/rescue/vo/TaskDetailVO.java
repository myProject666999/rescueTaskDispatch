package com.rescue.vo;

import com.rescue.entity.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TaskDetailVO implements Serializable {

    private RescueTask task;

    private List<TaskSkillRequirement> skillRequirements;

    private List<TaskRescuerVO> rescuers;

    private List<TaskEquipment> equipments;

    private List<TaskTimeline> timelines;

    private TaskReview review;
}
