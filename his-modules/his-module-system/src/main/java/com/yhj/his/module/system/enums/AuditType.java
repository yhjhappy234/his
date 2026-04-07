package com.yhj.his.module.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审计类型枚举
 */
@Getter
@AllArgsConstructor
public enum AuditType {

    LOGIN("登录审计", "LOGIN"),
    PERMISSION("权限审计", "PERMISSION"),
    DATA("数据审计", "DATA"),
    SYSTEM("系统审计", "SYSTEM"),
    SECURITY("安全审计", "SECURITY");

    private final String name;
    private final String code;

    public static AuditType fromCode(String code) {
        for (AuditType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}