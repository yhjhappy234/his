package com.yhj.his.module.voice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 播放结果枚举
 */
@Getter
@AllArgsConstructor
public enum PlayResult {

    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    TIMEOUT("TIMEOUT", "超时"),
    INTERRUPTED("INTERRUPTED", "被打断");

    private final String code;
    private final String desc;
}