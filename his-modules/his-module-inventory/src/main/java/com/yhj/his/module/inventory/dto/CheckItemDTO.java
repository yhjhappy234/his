package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 盘点录入DTO
 */
@Data
@Schema(description = "盘点录入DTO")
public class CheckItemDTO {

    @Schema(description = "盘点明细ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "盘点明细ID不能为空")
    private String itemId;

    @Schema(description = "实盘数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "实盘数量不能为空")
    private BigDecimal actualQuantity;

    @Schema(description = "备注")
    private String remark;
}