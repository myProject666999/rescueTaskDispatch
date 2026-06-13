package com.rescue.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class RescuerDTO implements Serializable {

    private Long id;

    private String name;

    private String phone;

    private Integer gender;

    private Integer age;

    private String avatar;

    private String idCard;

    private String address;

    private String availabilityStatus;

    private String level;

    private LocalDate joinDate;

    private String remark;
}
