package com.yhj.his.module.emr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 知情同意书类型枚举
 */
@Getter
@AllArgsConstructor
public enum ConsentType {

    ADMISSION("入院知情同意书"),
    OPERATION("手术知情同意书"),
    ANESTHESIA("麻醉知情同意书"),
    TRANSFUSION("输血知情同意书"),
    SPECIAL_TREATMENT("特殊治疗同意书"),
    CHEMOTHERAPY("化疗知情同意书");

    private final String description;
}