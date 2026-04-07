package com.yhj.his.module.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录方式枚举
 */
@Getter
@AllArgsConstructor
public enum LoginType {

    PASSWORD("密码登录", "PASSWORD"),
    SMS("短信验证码登录", "SMS"),
    FINGERPRINT("指纹登录", "FINGERPRINT"),
    FACE("人脸识别登录", "FACE"),
    QR_CODE("二维码登录", "QR_CODE");

    private final String name;
    private final String code;

    public static LoginType fromCode(String code) {
        for (LoginType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}