package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 绩效指标VO
 */
@Data
@Schema(description = "绩效指标信息")
public class PerformanceIndicatorVO {

    @Schema(description = "指标ID")
    private String id;

    @Schema(description = "指标名称")
    private String indicatorName;

    @Schema(description = "指标编码")
    private String indicatorCode;

    @Schema(description = "指标类型")
    private String indicatorType;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "最高分值")
    private BigDecimal maxScore;

    @Schema(description = "权重(百分比)")
    private BigDecimal weight;

    @Schema(description = "目标值")
    private BigDecimal targetValue;

    @Schema(description = "指标描述")
    private String description;

    @Schema(description = "计算规则")
    private String calcRule;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}