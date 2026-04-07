package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入库明细VO
 */
@Data
@Schema(description = "入库明细VO")
public class MaterialInboundItemVO {

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

    @Schema(description = "生产日期")
    private LocalDate productionDate;

    @Schema(description = "有效期")
    private LocalDate expiryDate;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "进价")
    private BigDecimal purchasePrice;

    @Schema(description = "零售价")
    private BigDecimal retailPrice;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "库位")
    private String location;

    @Schema(description = "备注")
    private String remark;
}