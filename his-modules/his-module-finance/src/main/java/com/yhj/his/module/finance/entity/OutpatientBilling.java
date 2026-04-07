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
 * 门诊收费记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "outpatient_billing", indexes = {
        @Index(name = "idx_billing_no", columnList = "billingNo", unique = true),
        @Index(name = "idx_patient_id", columnList = "patientId"),
        @Index(name = "idx_visit_id", columnList = "visitId"),
        @Index(name = "idx_billing_date", columnList = "billingDate"),
        @Index(name = "idx_invoice_no", columnList = "invoiceNo")
})
@Schema(description = "门诊收费记录")
public class OutpatientBilling extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "billing_no", length = 30, nullable = false, unique = true)
    @Schema(description = "收费单号")
    private String billingNo;

    @Column(name = "invoice_no", length = 30)
    @Schema(description = "发票号")
    private String invoiceNo;

    @Column(name = "patient_id", length = 20, nullable = false)
    @Schema(description = "患者ID")
    private String patientId;

    @Column(name = "patient_name", length = 50)
    @Schema(description = "患者姓名")
    private String patientName;

    @Column(name = "visit_id", length = 36)
    @Schema(description = "就诊ID")
    private String visitId;

    @Column(name = "visit_no", length = 30)
    @Schema(description = "就诊序号")
    private String visitNo;

    @Column(name = "dept_id", length = 20)
    @Schema(description = "科室ID")
    private String deptId;

    @Column(name = "dept_name", length = 100)
    @Schema(description = "科室名称")
    private String deptName;

    @Column(name = "billing_date", nullable = false)
    @Schema(description = "收费日期")
    private LocalDate billingDate;

    @Column(name = "billing_time", nullable = false)
    @Schema(description = "收费时间")
    private LocalDateTime billingTime;

    @Column(name = "total_amount", precision = 12, scale = 2)
    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Column(name = "insurance_amount", precision = 12, scale = 2)
    @Schema(description = "医保支付金额")
    private BigDecimal insuranceAmount;

    @Column(name = "self_pay_amount", precision = 12, scale = 2)
    @Schema(description = "自付金额")
    private BigDecimal selfPayAmount;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    @Schema(description = "退费金额")
    private BigDecimal refundAmount;

    @Column(name = "insurance_type", length = 20)
    @Schema(description = "医保类型")
    @Enumerated(EnumType.STRING)
    private InsurancePolicy.InsuranceTypeEnum insuranceType;

    @Column(name = "insurance_card_no", length = 50)
    @Schema(description = "医保卡号")
    private String insuranceCardNo;

    @Column(name = "payments", columnDefinition = "TEXT")
    @Schema(description = "支付明细(JSON格式)")
    private String payments;

    @Column(name = "operator_id", length = 20)
    @Schema(description = "收费员ID")
    private String operatorId;

    @Column(name = "operator_name", length = 50)
    @Schema(description = "收费员姓名")
    private String operatorName;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态: NORMAL-正常, REFUNDED-已退费, PARTIAL_REFUND-部分退费")
    @Enumerated(EnumType.STRING)
    private BillingStatus status = BillingStatus.NORMAL;

    @Column(name = "refund_status", length = 20)
    @Schema(description = "退费状态: NONE-未退费, PENDING-待审批, APPROVED-已审批, COMPLETED-已完成, REJECTED-已拒绝")
    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus = RefundStatus.NONE;

    @Column(name = "refund_time")
    @Schema(description = "退费时间")
    private LocalDateTime refundTime;

    @Column(name = "refund_operator_id", length = 20)
    @Schema(description = "退费操作员ID")
    private String refundOperatorId;

    @Column(name = "refund_reason", length = 200)
    @Schema(description = "退费原因")
    private String refundReason;

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;

    /**
     * 收费状态枚举
     */
    public enum BillingStatus {
        NORMAL("正常"),
        REFUNDED("已退费"),
        PARTIAL_REFUND("部分退费");

        private final String description;

        BillingStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 退费状态枚举
     */
    public enum RefundStatus {
        NONE("未退费"),
        PENDING("待审批"),
        APPROVED("已审批"),
        COMPLETED("已完成"),
        REJECTED("已拒绝");

        private final String description;

        RefundStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}