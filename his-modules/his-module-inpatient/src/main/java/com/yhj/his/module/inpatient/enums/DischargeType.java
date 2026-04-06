package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 出院类型枚举
 */
@Getter
@AllArgsConstructor
public enum DischargeType {

    CURED("治愈", "治愈出院"),
    IMPROVED("好转", "好转出院"),
    UNCHANGED("未愈", "未愈出院"),
    DEATH("死亡", "死亡出院"),
    TRANSFER("转院", "转院出院"),
    VOLUNTARY("自动出院", "患者自动出院");

    private final String code;
    private final String description;
}