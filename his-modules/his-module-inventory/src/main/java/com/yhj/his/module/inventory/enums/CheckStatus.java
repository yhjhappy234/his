package com.yhj.his.module.inventory.enums;

import lombok.Getter;

/**
 * 盘点状态枚举
 */
@Getter
public enum CheckStatus {

    PENDING("待盘点", "盘点计划待执行"),
    IN_PROGRESS("盘点中", "盘点进行中"),
    COMPLETED("已完成", "盘点已完成"),
    ADJUSTED("已调整", "盘点差异已调整"),
    CANCELLED("已取消", "盘点已取消");

    private final String name;
    private final String description;

    CheckStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}