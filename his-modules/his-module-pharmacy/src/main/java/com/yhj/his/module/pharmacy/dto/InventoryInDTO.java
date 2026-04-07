package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 库存入库DTO
 */
@Data
@Schema(description = "库存入库请求")
public class InventoryInDTO {

    @NotBlank(message = "药品ID不能为空")
    @Schema(description = "药品ID", required = true)
    private String drugId;

    @NotBlank(message = "药房ID不能为空")
    @Schema(description = "药房ID", required = true)
    private String pharmacyId;

    @NotBlank(message = "批号不能为空")
    @Schema(description = "批号", required = true)
    private String batchNo;

    @Schema(description = "生产日期")
    private LocalDate productionDate;

    @NotNull(message = "有效期不能为空")
    @Schema(description = "有效期", required = true)
    private LocalDate expiryDate;

    @NotNull(message = "入库数量不能为空")
    @Schema(description = "入库数量", required = true)
    private BigDecimal quantity;

    @Schema(description = "进价")
    private BigDecimal purchasePrice;

    @Schema(description = "零售价")
    private BigDecimal retailPrice;

    @Schema(description = "库位")
    private String location;

    @Schema(description = "供应商ID")
    private String supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "关联单据ID")
    private String relatedId;

    @Schema(description = "关联单据号")
    private String relatedNo;

    @Schema(description = "备注")
    private String remark;
}