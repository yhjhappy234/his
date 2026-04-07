package com.yhj.his.module.hr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 绩效指标类型枚举
 */
@Getter
@AllArgsConstructor
public enum IndicatorType {

    WORKLOAD("工作量指标", "工作数量统计"),
    QUALITY("质量指标", "工作质量评价"),
    EFFICIENCY("效率指标", "工作效率统计"),
    SERVICE("服务指标", "患者满意度"),
    ATTENDANCE("考勤指标", "考勤表现统计"),
    RESEARCH("科研指标", "科研成果统计"),
    TEACHING("教学指标", "教学工作统计");

    private final String name;
    private final String description;
}