package com.yhj.his.module.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 预交金缴纳DTO
 */
@Data
@Schema(description = "预交金缴纳请求")
public class PrepaymentDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Schema(description = "缴纳金额")
    private BigDecimal depositAmount;

    @NotBlank(message = "支付方式不能为空")
    @Schema(description = "支付方式: CASH, CARD, WECHAT, ALIPAY")
    private String paymentMethod;

    @Schema(description = "备注")
    private String remark;
}