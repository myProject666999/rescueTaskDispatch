package com.rescue.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RescueTaskDTO implements Serializable {

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

    private Long creatorId;

    private List<Long> skillIds;
}
