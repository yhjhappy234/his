package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 采购申请DTO
 */
@Data
@Schema(description = "采购申请请求")
public class PurchaseApplyDTO {

    @NotBlank(message = "供应商ID不能为空")
    @Schema(description = "供应商ID", required = true)
    private String supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "预计到货日期")
    private LocalDate expectedDate;

    @Schema(description = "申请人ID")
    private String applicantId;

    @Schema(description = "申请人姓名")
    private String applicantName;

    @Schema(description = "采购明细")
    private List<PurchaseItemDTO> items;

    @Data
    @Schema(description = "采购明细")
    public static class PurchaseItemDTO {

        @NotBlank(message = "药品ID不能为空")
        @Schema(description = "药品ID", required = true)
        private String drugId;

        @Schema(description = "药品编码")
        private String drugCode;

        @Schema(description = "药品名称")
        private String drugName;

        @Schema(description = "规格")
        private String drugSpec;

        @Schema(description = "单位")
        private String drugUnit;

        @Schema(description = "采购数量")
        private java.math.BigDecimal quantity;

        @Schema(description = "进价")
        private java.math.BigDecimal purchasePrice;

        @Schema(description = "备注")
        private String remark;
    }
}