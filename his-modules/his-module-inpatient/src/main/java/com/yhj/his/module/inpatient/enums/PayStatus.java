package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结算状态枚举
 */
@Getter
@AllArgsConstructor
public enum PayStatus {

    UNSETTLED("未结算", "未结算"),
    SETTLED("已结算", "已结算"),
    REFUNDED("已退费", "已退费");

    private final String code;
    private final String description;
}