package com.yhj.his.module.inventory.enums;

import lombok.Getter;

/**
 * 物资状态枚举
 */
@Getter
public enum MaterialStatus {

    NORMAL("正常", "物资正常可用"),
    SUSPENDED("停用", "物资已停用"),
    DELETED("删除", "物资已删除");

    private final String name;
    private final String description;

    MaterialStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}