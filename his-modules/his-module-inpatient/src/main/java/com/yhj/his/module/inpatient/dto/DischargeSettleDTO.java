package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出院结算请求DTO
 */
@Data
@Schema(description = "出院结算请求")
public class DischargeSettleDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "支付明细")
    private List<PaymentDetail> payments;

    @Schema(description = "操作员ID")
    private String operatorId;

    /**
     * 支付明细
     */
    @Data
    @Schema(description = "支付明细")
    public static class PaymentDetail {
        @Schema(description = "支付方式(MEDICAL_INSURANCE/WECHAT/ALIPAY/CASH/BANK_CARD)")
        private String payMethod;

        @Schema(description = "支付金额")
        private BigDecimal amount;
    }
}