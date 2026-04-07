package com.yhj.his.module.pharmacy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发药状态枚举
 */
@Getter
@AllArgsConstructor
public enum DispenseStatus {

    PENDING("待发药", "等待发药"),
    DISPENSED("已发药", "已发药"),
    RETURNED("已退药", "已退药"),
    CANCELLED("已取消", "已取消");

    private final String name;
    private final String description;
}