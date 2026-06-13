package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("rescue_task")
public class RescueTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskNo;

    private String taskTitle;

    private String taskType;

    private String dangerLevel;

    private String location;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String victimInfo;

    private Integer victimCount;

    private String reporterName;

    private String reporterPhone;

    private String description;

    private String status;

    private LocalDateTime dispatchTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long creatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
