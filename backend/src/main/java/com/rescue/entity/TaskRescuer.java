package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("task_rescuer")
public class TaskRescuer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;

    private Long rescuerId;

    private String responseStatus;

    private LocalDateTime responseTime;

    private LocalDateTime departTime;

    private LocalDateTime arriveTime;

    private LocalDateTime withdrawTime;

    private Integer estimatedArrival;

    private String assignReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
