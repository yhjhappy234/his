package com.yhj.his.module.inventory.enums;

import lombok.Getter;

/**
 * 入库状态枚举
 */
@Getter
public enum InboundStatus {

    PENDING("待审核", "入库申请待审核"),
    AUDITED("已审核", "入库已审核"),
    CONFIRMED("已入库", "入库已确认"),
    REJECTED("已拒绝", "入库申请被拒绝"),
    CANCELLED("已取消", "入库已取消");

    private final String name;
    private final String description;

    InboundStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}