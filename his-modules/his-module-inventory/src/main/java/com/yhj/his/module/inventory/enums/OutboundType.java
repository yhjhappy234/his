package com.yhj.his.module.inventory.enums;

import lombok.Getter;

/**
 * 出库类型枚举
 */
@Getter
public enum OutboundType {

    ISSUE("领用出库", "科室领用出库"),
    TRANSFER("调拨出库", "物资调拨出库"),
    RETURN("退库出库", "物资退库出库"),
    ADJUST("盘亏出库", "盘点盘亏出库"),
    DISCARD("报损出库", "物资报损出库");

    private final String name;
    private final String description;

    OutboundType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}