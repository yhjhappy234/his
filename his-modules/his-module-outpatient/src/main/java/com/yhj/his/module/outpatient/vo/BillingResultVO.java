package com.yhj.his.module.outpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 收费结算结果VO
 */
@Data
@Schema(description = "收费结算结果")
public class BillingResultVO {

    @Schema(description = "结算单ID")
    private String billId;

    @Schema(description = "发票号")
    private String invoiceNo;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "医保支付金额")
    private BigDecimal insuranceAmount;

    @Schema(description = "自费支付金额")
    private BigDecimal selfPayAmount;

    @Schema(description = "实收金额")
    private BigDecimal actualAmount;

    @Schema(description = "发票URL")
    private String invoiceUrl;
}