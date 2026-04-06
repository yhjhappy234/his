package com.yhj.his.module.inpatient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 医嘱状态枚举
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {

    NEW("新开", "新开立的医嘱"),
    AUDITED("审核", "已审核"),
    EXECUTING("执行中", "正在执行"),
    STOPPED("已停止", "已停止"),
    COMPLETED("完成", "已完成"),
    CANCELLED("作废", "已作废"),
    REJECTED("驳回", "被驳回");

    private final String code;
    private final String description;
}