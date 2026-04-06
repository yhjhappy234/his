package com.yhj.his.module.pacs.enums;

import lombok.Getter;

/**
 * 设备状态枚举
 */
@Getter
public enum EquipmentStatus {

    NORMAL("正常", "设备正常运行"),
    MAINTENANCE("维护中", "设备正在维护"),
    FAULT("故障", "设备故障"),
    OFFLINE("离线", "设备离线");

    private final String name;
    private final String description;

    EquipmentStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}