package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出库明细DTO
 */
@Data
@Schema(description = "出库明细DTO")
public class OutboundItemDTO {

    @Schema(description = "物资ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "物资ID不能为空")
    private String materialId;

    @Schema(description = "物资编码")
    private String materialCode;

    @Schema(description = "物资名称")
    private String materialName;

    @Schema(description = "规格")
    private String materialSpec;

    @Schema(description = "单位")
    private String materialUnit;

    @Schema(description = "申请数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "申请数量不能为空")
    @DecimalMin(value = "0.01", message = "数量必须大于0")
    private BigDecimal applyQuantity;

    @Schema(description = "备注")
    private String remark;
}