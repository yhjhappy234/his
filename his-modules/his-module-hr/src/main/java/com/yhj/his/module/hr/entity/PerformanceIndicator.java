package com.yhj.his.module.hr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.hr.enums.IndicatorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 绩效指标实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_performance_indicator", indexes = {
    @Index(name = "idx_indicator_type", columnList = "indicator_type"),
    @Index(name = "idx_indicator_dept", columnList = "dept_id")
})
@Schema(description = "绩效指标")
public class PerformanceIndicator extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "indicator_name", length = 50, nullable = false)
    @Schema(description = "指标名称")
    private String indicatorName;

    @Column(name = "indicator_code", length = 20, unique = true)
    @Schema(description = "指标编码")
    private String indicatorCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "indicator_type", length = 20, nullable = false)
    @Schema(description = "指标类型")
    private IndicatorType indicatorType;

    @Column(name = "dept_id", length = 36)
    @Schema(description = "科室ID(为空表示通用指标)")
    private String deptId;

    @Column(name = "dept_name", length = 100)
    @Schema(description = "科室名称")
    private String deptName;

    @Column(name = "unit", length = 20)
    @Schema(description = "计量单位")
    private String unit;

    @Column(name = "max_score", precision = 6, scale = 2)
    @Schema(description = "最高分值")
    private BigDecimal maxScore;

    @Column(name = "weight", precision = 5, scale = 2)
    @Schema(description = "权重(百分比)")
    private BigDecimal weight;

    @Column(name = "target_value", precision = 10, scale = 2)
    @Schema(description = "目标值")
    private BigDecimal targetValue;

    @Column(name = "description", length = 500)
    @Schema(description = "指标描述")
    private String description;

    @Column(name = "calc_rule", length = 500)
    @Schema(description = "计算规则")
    private String calcRule;

    @Column(name = "sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态(启用/禁用)")
    private String status = "启用";

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;
}