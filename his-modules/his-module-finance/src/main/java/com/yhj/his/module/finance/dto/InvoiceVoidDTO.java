package com.yhj.his.module.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发票作废DTO
 */
@Data
@Schema(description = "发票作废请求")
public class InvoiceVoidDTO {

    @NotBlank(message = "发票号不能为空")
    @Schema(description = "发票号")
    private String invoiceNo;

    @NotBlank(message = "作废原因不能为空")
    @Schema(description = "作废原因")
    private String voidReason;
}