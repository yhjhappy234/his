package com.yhj.his.module.pharmacy.vo;

import com.yhj.his.module.pharmacy.enums.InventoryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存信息VO
 */
@Data
@Schema(description = "库存信息响应")
public class InventoryVO {

    @Schema(description = "库存ID")
    private String inventoryId;

    @Schema(description = "药品ID")
    private String drugId;

    @Schema(description = "药品编码")
    private String drugCode;

    @Schema(description = "药品名称")
    private String drugName;

    @Schema(description = "规格")
    private String drugSpec;

    @Schema(description = "单位")
    private String drugUnit;

    @Schema(description = "药房ID")
    private String pharmacyId;

    @Schema(description = "药房名称")
    private String pharmacyName;

    @Schema(description = "批号")
    private String batchNo;

    @Schema(description = "生产日期")
    private LocalDate productionDate;

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

    @Schema(description = "状态")
    private InventoryStatus status;

    @Schema(description = "剩余天数")
    private Integer daysRemaining;

    @Schema(description = "效期预警级别")
    private String alertLevel;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}