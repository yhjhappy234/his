package com.yhj.his.module.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 门诊收费结算DTO
 */
@Data
@Schema(description = "门诊收费结算请求")
public class OutpatientSettleDTO {

    @NotBlank(message = "就诊ID不能为空")
    @Schema(description = "就诊ID")
    private String visitId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "就诊序号")
    private String visitNo;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "医保卡号")
    private String insuranceCardNo;

    @NotNull(message = "支付明细不能为空")
    @Schema(description = "支付明细")
    private List<PaymentDTO> payments;

    /**
     * 支付明细DTO
     */
    @Data
    @Schema(description = "支付明细")
    public static class PaymentDTO {

        @NotBlank(message = "支付方式不能为空")
        @Schema(description = "支付方式: CASH, CARD, WECHAT, ALIPAY, MEDICAL_INSURANCE, PREPAID")
        private String payMethod;

        @NotNull(message = "支付金额不能为空")
        @DecimalMin(value = "0.01", message = "支付金额必须大于0")
        @Schema(description = "支付金额")
        private BigDecimal amount;
    }
}