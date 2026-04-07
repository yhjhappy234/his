package com.yhj.his.module.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 价格调整DTO
 */
@Data
@Schema(description = "价格调整请求")
public class PriceAdjustDTO {

    @Schema(description = "收费项目ID")
    private String itemId;

    @Schema(description = "项目编码")
    private String itemCode;

    @Schema(description = "原价格")
    private BigDecimal oldPrice;

    @Schema(description = "新价格")
    private BigDecimal newPrice;

    @Schema(description = "调价原因")
    private String reason;

    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    @Schema(description = "调整前版本号")
    private String oldVersionNo;

    @Schema(description = "调整后版本号")
    private String newVersionNo;
}