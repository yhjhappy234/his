package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出库明细VO
 */
@Data
@Schema(description = "出库明细VO")
public class MaterialOutboundItemVO {

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

    @Schema(description = "申请数量")
    private BigDecimal applyQuantity;

    @Schema(description = "实际出库数量")
    private BigDecimal quantity;

    @Schema(description = "进价")
    private BigDecimal purchasePrice;

    @Schema(description = "零售价")
    private BigDecimal retailPrice;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;
}