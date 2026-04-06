package com.yhj.his.module.outpatient.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收费项目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing_item", indexes = {
        @Index(name = "idx_bill_registration", columnList = "registration_id"),
        @Index(name = "idx_bill_patient", columnList = "patient_id"),
        @Index(name = "idx_bill_status", columnList = "pay_status")
})
public class BillingItem extends BaseEntity {

    /**
     * 项目编号
     */
    @Column(name = "item_no", length = 30)
    private String itemNo;

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
     * 项目类型: 挂号费/诊查费/处方/检查/检验
     */
    @Column(name = "item_type", length = 20, nullable = false)
    private String itemType;

    /**
     * 关联ID(处方ID/检查申请ID等)
     */
    @Column(name = "ref_id", length = 36)
    private String refId;

    /**
     * 项目名称
     */
    @Column(name = "item_name", length = 200)
    private String itemName;

    /**
     * 项目描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 金额
     */
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * 医保报销金额
     */
    @Column(name = "insurance_amount", precision = 10, scale = 2)
    private BigDecimal insuranceAmount;

    /**
     * 自付金额
     */
    @Column(name = "self_pay_amount", precision = 10, scale = 2)
    private BigDecimal selfPayAmount;

    /**
     * 收费状态: 未收费/已收费/已退费
     */
    @Column(name = "pay_status", length = 20, nullable = false)
    private String payStatus = "未收费";

    /**
     * 开单时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 收费时间
     */
    @Column(name = "pay_time")
    private LocalDateTime payTime;

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
     * 发票号
     */
    @Column(name = "invoice_no", length = 30)
    private String invoiceNo;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;
}