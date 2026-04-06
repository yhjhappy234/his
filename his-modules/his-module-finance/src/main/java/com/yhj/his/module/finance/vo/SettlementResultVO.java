package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 结算结果VO
 */
@Data
@Schema(description = "结算结果视图对象")
public class SettlementResultVO {

    @Schema(description = "收费ID/结算ID")
    private String billingId;

    @Schema(description = "收费单号/结算单号")
    private String billingNo;

    @Schema(description = "发票号")
    private String invoiceNo;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "医保支付金额")
    private BigDecimal insuranceAmount;

    @Schema(description = "自付金额")
    private BigDecimal selfPayAmount;

    @Schema(description = "预交金使用金额")
    private BigDecimal prepaidUsed;

    @Schema(description = "退还金额")
    private BigDecimal refundAmount;

    @Schema(description = "补交金额")
    private BigDecimal supplementAmount;

    @Schema(description = "收费项目ID列表")
    private List<String> itemIds;

    @Schema(description = "支付明细")
    private List<PaymentDetail> payments;

    @Schema(description = "结算时间")
    private LocalDateTime settlementTime;

    @Schema(description = "操作员")
    private String operatorName;

    /**
     * 支付明细
     */
    @Data
    @Schema(description = "支付明细")
    public static class PaymentDetail {

        @Schema(description = "支付方式")
        private String payMethod;

        @Schema(description = "支付方式描述")
        private String payMethodDesc;

        @Schema(description = "支付金额")
        private BigDecimal amount;

        @Schema(description = "支付流水号")
        private String transactionNo;
    }
}