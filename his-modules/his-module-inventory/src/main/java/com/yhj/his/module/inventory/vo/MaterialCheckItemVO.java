package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 盘点明细VO
 */
@Data
@Schema(description = "盘点明细VO")
public class MaterialCheckItemVO {

    @Schema(description = "明细ID")
    private String id;

    @Schema(description = "物资ID")
    private String materialId;

    @Schema(description = "物资编码")
    private String materialCode;

    @Schema(description = "物资名称")
    private String materialName;

    @Schema(description = "规格")
    private String materialSpec;

    @Schema(description = "单位")
    private String materialUnit;

    @Schema(description = "批号")
    private String batchNo;

    @Schema(description = "账面数量")
    private BigDecimal bookQuantity;

    @Schema(description = "实盘数量")
    private BigDecimal actualQuantity;

    @Schema(description = "差异数量")
    private BigDecimal diffQuantity;

    @Schema(description = "进价")
    private BigDecimal purchasePrice;

    @Schema(description = "零售价")
    private BigDecimal retailPrice;

    @Schema(description = "差异金额")
    private BigDecimal diffAmount;

    @Schema(description = "差异类型")
    private String diffType;

    @Schema(description = "调整状态")
    private Boolean adjusted;

    @Schema(description = "备注")
    private String remark;
}