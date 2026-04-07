package com.yhj.his.module.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付明细DTO
 */
@Data
@Schema(description = "支付明细")
public class PaymentDTO {

    @Schema(description = "支付方式: CASH, CARD, WECHAT, ALIPAY, MEDICAL_INSURANCE, PREPAID")
    private String payMethod;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "支付流水号")
    private String transactionNo;
}