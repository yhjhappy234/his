package com.yhj.his.module.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 医保政策创建DTO
 */
@Data
@Schema(description = "医保政策创建请求")
public class InsurancePolicyCreateDTO {

    @NotBlank(message = "政策名称不能为空")
    @Schema(description = "政策名称")
    private String policyName;

    @NotBlank(message = "医保类型不能为空")
    @Schema(description = "医保类型: URBAN_EMPLOYEE, URBAN_RESIDENT, NEW_RURAL, PUBLIC, COMMERCIAL, SELF")
    private String insuranceType;

    @Schema(description = "起付线")
    @DecimalMin(value = "0", message = "起付线不能为负数")
    private BigDecimal deductibleLine;

    @Schema(description = "封顶线")
    @DecimalMin(value = "0", message = "封顶线不能为负数")
    private BigDecimal capLine;

    @Schema(description = "甲类报销比例(0-100)")
    @DecimalMin(value = "0", message = "报销比例不能为负数")
    private BigDecimal classARatio;

    @Schema(description = "乙类报销比例(0-100)")
    @DecimalMin(value = "0", message = "报销比例不能为负数")
    private BigDecimal classBRatio;

    @Schema(description = "丙类报销比例(0-100)")
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
}