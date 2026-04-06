package com.yhj.his.module.voice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备状态枚举
 */
@Getter
@AllArgsConstructor
public enum DeviceStatus {

    ONLINE("ONLINE", "在线"),
    OFFLINE("OFFLINE", "离线"),
    BUSY("BUSY", "忙碌"),
    ERROR("ERROR", "故障");

    private final String code;
    private final String desc;
}