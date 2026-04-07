package com.yhj.his.module.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限级别枚举
 */
@Getter
@AllArgsConstructor
public enum DataScopeLevel {

    ALL("全院数据", "ALL"),
    DEPARTMENT("科室数据", "DEPARTMENT"),
    PERSONAL("个人数据", "PERSONAL");

    private final String name;
    private final String code;

    public static DataScopeLevel fromCode(String code) {
        for (DataScopeLevel level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
}