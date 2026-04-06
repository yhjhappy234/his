package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 护理记录类型枚举
 */
@Getter
@AllArgsConstructor
public enum NursingRecordType {

    VITAL_SIGNS("生命体征", "生命体征记录"),
    NURSING_RECORD("护理记录", "护理过程记录"),
    ASSESSMENT("评估记录", "护理评估记录");

    private final String code;
    private final String description;
}