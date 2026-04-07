package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 物资库存VO
 */
@Data
@Schema(description = "物资库存VO")
public class MaterialInventoryVO {

    @Schema(description = "库存ID")
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

    @Schema(description = "库房ID")
    private String warehouseId;

    @Schema(description = "库房名称")
    private String warehouseName;

    @Schema(description = "批号")
    private String batchNo;

    @Schema(description = "有效期")
    private LocalDate expiryDate;

    @Schema(description = "库存数量")
    private BigDecimal quantity;

    @Schema(description = "锁定数量")
    private BigDecimal lockedQuantity;

    @Schema(description = "可用数量")
    private BigDecimal availableQuantity;

    @Schema(description = "库位")
    private String location;

    @Schema(description = "进价")
    private BigDecimal purchasePrice;

    @Schema(description = "零售价")
    private BigDecimal retailPrice;

    @Schema(description = "供应商ID")
    private String supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "入库时间")
    private LocalDateTime inboundTime;

    @Schema(description = "状态")
    private Integer status;
}