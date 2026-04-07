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
 * 发票实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "invoice", indexes = {
        @Index(name = "idx_invoice_no", columnList = "invoiceNo", unique = true),
        @Index(name = "idx_billing_id", columnList = "billingId"),
        @Index(name = "idx_patient_id", columnList = "patientId"),
        @Index(name = "idx_invoice_date", columnList = "invoiceDate")
})
@Schema(description = "发票")
public class Invoice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "invoice_no", length = 30, nullable = false, unique = true)
    @Schema(description = "发票号码")
    private String invoiceNo;

    @Column(name = "invoice_code", length = 20)
    @Schema(description = "发票代码")
    private String invoiceCode;

    @Column(name = "billing_id", length = 36)
    @Schema(description = "关联收费ID(门诊收费ID或住院结算ID)")
    private String billingId;

    @Column(name = "billing_type", length = 20, nullable = false)
    @Schema(description = "收费类型: OUTPATIENT-门诊, INPATIENT-住院, PREPAYMENT-预交金")
    @Enumerated(EnumType.STRING)
    private BillingType billingType;

    @Column(name = "patient_id", length = 20, nullable = false)
    @Schema(description = "患者ID")
    private String patientId;

    @Column(name = "patient_name", length = 50)
    @Schema(description = "患者姓名")
    private String patientName;

    @Column(name = "invoice_date", nullable = false)
    @Schema(description = "开票日期")
    private LocalDate invoiceDate;

    @Column(name = "invoice_time", nullable = false)
    @Schema(description = "开票时间")
    private LocalDateTime invoiceTime;

    @Column(name = "total_amount", precision = 12, scale = 2)
    @Schema(description = "合计金额")
    private BigDecimal totalAmount;

    @Column(name = "insurance_amount", precision = 12, scale = 2)
    @Schema(description = "医保支付")
    private BigDecimal insuranceAmount;

    @Column(name = "self_pay_amount", precision = 12, scale = 2)
    @Schema(description = "个人支付")
    private BigDecimal selfPayAmount;

    @Column(name = "invoice_type", length = 20, nullable = false)
    @Schema(description = "发票类型: MEDICAL-医疗收费发票, PREPAYMENT-预交金收据, SETTLEMENT-结算清单, ELECTRONIC-电子发票")
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;

    @Column(name = "print_count")
    @Schema(description = "打印次数")
    private Integer printCount = 0;

    @Column(name = "last_print_time")
    @Schema(description = "最后打印时间")
    private LocalDateTime lastPrintTime;

    @Column(name = "operator_id", length = 20)
    @Schema(description = "开票员ID")
    private String operatorId;

    @Column(name = "operator_name", length = 50)
    @Schema(description = "开票员姓名")
    private String operatorName;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态: VALID-有效, VOID-已作废")
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.VALID;

    @Column(name = "void_time")
    @Schema(description = "作废时间")
    private LocalDateTime voidTime;

    @Column(name = "void_operator_id", length = 20)
    @Schema(description = "作废操作员ID")
    private String voidOperatorId;

    @Column(name = "void_reason", length = 200)
    @Schema(description = "作废原因")
    private String voidReason;

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;

    /**
     * 收费类型枚举
     */
    public enum BillingType {
        OUTPATIENT("门诊"),
        INPATIENT("住院"),
        PREPAYMENT("预交金");

        private final String description;

        BillingType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 发票类型枚举
     */
    public enum InvoiceType {
        MEDICAL("医疗收费发票"),
        PREPAYMENT("预交金收据"),
        SETTLEMENT("结算清单"),
        ELECTRONIC("电子发票");

        private final String description;

        InvoiceType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 发票状态枚举
     */
    public enum InvoiceStatus {
        VALID("有效"),
        VOID("已作废");

        private final String description;

        InvoiceStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}