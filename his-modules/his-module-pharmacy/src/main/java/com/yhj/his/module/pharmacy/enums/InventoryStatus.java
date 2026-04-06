package com.yhj.his.module.pharmacy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存状态枚举
 */
@Getter
@AllArgsConstructor
public enum InventoryStatus {

    NORMAL("正常", "正常使用"),
    EXPIRED("过期", "已过期"),
    DISABLED("停用", "已停用");

    private final String name;
    private final String description;
}