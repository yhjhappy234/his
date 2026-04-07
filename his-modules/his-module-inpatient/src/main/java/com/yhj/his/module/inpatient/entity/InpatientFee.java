package com.yhj.his.module.inpatient.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.inpatient.enums.FeeCategory;
import com.yhj.his.module.inpatient.enums.PayStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 住院费用实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inpatient_fee", indexes = {
        @Index(name = "idx_admission", columnList = "admission_id"),
        @Index(name = "idx_patient", columnList = "patient_id"),
        @Index(name = "idx_fee_date", columnList = "fee_date"),
        @Index(name = "idx_fee_category", columnList = "fee_category")
})
public class InpatientFee extends BaseEntity {

    /**
     * 住院ID
     */
    @Column(name = "admission_id", length = 36, nullable = false)
    private String admissionId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    /**
     * 费用日期
     */
    @Column(name = "fee_date", nullable = false)
    private LocalDate feeDate;

    /**
     * 费用分类
     */
    @Column(name = "fee_category", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private FeeCategory feeCategory;

    /**
     * 项目编码
     */
    @Column(name = "fee_item_code", length = 20, nullable = false)
    private String feeItemCode;

    /**
     * 项目名称
     */
    @Column(name = "fee_item_name", length = 100, nullable = false)
    private String feeItemName;

    /**
     * 规格
     */
    @Column(name = "fee_spec", length = 50)
    private String feeSpec;

    /**
     * 单位
     */
    @Column(name = "fee_unit", length = 20)
    private String feeUnit;

    /**
     * 单价
     */
    @Column(name = "fee_price", precision = 10, scale = 4)
    private BigDecimal feePrice;

    /**
     * 数量
     */
    @Column(name = "fee_quantity", precision = 10, scale = 2)
    private BigDecimal feeQuantity;

    /**
     * 金额
     */
    @Column(name = "fee_amount", precision = 10, scale = 2)
    private BigDecimal feeAmount;

    /**
     * 关联医嘱ID
     */
    @Column(name = "order_id", length = 36)
    private String orderId;

    /**
     * 关联医嘱号
     */
    @Column(name = "order_no", length = 30)
    private String orderNo;

    /**
     * 执行科室ID
     */
    @Column(name = "dept_id", length = 20)
    private String deptId;

    /**
     * 执行科室名称
     */
    @Column(name = "dept_name", length = 100)
    private String deptName;

    /**
     * 是否医保
     */
    @Column(name = "is_insurance", nullable = false)
    private Boolean isInsurance = true;

    /**
     * 结算状态
     */
    @Column(name = "pay_status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus = PayStatus.UNSETTLED;

    /**
     * 操作员ID
     */
    @Column(name = "operator_id", length = 20)
    private String operatorId;
}