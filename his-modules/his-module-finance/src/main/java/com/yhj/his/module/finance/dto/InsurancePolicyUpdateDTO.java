package com.yhj.his.module.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 医保政策更新DTO
 */
@Data
@Schema(description = "医保政策更新请求")
public class InsurancePolicyUpdateDTO {

    @NotNull(message = "政策ID不能为空")
    @Schema(description = "政策ID")
    private String id;

    @Schema(description = "政策名称")
    private String policyName;

    @Schema(description = "起付线")
    @DecimalMin(value = "0", message = "起付线不能为负数")
    private BigDecimal deductibleLine;

    @Schema(description = "封顶线")
    @DecimalMin(value = "0", message = "封顶线不能为负数")
    private BigDecimal capLine;

    @Schema(description = "甲类报销比例")
    @DecimalMin(value = "0", message = "报销比例不能为负数")
    private BigDecimal classARatio;

    @Schema(description = "乙类报销比例")
    @DecimalMin(value = "0", message = "报销比例不能为负数")
    private BigDecimal classBRatio;

    @Schema(description = "丙类报销比例")
    @DecimalMin(value = "0", message = "报销比例不能为负数")
    private BigDecimal classCRatio;

    @Schema(description = "门诊报销比例")
    @DecimalMin(value = "0", message = "报销比例不能为负数")
    private BigDecimal outpatientRatio;

    @Schema(description = "住院报销比例")
    @DecimalMin(value = "0", message = "报销比例不能为负数")
    private BigDecimal inpatientRatio;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "状态")
    private String status;
}