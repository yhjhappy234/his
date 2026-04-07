package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "门诊计费项目VO")
public class OutpatientBillingItemVO {

    @Schema(description = "项目ID")
    private String id;

    @Schema(description = "计费ID")
    private String billingId;

    @Schema(description = "项目ID")
    private String itemId;

    @Schema(description = "项目类型")
    private String itemType;

    @Schema(description = "项目名称")
    private String itemName;

    @Schema(description = "项目编码")
    private String itemCode;

    @Schema(description = "项目分类")
    private String itemCategory;

    @Schema(description = "项目分类描述")
    private String itemCategoryDesc;

    @Schema(description = "规格")
    private String spec;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "项目单位")
    private String itemUnit;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "单价")
    private BigDecimal unitPrice;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "医保金额")
    private BigDecimal insuranceAmount;

    @Schema(description = "自付金额")
    private BigDecimal selfPayAmount;

    @Schema(description = "退费金额")
    private BigDecimal refundAmount;

    @Schema(description = "处方ID")
    private String prescriptionId;

    @Schema(description = "申请ID")
    private String requestId;

    @Schema(description = "执行科室ID")
    private String execDeptId;

    @Schema(description = "执行科室名称")
    private String execDeptName;

    @Schema(description = "状态")
    private String status;
}
