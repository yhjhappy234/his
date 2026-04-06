package com.yhj.his.module.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审计级别枚举
 */
@Getter
@AllArgsConstructor
public enum AuditLevel {

    NORMAL("正常", "NORMAL"),
    WARNING("警告", "WARNING"),
    CRITICAL("严重", "CRITICAL");

    private final String name;
    private final String code;

    public static AuditLevel fromCode(String code) {
        for (AuditLevel level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
}