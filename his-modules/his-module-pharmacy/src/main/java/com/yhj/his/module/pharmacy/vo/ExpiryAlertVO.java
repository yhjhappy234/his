package com.yhj.his.module.pharmacy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 效期预警VO
 */
@Data
@Schema(description = "效期预警响应")
public class ExpiryAlertVO {

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

    @Schema(description = "批号")
    private String batchNo;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "有效期")
    private LocalDate expiryDate;

    @Schema(description = "剩余天数")
    private Integer daysRemaining;

    @Schema(description = "预警级别")
    private String alertLevel;

    @Schema(description = "预警颜色")
    private String alertColor;

    @Schema(description = "药房ID")
    private String pharmacyId;

    @Schema(description = "药房名称")
    private String pharmacyName;

    @Schema(description = "库位")
    private String location;

    @Schema(description = "供应商名称")
    private String supplierName;
}