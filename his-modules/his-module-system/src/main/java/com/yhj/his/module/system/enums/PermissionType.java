package com.yhj.his.module.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限类型枚举
 */
@Getter
@AllArgsConstructor
public enum PermissionType {

    MENU("菜单权限", "MENU"),
    FUNCTION("功能权限", "FUNCTION"),
    DATA("数据权限", "DATA"),
    API("接口权限", "API");

    private final String name;
    private final String code;

    public static PermissionType fromCode(String code) {
        for (PermissionType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}