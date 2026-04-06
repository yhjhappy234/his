package com.yhj.his.module.finance.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 住院预交金实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "prepayment", indexes = {
        @Index(name = "idx_prepayment_no", columnList = "prepaymentNo", unique = true),
        @Index(name = "idx_admission_id", columnList = "admissionId"),
        @Index(name = "idx_patient_id", columnList = "patientId"),
        @Index(name = "idx_operate_time", columnList = "operateTime")
})
@Schema(description = "住院预交金")
public class Prepayment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "prepayment_no", length = 30, nullable = false, unique = true)
    @Schema(description = "预交金单号")
    private String prepaymentNo;

    @Column(name = "receipt_no", length = 30)
    @Schema(description = "收据号")
    private String receiptNo;

    @Column(name = "admission_id", length = 36, nullable = false)
    @Schema(description = "住院ID")
    private String admissionId;

    @Column(name = "patient_id", length = 20, nullable = false)
    @Schema(description = "患者ID")
    private String patientId;

    @Column(name = "patient_name", length = 50)
    @Schema(description = "患者姓名")
    private String patientName;

    @Column(name = "deposit_type", length = 20, nullable = false)
    @Schema(description = "类型: DEPOSIT-缴纳, REFUND-退还")
    @Enumerated(EnumType.STRING)
    private DepositType depositType;

    @Column(name = "deposit_amount", precision = 12, scale = 2, nullable = false)
    @Schema(description = "金额")
    private BigDecimal depositAmount;

    @Column(name = "payment_method", length = 20)
    @Schema(description = "支付方式: CASH-现金, CARD-银行卡, WECHAT-微信, ALIPAY-支付宝")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "balance_before", precision = 12, scale = 2)
    @Schema(description = "操作前余额")
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 12, scale = 2)
    @Schema(description = "操作后余额")
    private BigDecimal balanceAfter;

    @Column(name = "operator_id", length = 20)
    @Schema(description = "操作员ID")
    private String operatorId;

    @Column(name = "operator_name", length = 50)
    @Schema(description = "操作员姓名")
    private String operatorName;

    @Column(name = "operate_time", nullable = false)
    @Schema(description = "操作时间")
    private LocalDateTime operateTime;

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态: NORMAL-正常, CANCELLED-已作废")
    @Enumerated(EnumType.STRING)
    private PrepaymentStatus status = PrepaymentStatus.NORMAL;

    /**
     * 预交金类型枚举
     */
    public enum DepositType {
        DEPOSIT("缴纳"),
        REFUND("退还");

        private final String description;

        DepositType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 支付方式枚举
     */
    public enum PaymentMethod {
        CASH("现金"),
        CARD("银行卡"),
        WECHAT("微信支付"),
        ALIPAY("支付宝"),
        MEDICAL_INSURANCE("医保"),
        PREPAID("预交金"),
        MIXED("混合支付");

        private final String description;

        PaymentMethod(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 预交金状态枚举
     */
    public enum PrepaymentStatus {
        NORMAL("正常"),
        CANCELLED("已作废");

        private final String description;

        PrepaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}