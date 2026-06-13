package com.rescue.vo;

import com.rescue.entity.Rescuer;
import com.rescue.entity.TaskRescuer;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskRescuerVO implements Serializable {

    private TaskRescuer taskRescuer;

    private Rescuer rescuer;
}
