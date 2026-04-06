package com.yhj.his.module.pacs.enums;

import lombok.Getter;

/**
 * 检查记录状态枚举
 */
@Getter
public enum ExamRecordStatus {

    REGISTERED("已登记", "患者已登记"),
    IN_PROGRESS("检查中", "正在进行检查"),
    COMPLETED("检查完成", "检查已完成"),
    CANCELLED("已取消", "检查已取消");

    private final String name;
    private final String description;

    ExamRecordStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}