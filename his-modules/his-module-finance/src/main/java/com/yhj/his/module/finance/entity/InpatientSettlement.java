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
 * 住院结算实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inpatient_settlement", indexes = {
        @Index(name = "idx_settlement_no", columnList = "settlementNo", unique = true),
        @Index(name = "idx_admission_id", columnList = "admissionId"),
        @Index(name = "idx_patient_id", columnList = "patientId"),
        @Index(name = "idx_settlement_time", columnList = "settlementTime")
})
@Schema(description = "住院结算")
public class InpatientSettlement extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "settlement_no", length = 30, nullable = false, unique = true)
    @Schema(description = "结算单号")
    private String settlementNo;

    @Column(name = "invoice_no", length = 30)
    @Schema(description = "发票号")
    private String invoiceNo;

    @Column(name = "admission_id", length = 36, nullable = false)
    @Schema(description = "住院ID")
    private String admissionId;

    @Column(name = "patient_id", length = 20, nullable = false)
    @Schema(description = "患者ID")
    private String patientId;

    @Column(name = "patient_name", length = 50)
    @Schema(description = "患者姓名")
    private String patientName;

    @Column(name = "admission_date")
    @Schema(description = "入院日期")
    private LocalDate admissionDate;

    @Column(name = "discharge_date")
    @Schema(description = "出院日期")
    private LocalDate dischargeDate;

    @Column(name = "hospital_days")
    @Schema(description = "住院天数")
    private Integer hospitalDays;

    @Column(name = "total_amount", precision = 12, scale = 2)
    @Schema(description = "总费用")
    private BigDecimal totalAmount;

    @Column(name = "bed_fee", precision = 12, scale = 2)
    @Schema(description = "床位费")
    private BigDecimal bedFee;

    @Column(name = "drug_fee", precision = 12, scale = 2)
    @Schema(description = "药品费")
    private BigDecimal drugFee;

    @Column(name = "exam_fee", precision = 12, scale = 2)
    @Schema(description = "检查费")
    private BigDecimal examFee;

    @Column(name = "test_fee", precision = 12, scale = 2)
    @Schema(description = "检验费")
    private BigDecimal testFee;

    @Column(name = "treatment_fee", precision = 12, scale = 2)
    @Schema(description = "治疗费")
    private BigDecimal treatmentFee;

    @Column(name = "material_fee", precision = 12, scale = 2)
    @Schema(description = "材料费")
    private BigDecimal materialFee;

    @Column(name = "nursing_fee", precision = 12, scale = 2)
    @Schema(description = "护理费")
    private BigDecimal nursingFee;

    @Column(name = "other_fee", precision = 12, scale = 2)
    @Schema(description = "其他费")
    private BigDecimal otherFee;

    @Column(name = "total_deposit", precision = 12, scale = 2)
    @Schema(description = "预交金总额")
    private BigDecimal totalDeposit;

    @Column(name = "insurance_amount", precision = 12, scale = 2)
    @Schema(description = "医保支付金额")
    private BigDecimal insuranceAmount;

    @Column(name = "self_pay_amount", precision = 12, scale = 2)
    @Schema(description = "自付金额")
    private BigDecimal selfPayAmount;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    @Schema(description = "退还金额")
    private BigDecimal refundAmount;

    @Column(name = "supplement_amount", precision = 12, scale = 2)
    @Schema(description = "补交金额")
    private BigDecimal supplementAmount;

    @Column(name = "insurance_type", length = 20)
    @Schema(description = "医保类型")
    @Enumerated(EnumType.STRING)
    private InsurancePolicy.InsuranceTypeEnum insuranceType;

    @Column(name = "insurance_card_no", length = 50)
    @Schema(description = "医保卡号")
    private String insuranceCardNo;

    @Column(name = "insurance_claim_no", length = 50)
    @Schema(description = "医保申报号")
    private String insuranceClaimNo;

    @Column(name = "payments", columnDefinition = "TEXT")
    @Schema(description = "支付明细(JSON格式)")
    private String payments;

    @Column(name = "settlement_time", nullable = false)
    @Schema(description = "结算时间")
    private LocalDateTime settlementTime;

    @Column(name = "operator_id", length = 20)
    @Schema(description = "结算员ID")
    private String operatorId;

    @Column(name = "operator_name", length = 50)
    @Schema(description = "结算员姓名")
    private String operatorName;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态: NORMAL-正常, CANCELLED-已作废")
    @Enumerated(EnumType.STRING)
    private SettlementStatus status = SettlementStatus.NORMAL;

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;

    /**
     * 结算状态枚举
     */
    public enum SettlementStatus {
        NORMAL("正常"),
        CANCELLED("已作废");

        private final String description;

        SettlementStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}