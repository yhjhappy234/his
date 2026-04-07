package com.yhj.his.module.inventory.enums;

import lombok.Getter;

/**
 * 入库类型枚举
 */
@Getter
public enum InboundType {

    PURCHASE("采购入库", "物资采购入库"),
    RETURN("退货入库", "物资退货入库"),
    TRANSFER("调拨入库", "物资调拨入库"),
    ADJUST("盘盈入库", "盘点盘盈入库");

    private final String name;
    private final String description;

    InboundType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}