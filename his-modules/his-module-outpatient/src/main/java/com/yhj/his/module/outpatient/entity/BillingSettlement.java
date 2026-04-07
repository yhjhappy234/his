package com.yhj.his.module.outpatient.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收费结算记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing_settlement", indexes = {
        @Index(name = "idx_settle_registration", columnList = "registration_id"),
        @Index(name = "idx_settle_patient", columnList = "patient_id"),
        @Index(name = "idx_settle_invoice", columnList = "invoice_no")
})
public class BillingSettlement extends BaseEntity {

    /**
     * 结算单号
     */
    @Column(name = "settlement_no", length = 30, unique = true)
    private String settlementNo;

    /**
     * 发票号
     */
    @Column(name = "invoice_no", length = 30, unique = true)
    private String invoiceNo;

    /**
     * 挂号ID
     */
    @Column(name = "registration_id", length = 36, nullable = false)
    private String registrationId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    /**
     * 患者姓名
     */
    @Column(name = "patient_name", length = 50)
    private String patientName;

    /**
     * 总金额
     */
    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    /**
     * 医保支付金额
     */
    @Column(name = "insurance_amount", precision = 10, scale = 2)
    private BigDecimal insuranceAmount;

    /**
     * 自费支付金额
     */
    @Column(name = "self_pay_amount", precision = 10, scale = 2)
    private BigDecimal selfPayAmount;

    /**
     * 优惠金额
     */
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    /**
     * 实收金额
     */
    @Column(name = "actual_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal actualAmount;

    /**
     * 支付方式: 现金/银行卡/微信/支付宝/医保卡/预交金
     */
    @Column(name = "pay_method", length = 50)
    private String payMethod;

    /**
     * 支付流水号
     */
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    /**
     * 医保卡号
     */
    @Column(name = "insurance_card_no", length = 30)
    private String insuranceCardNo;

    /**
     * 结算状态: 已结算/已退费
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "已结算";

    /**
     * 结算时间
     */
    @Column(name = "settle_time")
    private LocalDateTime settleTime;

    /**
     * 收费员ID
     */
    @Column(name = "cashier_id", length = 20)
    private String cashierId;

    /**
     * 收费员姓名
     */
    @Column(name = "cashier_name", length = 50)
    private String cashierName;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}