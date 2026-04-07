package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 床位状态枚举
 */
@Getter
@AllArgsConstructor
public enum BedStatus {

    VACANT("空床", "可分配"),
    OCCUPIED("占用", "有患者"),
    RESERVED("预留", "已预约未入住"),
    MAINTENANCE("维修", "暂停使用"),
    ISOLATION("隔离", "感染控制"),
    CLEANING("打扫", "准备中");

    private final String code;
    private final String description;
}