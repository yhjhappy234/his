package com.yhj.his.module.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 收费项目创建DTO
 */
@Data
@Schema(description = "收费项目创建请求")
public class PriceItemCreateDTO {

    @NotBlank(message = "项目编码不能为空")
    @Schema(description = "项目编码")
    private String itemCode;

    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称")
    private String itemName;

    @NotNull(message = "项目分类不能为空")
    @Schema(description = "项目分类: DRUG, EXAM, TEST, TREATMENT, BED, NURSING, MATERIAL, OTHER")
    private String itemCategory;

    @Schema(description = "单位")
    private String itemUnit;

    @Schema(description = "规格")
    private String itemSpec;

    @Schema(description = "标准价格")
    @DecimalMin(value = "0", message = "标准价格不能为负数")
    private BigDecimal standardPrice;

    @NotNull(message = "零售价格不能为空")
    @DecimalMin(value = "0", message = "零售价格不能为负数")
    @Schema(description = "零售价格")
    private BigDecimal retailPrice;

    @Schema(description = "批发价格")
    @DecimalMin(value = "0", message = "批发价格不能为负数")
    private BigDecimal wholesalePrice;

    @Schema(description = "医保类型: A, B, C, SELF")
    private String insuranceType;

    @Schema(description = "医保编码")
    private String insuranceCode;

    @Schema(description = "医保价格")
    @DecimalMin(value = "0", message = "医保价格不能为负数")
    private BigDecimal insurancePrice;

    @Schema(description = "报销比例(0-100)")
    @DecimalMin(value = "0", message = "报销比例不能为负数")
    private BigDecimal reimbursementRatio;

    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    @Schema(description = "失效日期")
    private LocalDate expireDate;

    @Schema(description = "版本号")
    private String versionNo;

    @Schema(description = "备注")
    private String remark;
}