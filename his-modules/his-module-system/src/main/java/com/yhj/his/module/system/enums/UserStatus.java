package com.yhj.his.module.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserStatus {

    NORMAL("正常", "NORMAL"),
    DISABLED("停用", "DISABLED"),
    LOCKED("锁定", "LOCKED");

    private final String name;
    private final String code;

    public static UserStatus fromCode(String code) {
        for (UserStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}