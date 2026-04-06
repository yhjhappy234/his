package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 床位类型枚举
 */
@Getter
@AllArgsConstructor
public enum BedType {

    NORMAL("普通", "普通床位"),
    VIP("VIP", "VIP床位"),
    ICU("ICU", "重症监护床位");

    private final String code;
    private final String description;
}