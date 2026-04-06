package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 住院状态枚举
 */
@Getter
@AllArgsConstructor
public enum AdmissionStatus {

    PENDING("待入院", "等待入院"),
    IN_HOSPITAL("在院", "正在住院"),
    PENDING_DISCHARGE("待出院", "等待出院"),
    TRANSFERRING("转科中", "正在转科"),
    DISCHARGED("已出院", "已经出院");

    private final String code;
    private final String description;
}