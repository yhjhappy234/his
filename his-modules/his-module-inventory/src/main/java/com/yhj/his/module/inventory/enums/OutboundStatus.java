package com.yhj.his.module.inventory.enums;

import lombok.Getter;

/**
 * 出库状态枚举
 */
@Getter
public enum OutboundStatus {

    PENDING("待审核", "出库申请待审核"),
    AUDITED("已审核", "出库已审核"),
    ISSUED("已发放", "物资已发放"),
    CONFIRMED("已确认", "出库已确认"),
    REJECTED("已拒绝", "出库申请被拒绝"),
    CANCELLED("已取消", "出库已取消");

    private final String name;
    private final String description;

    OutboundStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}