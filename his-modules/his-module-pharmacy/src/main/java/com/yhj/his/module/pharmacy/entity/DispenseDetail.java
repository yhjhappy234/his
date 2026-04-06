package com.yhj.his.module.pharmacy.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 发药明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dispense_detail", indexes = {
        @Index(name = "idx_detail_dispense_id", columnList = "dispense_id"),
        @Index(name = "idx_detail_drug_id", columnList = "drug_id")
})
@Schema(description = "发药明细")
public class DispenseDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "发药ID")
    @Column(name = "dispense_id", length = 36, nullable = false)
    private String dispenseId;

    @Schema(description = "药品ID")
    @Column(name = "drug_id", length = 36, nullable = false)
    private String drugId;

    @Schema(description = "药品编码")
    @Column(name = "drug_code", length = 30)
    private String drugCode;

    @Schema(description = "药品名称")
    @Column(name = "drug_name", length = 100)
    private String drugName;

    @Schema(description = "规格")
    @Column(name = "drug_spec", length = 50)
    private String drugSpec;

    @Schema(description = "单位")
    @Column(name = "drug_unit", length = 20)
    private String drugUnit;

    @Schema(description = "批号")
    @Column(name = "batch_no", length = 50)
    private String batchNo;

    @Schema(description = "有效期")
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Schema(description = "发药数量")
    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Schema(description = "零售价")
    @Column(name = "retail_price", precision = 10, scale = 4)
    private BigDecimal retailPrice;

    @Schema(description = "金额")
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Schema(description = "用法")
    @Column(name = "dosage", length = 50)
    private String dosage;

    @Schema(description = "频次")
    @Column(name = "frequency", length = 50)
    private String frequency;

    @Schema(description = "天数")
    @Column(name = "days")
    private Integer days;

    @Schema(description = "给药途径")
    @Column(name = "route", length = 50)
    private String route;

    @Schema(description = "处方明细ID")
    @Column(name = "prescription_detail_id", length = 36)
    private String prescriptionDetailId;

    @Schema(description = "审核结果(通过/不通过)")
    @Column(name = "audit_result", length = 20)
    private String auditResult;

    @Schema(description = "审核说明")
    @Column(name = "audit_remark", length = 500)
    private String auditRemark;
}