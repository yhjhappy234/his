package com.yhj.his.module.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 标本类型枚举
 */
@Getter
@AllArgsConstructor
public enum SpecimenType {

    BLOOD("血液", "血液标本"),
    SERUM("血清", "血清标本"),
    PLASMA("血浆", "血浆标本"),
    URINE("尿液", "尿液标本"),
    STOOL("粪便", "粪便标本"),
    SPUTUM("痰液", "痰液标本"),
    CSF("脑脊液", "脑脊液标本"),
    PLEURAL_FLUID("胸水", "胸水标本"),
    ASCITIC_FLUID("腹水", "腹水标本"),
    SYNOVIAL_FLUID("关节液", "关节液标本"),
    SWAB("拭子", "拭子标本"),
    BIOPSY("活检", "活检标本"),
    OTHER("其他", "其他标本");

    private final String code;
    private final String description;
}