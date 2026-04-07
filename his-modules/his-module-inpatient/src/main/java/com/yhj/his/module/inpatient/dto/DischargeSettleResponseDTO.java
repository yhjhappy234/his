package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出院结算响应DTO
 */
@Data
@Schema(description = "出院结算响应")
public class DischargeSettleResponseDTO {

    @Schema(description = "结算ID")
    private String settleId;

    @Schema(description = "发票号")
    private String invoiceNo;

    @Schema(description = "住院天数")
    private Integer totalDays;

    @Schema(description = "总费用")
    private BigDecimal totalCost;

    @Schema(description = "使用预交金")
    private BigDecimal depositUsed;

    @Schema(description = "医保支付")
    private BigDecimal insurancePayment;

    @Schema(description = "自付金额")
    private BigDecimal selfPayment;

    @Schema(description = "退费金额")
    private BigDecimal refund;
}