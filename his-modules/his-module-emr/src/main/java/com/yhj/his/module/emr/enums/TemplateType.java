package com.yhj.his.module.emr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 病历模板类型枚举
 */
@Getter
@AllArgsConstructor
public enum TemplateType {

    OUTPATIENT_FIRST("门诊初诊病历模板"),
    OUTPATIENT_FOLLOW("门诊复诊病历模板"),
    ADMISSION("入院记录模板"),
    PROGRESS("病程记录模板"),
    DISCHARGE("出院记录模板"),
    OPERATION("手术记录模板"),
    NURSING("护理记录模板");

    private final String description;
}