package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 绩效指标创建请求DTO
 */
@Data
@Schema(description = "绩效指标创建请求")
public class PerformanceIndicatorCreateDTO {

    @NotBlank(message = "指标名称不能为空")
    @Schema(description = "指标名称", required = true)
    private String indicatorName;

    @Schema(description = "指标编码")
    private String indicatorCode;

    @NotBlank(message = "指标类型不能为空")
    @Schema(description = "指标类型", required = true)
    private String indicatorType;

    @Schema(description = "科室ID(为空表示通用指标)")
    private String deptId;

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

    @Schema(description = "状态(启用/禁用)")
    private String status;

    @Schema(description = "备注")
    private String remark;
}