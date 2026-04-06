package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 预交金缴纳响应DTO
 */
@Data
@Schema(description = "预交金缴纳响应")
public class DepositPaymentResponseDTO {

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "住院号")
    private String admissionNo;

    @Schema(description = "缴纳金额")
    private BigDecimal amount;

    @Schema(description = "当前预交金总额")
    private BigDecimal totalDeposit;

    @Schema(description = "缴费时间")
    private String paymentTime;

    @Schema(description = "缴费方式")
    private String paymentMethod;
}