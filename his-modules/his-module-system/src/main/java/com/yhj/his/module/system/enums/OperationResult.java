package com.yhj.his.module.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作结果枚举
 */
@Getter
@AllArgsConstructor
public enum OperationResult {

    SUCCESS("成功", "SUCCESS"),
    FAILURE("失败", "FAILURE");

    private final String name;
    private final String code;

    public static OperationResult fromCode(String code) {
        for (OperationResult result : values()) {
            if (result.getCode().equals(code)) {
                return result;
            }
        }
        return null;
    }
}