package com.yhj.his.module.outpatient.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 处方明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "prescription_detail", indexes = {
        @Index(name = "idx_detail_prescription", columnList = "prescription_id"),
        @Index(name = "idx_detail_drug", columnList = "drug_id")
})
public class PrescriptionDetail extends BaseEntity {

    /**
     * 处方ID
     */
    @Column(name = "prescription_id", length = 36, nullable = false)
    private String prescriptionId;

    /**
     * 药品ID
     */
    @Column(name = "drug_id", length = 20, nullable = false)
    private String drugId;

    /**
     * 药品名称
     */
    @Column(name = "drug_name", length = 100, nullable = false)
    private String drugName;

    /**
     * 药品规格
     */
    @Column(name = "drug_spec", length = 50)
    private String drugSpec;

    /**
     * 单位
     */
    @Column(name = "drug_unit", length = 20)
    private String drugUnit;

    /**
     * 剂型
     */
    @Column(name = "drug_form", length = 20)
    private String drugForm;

    /**
     * 数量
     */
    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity;

    /**
     * 用法
     */
    @Column(name = "dosage", length = 50)
    private String dosage;

    /**
     * 使用频率
     */
    @Column(name = "frequency", length = 50)
    private String frequency;

    /**
     * 用药天数
     */
    @Column(name = "days")
    private Integer days;

    /**
     * 给药途径
     */
    @Column(name = "route", length = 50)
    private String route;

    /**
     * 单价
     */
    @Column(name = "unit_price", precision = 10, scale = 4)
    private BigDecimal unitPrice;

    /**
     * 金额
     */
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 组号(用于输液分组)
     */
    @Column(name = "group_no")
    private Integer groupNo;

    /**
     * 皮试要求
     */
    @Column(name = "skin_test", length = 10)
    private String skinTest;

    /**
     * 皮试结果
     */
    @Column(name = "skin_test_result", length = 20)
    private String skinTestResult;

    /**
     * 是否基药
     */
    @Column(name = "is_essential")
    private Boolean isEssential;

    /**
     * 是否医保
     */
    @Column(name = "is_medical_insurance")
    private Boolean isMedicalInsurance;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;
}