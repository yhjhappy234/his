package com.yhj.his.module.inventory.enums;

import lombok.Getter;

/**
 * 申领状态枚举
 */
@Getter
public enum RequisitionStatus {

    PENDING("待审批", "领料申请待审批"),
    APPROVED("已审批", "领料申请已审批"),
    ISSUED("已发放", "物资已发放"),
    RECEIVED("已接收", "科室已接收"),
    REJECTED("已拒绝", "领料申请被拒绝"),
    CANCELLED("已取消", "领料申请已取消");

    private final String name;
    private final String description;

    RequisitionStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}