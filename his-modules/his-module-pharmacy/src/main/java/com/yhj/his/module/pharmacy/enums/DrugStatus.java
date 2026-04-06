package com.yhj.his.module.pharmacy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 药品状态枚举
 */
@Getter
@AllArgsConstructor
public enum DrugStatus {

    NORMAL("正常", "正常使用"),
    DISABLED("停用", "已停用");

    private final String name;
    private final String description;
}