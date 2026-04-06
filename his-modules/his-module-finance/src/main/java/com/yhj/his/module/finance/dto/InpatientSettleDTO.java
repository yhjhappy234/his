package com.yhj.his.module.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 住院结算DTO
 */
@Data
@Schema(description = "住院结算请求")
public class InpatientSettleDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "医保卡号")
    private String insuranceCardNo;

    @Schema(description = "支付明细")
    private List<PaymentDTO> payments;

    /**
     * 支付明细DTO
     */
    @Data
    @Schema(description = "支付明细")
    public static class PaymentDTO {

        @Schema(description = "支付方式")
        private String payMethod;

        @Schema(description = "支付金额")
        private BigDecimal amount;
    }
}