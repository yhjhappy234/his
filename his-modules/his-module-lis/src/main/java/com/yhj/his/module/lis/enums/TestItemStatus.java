package com.yhj.his.module.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 检验项目状态枚举
 */
@Getter
@AllArgsConstructor
public enum TestItemStatus {

    NORMAL("正常", "项目正常使用"),
    DISABLED("停用", "项目已停用");

    private final String code;
    private final String description;
}