package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 发票VO
 */
@Data
@Schema(description = "发票视图对象")
public class InvoiceVO {

    @Schema(description = "发票ID")
    private String id;

    @Schema(description = "发票号码")
    private String invoiceNo;

    @Schema(description = "发票代码")
    private String invoiceCode;

    @Schema(description = "关联收费ID")
    private String billingId;

    @Schema(description = "收费类型")
    private String billingType;

    @Schema(description = "收费类型描述")
    private String billingTypeDesc;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "开票日期")
    private LocalDate invoiceDate;

    @Schema(description = "开票时间")
    private LocalDateTime invoiceTime;

    @Schema(description = "合计金额")
    private BigDecimal totalAmount;

    @Schema(description = "医保支付")
    private BigDecimal insuranceAmount;

    @Schema(description = "个人支付")
    private BigDecimal selfPayAmount;

    @Schema(description = "发票类型")
    private String invoiceType;

    @Schema(description = "发票类型描述")
    private String invoiceTypeDesc;

    @Schema(description = "打印次数")
    private Integer printCount;

    @Schema(description = "最后打印时间")
    private LocalDateTime lastPrintTime;

    @Schema(description = "开票员ID")
    private String operatorId;

    @Schema(description = "开票员姓名")
    private String operatorName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "作废时间")
    private LocalDateTime voidTime;

    @Schema(description = "作废原因")
    private String voidReason;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}