package com.yhj.his.module.pharmacy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 库存汇总VO
 */
@Data
@Schema(description = "库存汇总响应")
public class InventorySummaryVO {

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

    @Schema(description = "总库存数量")
    private BigDecimal totalQuantity;

    @Schema(description = "总可用数量")
    private BigDecimal totalAvailableQuantity;

    @Schema(description = "总锁定数量")
    private BigDecimal totalLockedQuantity;

    @Schema(description = "库存下限")
    private BigDecimal minStock;

    @Schema(description = "库存上限")
    private BigDecimal maxStock;

    @Schema(description = "批次列表")
    private java.util.List<BatchVO> batches;

    @Data
    @Schema(description = "批次信息")
    public static class BatchVO {

        @Schema(description = "库存ID")
        private String inventoryId;

        @Schema(description = "批号")
        private String batchNo;

        @Schema(description = "数量")
        private BigDecimal quantity;

        @Schema(description = "可用数量")
        private BigDecimal availableQuantity;

        @Schema(description = "有效期")
        private LocalDate expiryDate;

        @Schema(description = "库位")
        private String location;

        @Schema(description = "剩余天数")
        private Integer daysRemaining;

        @Schema(description = "预警级别")
        private String alertLevel;
    }
}