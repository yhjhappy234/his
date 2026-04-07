package com.yhj.his.module.finance.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日结记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "daily_settlement", indexes = {
        @Index(name = "idx_settlement_no", columnList = "settlementNo", unique = true),
        @Index(name = "idx_date_operator", columnList = "settlementDate, operatorId"),
        @Index(name = "idx_status", columnList = "status")
})
@Schema(description = "日结记录")
public class DailySettlement extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "settlement_no", length = 30, nullable = false, unique = true)
    @Schema(description = "日结单号")
    private String settlementNo;

    @Column(name = "settlement_date", nullable = false)
    @Schema(description = "日结日期")
    private LocalDate settlementDate;

    @Column(name = "operator_id", length = 20, nullable = false)
    @Schema(description = "收费员ID")
    private String operatorId;

    @Column(name = "operator_name", length = 50)
    @Schema(description = "收费员姓名")
    private String operatorName;

    @Column(name = "cash_amount", precision = 12, scale = 2)
    @Schema(description = "现金收入")
    private BigDecimal cashAmount = BigDecimal.ZERO;

    @Column(name = "card_amount", precision = 12, scale = 2)
    @Schema(description = "银行卡收入")
    private BigDecimal cardAmount = BigDecimal.ZERO;

    @Column(name = "wechat_amount", precision = 12, scale = 2)
    @Schema(description = "微信收入")
    private BigDecimal wechatAmount = BigDecimal.ZERO;

    @Column(name = "alipay_amount", precision = 12, scale = 2)
    @Schema(description = "支付宝收入")
    private BigDecimal alipayAmount = BigDecimal.ZERO;

    @Column(name = "insurance_amount", precision = 12, scale = 2)
    @Schema(description = "医保收入")
    private BigDecimal insuranceAmount = BigDecimal.ZERO;

    @Column(name = "prepaid_amount", precision = 12, scale = 2)
    @Schema(description = "预交金收入")
    private BigDecimal prepaidAmount = BigDecimal.ZERO;

    @Column(name = "total_income", precision = 12, scale = 2)
    @Schema(description = "总收入")
    private BigDecimal totalIncome = BigDecimal.ZERO;

    @Column(name = "total_refund", precision = 12, scale = 2)
    @Schema(description = "总退费")
    private BigDecimal totalRefund = BigDecimal.ZERO;

    @Column(name = "net_income", precision = 12, scale = 2)
    @Schema(description = "净收入")
    private BigDecimal netIncome = BigDecimal.ZERO;

    @Column(name = "billing_count")
    @Schema(description = "收费笔数")
    private Integer billingCount = 0;

    @Column(name = "refund_count")
    @Schema(description = "退费笔数")
    private Integer refundCount = 0;

    @Column(name = "settlement_time")
    @Schema(description = "日结时间")
    private LocalDateTime settlementTime;

    @Column(name = "confirm_time")
    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;

    @Column(name = "confirmer_id", length = 20)
    @Schema(description = "确认人ID")
    private String confirmerId;

    @Column(name = "confirmer_name", length = 50)
    @Schema(description = "确认人姓名")
    private String confirmerName;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态: PENDING-待确认, CONFIRMED-已确认")
    @Enumerated(EnumType.STRING)
    private DailySettlementStatus status = DailySettlementStatus.PENDING;

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;

    /**
     * 日结状态枚举
     */
    public enum DailySettlementStatus {
        PENDING("待确认"),
        CONFIRMED("已确认");

        private final String description;

        DailySettlementStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}