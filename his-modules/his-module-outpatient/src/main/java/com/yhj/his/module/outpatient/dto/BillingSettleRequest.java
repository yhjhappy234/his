package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收费结算请求DTO
 */
@Data
@Schema(description = "收费结算请求")
public class BillingSettleRequest {

    @NotBlank(message = "挂号ID不能为空")
    @Schema(description = "挂号ID", required = true)
    private String registrationId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @NotEmpty(message = "收费项目不能为空")
    @Schema(description = "收费项目ID列表", required = true)
    private List<String> itemIds;

    @Schema(description = "支付方式列表")
    private List<PaymentDto> payments;

    @Schema(description = "备注")
    private String remark;

    /**
     * 支付信息
     */
    @Data
    @Schema(description = "支付信息")
    public static class PaymentDto {

        @Schema(description = "支付方式: 现金/银行卡/微信/支付宝/医保卡/预交金", required = true)
        private String payMethod;

        @Schema(description = "支付金额", required = true)
        private BigDecimal amount;

        @Schema(description = "医保卡号")
        private String insuranceCard;

        @Schema(description = "交易流水号")
        private String transactionId;
    }
}