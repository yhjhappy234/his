package com.yhj.his.module.finance.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 医保政策实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "insurance_policy", indexes = {
        @Index(name = "idx_insurance_type", columnList = "insuranceType"),
        @Index(name = "idx_status", columnList = "status")
})
@Schema(description = "医保政策")
public class InsurancePolicy extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "policy_name", length = 100, nullable = false)
    @Schema(description = "政策名称")
    private String policyName;

    @Column(name = "insurance_type", length = 20, nullable = false)
    @Schema(description = "医保类型: URBAN_EMPLOYEE-城镇职工医保, URBAN_RESIDENT-城镇居民医保, NEW_RURAL-新农合, PUBLIC-公费医疗, COMMERCIAL-商业保险")
    @Enumerated(EnumType.STRING)
    private InsuranceTypeEnum insuranceType;

    @Column(name = "deductible_line", precision = 12, scale = 2)
    @Schema(description = "起付线")
    private BigDecimal deductibleLine;

    @Column(name = "cap_line", precision = 12, scale = 2)
    @Schema(description = "封顶线")
    private BigDecimal capLine;

    @Column(name = "class_a_ratio", precision = 5, scale = 2)
    @Schema(description = "甲类报销比例(0-100)")
    private BigDecimal classARatio;

    @Column(name = "class_b_ratio", precision = 5, scale = 2)
    @Schema(description = "乙类报销比例(0-100)")
    private BigDecimal classBRatio;

    @Column(name = "class_c_ratio", precision = 5, scale = 2)
    @Schema(description = "丙类报销比例(0-100)")
    private BigDecimal classCRatio;

    @Column(name = "outpatient_ratio", precision = 5, scale = 2)
    @Schema(description = "门诊报销比例")
    private BigDecimal outpatientRatio;

    @Column(name = "inpatient_ratio", precision = 5, scale = 2)
    @Schema(description = "住院报销比例")
    private BigDecimal inpatientRatio;

    @Column(name = "remark", length = 500)
    @Schema(description = "备注说明")
    private String remark;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态: ACTIVE-启用, INACTIVE-停用")
    @Enumerated(EnumType.STRING)
    private InsurancePolicyStatus status = InsurancePolicyStatus.ACTIVE;

    /**
     * 医保类型枚举
     */
    public enum InsuranceTypeEnum {
        URBAN_EMPLOYEE("城镇职工医保"),
        URBAN_RESIDENT("城镇居民医保"),
        NEW_RURAL("新农合"),
        PUBLIC("公费医疗"),
        COMMERCIAL("商业保险"),
        SELF("自费");

        private final String description;

        InsuranceTypeEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 医保政策状态枚举
     */
    public enum InsurancePolicyStatus {
        ACTIVE("启用"),
        INACTIVE("停用");

        private final String description;

        InsurancePolicyStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}