package com.yhj.his.module.voice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 语音任务状态枚举
 */
@Getter
@AllArgsConstructor
public enum TaskStatus {

    PENDING("PENDING", "待播报"),
    PLAYING("PLAYING", "播放中"),
    COMPLETED("COMPLETED", "已完成"),
    FAILED("FAILED", "播放失败"),
    CANCELLED("CANCELLED", "已取消"),
    PAUSED("PAUSED", "已暂停");

    private final String code;
    private final String desc;
}