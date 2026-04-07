package com.yhj.his.module.pharmacy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 采购订单状态枚举
 */
@Getter
@AllArgsConstructor
public enum PurchaseOrderStatus {

    DRAFT("草稿", "草稿状态"),
    PENDING("待审核", "等待审核"),
    APPROVED("已审核", "审核通过"),
    REJECTED("已拒绝", "审核拒绝"),
    SHIPPED("已发货", "供应商已发货"),
    PARTIAL_RECEIVED("部分入库", "部分药品已入库"),
    RECEIVED("已入库", "全部入库完成"),
    COMPLETED("已完成", "订单完成"),
    CANCELLED("已取消", "订单已取消");

    private final String name;
    private final String description;
}