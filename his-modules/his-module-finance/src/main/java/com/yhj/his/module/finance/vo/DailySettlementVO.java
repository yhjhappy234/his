package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日结VO
 */
@Data
@Schema(description = "日结视图对象")
public class DailySettlementVO {

    @Schema(description = "日结ID")
    private String id;

    @Schema(description = "日结单号")
    private String settlementNo;

    @Schema(description = "日结日期")
    private LocalDate settlementDate;

    @Schema(description = "收费员ID")
    private String operatorId;

    @Schema(description = "收费员姓名")
    private String operatorName;

    @Schema(description = "现金收入")
    private BigDecimal cashAmount;

    @Schema(description = "银行卡收入")
    private BigDecimal cardAmount;

    @Schema(description = "微信收入")
    private BigDecimal wechatAmount;

    @Schema(description = "支付宝收入")
    private BigDecimal alipayAmount;

    @Schema(description = "医保收入")
    private BigDecimal insuranceAmount;

    @Schema(description = "预交金收入")
    private BigDecimal prepaidAmount;

    @Schema(description = "总收入")
    private BigDecimal totalIncome;

    @Schema(description = "总退费")
    private BigDecimal totalRefund;

    @Schema(description = "净收入")
    private BigDecimal netIncome;

    @Schema(description = "收费笔数")
    private Integer billingCount;

    @Schema(description = "退费笔数")
    private Integer refundCount;

    @Schema(description = "日结时间")
    private LocalDateTime settlementTime;

    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;

    @Schema(description = "确认人ID")
    private String confirmerId;

    @Schema(description = "确认人姓名")
    private String confirmerName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}