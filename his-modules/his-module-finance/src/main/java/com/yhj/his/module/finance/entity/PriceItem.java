package com.yhj.his.module.finance.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 收费项目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "price_item", indexes = {
        @Index(name = "idx_code", columnList = "itemCode"),
        @Index(name = "idx_category", columnList = "itemCategory"),
        @Index(name = "idx_insurance_type", columnList = "insuranceType")
})
@Schema(description = "收费项目")
public class PriceItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "item_code", length = 20, nullable = false, unique = true)
    @Schema(description = "项目编码")
    private String itemCode;

    @Column(name = "item_name", length = 100, nullable = false)
    @Schema(description = "项目名称")
    private String itemName;

    @Column(name = "item_category", length = 20, nullable = false)
    @Schema(description = "项目分类: DRUG-药品, EXAM-检查, TEST-检验, TREATMENT-治疗, BED-床位, NURSING-护理, MATERIAL-材料, OTHER-其他")
    @Enumerated(EnumType.STRING)
    private ItemCategory itemCategory;

    @Column(name = "item_unit", length = 20)
    @Schema(description = "单位")
    private String itemUnit;

    @Column(name = "item_spec", length = 50)
    @Schema(description = "规格")
    private String itemSpec;

    @Column(name = "standard_price", precision = 10, scale = 4)
    @Schema(description = "标准价格")
    private BigDecimal standardPrice;

    @Column(name = "retail_price", precision = 10, scale = 4, nullable = false)
    @Schema(description = "零售价格")
    private BigDecimal retailPrice;

    @Column(name = "wholesale_price", precision = 10, scale = 4)
    @Schema(description = "批发价格")
    private BigDecimal wholesalePrice;

    @Column(name = "insurance_type", length = 20)
    @Schema(description = "医保类型: A-甲类, B-乙类, C-丙类, SELF-自费")
    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    @Column(name = "insurance_code", length = 50)
    @Schema(description = "医保编码")
    private String insuranceCode;

    @Column(name = "insurance_price", precision = 10, scale = 4)
    @Schema(description = "医保价格")
    private BigDecimal insurancePrice;

    @Column(name = "reimbursement_ratio", precision = 5, scale = 2)
    @Schema(description = "报销比例(0-100)")
    private BigDecimal reimbursementRatio;

    @Column(name = "effective_date")
    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    @Column(name = "expire_date")
    @Schema(description = "失效日期")
    private LocalDate expireDate;

    @Column(name = "version_no", length = 10)
    @Schema(description = "版本号")
    private String versionNo;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态: ACTIVE-启用, INACTIVE-停用")
    @Enumerated(EnumType.STRING)
    private PriceItemStatus status = PriceItemStatus.ACTIVE;

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;

    /**
     * 项目分类枚举
     */
    public enum ItemCategory {
        DRUG("药品"),
        EXAM("检查"),
        TEST("检验"),
        TREATMENT("治疗"),
        BED("床位"),
        NURSING("护理"),
        MATERIAL("材料"),
        OTHER("其他");

        private final String description;

        ItemCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 医保类型枚举
     */
    public enum InsuranceType {
        A("甲类"),
        B("乙类"),
        C("丙类"),
        SELF("自费");

        private final String description;

        InsuranceType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 价格项目状态枚举
     */
    public enum PriceItemStatus {
        ACTIVE("启用"),
        INACTIVE("停用");

        private final String description;

        PriceItemStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}