package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 预交金缴纳请求DTO
 */
@Data
@Schema(description = "预交金缴纳请求")
public class DepositPaymentDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotNull(message = "缴纳金额不能为空")
    @Schema(description = "缴纳金额")
    private BigDecimal amount;

    @NotBlank(message = "缴费方式不能为空")
    @Schema(description = "缴费方式(现金/微信/支付宝/银行卡)")
    private String paymentMethod;

    @Schema(description = "操作员ID")
    private String operatorId;

    @Schema(description = "备注")
    private String remarks;
}