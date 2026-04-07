package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 薪资结构创建请求DTO
 */
@Data
@Schema(description = "薪资结构创建请求")
public class SalaryStructureCreateDTO {

    @NotBlank(message = "结构名称不能为空")
    @Schema(description = "结构名称", required = true)
    private String structureName;

    @Schema(description = "结构编码")
    private String structureCode;

    @NotBlank(message = "薪资类型不能为空")
    @Schema(description = "薪资类型", required = true)
    private String salaryType;

    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称", required = true)
    private String itemName;

    @Schema(description = "项目编码")
    private String itemCode;

    @Schema(description = "默认金额")
    private BigDecimal defaultAmount;

    @Schema(description = "最小金额")
    private BigDecimal minAmount;

    @Schema(description = "最大金额")
    private BigDecimal maxAmount;

    @Schema(description = "计算公式")
    private String calcFormula;

    @Schema(description = "是否应税")
    private Boolean taxable;

    @Schema(description = "是否计入社保基数")
    private Boolean insuranceable;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "备注")
    private String remark;
}