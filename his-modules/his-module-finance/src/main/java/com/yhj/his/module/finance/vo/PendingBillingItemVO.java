package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "待结算项目VO")
public class PendingBillingItemVO {

    @Schema(description = "项目ID")
    private String itemId;

    @Schema(description = "项目类型")
    private String itemType;

    @Schema(description = "项目名称")
    private String itemName;

    @Schema(description = "项目编码")
    private String itemCode;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "单价")
    private BigDecimal unitPrice;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "医生ID")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "创建时间")
    private String createTime;
}
