package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 薪资结构VO
 */
@Data
@Schema(description = "薪资结构信息")
public class SalaryStructureVO {

    @Schema(description = "薪资结构ID")
    private String id;

    @Schema(description = "结构名称")
    private String structureName;

    @Schema(description = "结构编码")
    private String structureCode;

    @Schema(description = "薪资类型")
    private String salaryType;

    @Schema(description = "项目名称")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}