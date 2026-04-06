package com.yhj.his.module.pharmacy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存预警VO
 */
@Data
@Schema(description = "库存预警响应")
public class StockAlertVO {

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

    @Schema(description = "当前库存数量")
    private BigDecimal quantity;

    @Schema(description = "药房ID")
    private String pharmacyId;

    @Schema(description = "药房名称")
    private String pharmacyName;

    @Schema(description = "库存下限")
    private BigDecimal minStock;

    @Schema(description = "库存上限")
    private BigDecimal maxStock;

    @Schema(description = "预警类型")
    private String alertType;

    @Schema(description = "低库存预警列表")
    private List<LowStockAlert> lowStock;

    @Schema(description = "高库存预警列表")
    private List<HighStockAlert> highStock;

    @Data
    @Schema(description = "低库存预警")
    public static class LowStockAlert {

        @Schema(description = "药品ID")
        private String drugId;

        @Schema(description = "药品编码")
        private String drugCode;

        @Schema(description = "药品名称")
        private String drugName;

        @Schema(description = "规格")
        private String drugSpec;

        @Schema(description = "当前库存数量")
        private BigDecimal currentQuantity;

        @Schema(description = "库存下限")
        private BigDecimal minStock;

        @Schema(description = "建议采购数量")
        private BigDecimal suggestedPurchase;
    }

    @Data
    @Schema(description = "高库存预警")
    public static class HighStockAlert {

        @Schema(description = "药品ID")
        private String drugId;

        @Schema(description = "药品编码")
        private String drugCode;

        @Schema(description = "药品名称")
        private String drugName;

        @Schema(description = "规格")
        private String drugSpec;

        @Schema(description = "当前库存数量")
        private BigDecimal currentQuantity;

        @Schema(description = "库存上限")
        private BigDecimal maxStock;

        @Schema(description = "建议减少数量")
        private BigDecimal suggestedReduce;
    }
}