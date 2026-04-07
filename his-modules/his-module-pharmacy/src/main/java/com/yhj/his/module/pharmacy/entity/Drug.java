package com.yhj.his.module.pharmacy.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.pharmacy.enums.DrugCategory;
import com.yhj.his.module.pharmacy.enums.DrugStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 药品信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "drug", indexes = {
        @Index(name = "idx_drug_code", columnList = "drug_code", unique = true),
        @Index(name = "idx_drug_name", columnList = "drug_name"),
        @Index(name = "idx_pinyin_code", columnList = "pinyin_code"),
        @Index(name = "idx_drug_category", columnList = "drug_category")
})
@Schema(description = "药品信息")
public class Drug extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "药品编码")
    @Column(name = "drug_code", length = 30, nullable = false, unique = true)
    private String drugCode;

    @Schema(description = "药品名称")
    @Column(name = "drug_name", length = 100, nullable = false)
    private String drugName;

    @Schema(description = "通用名")
    @Column(name = "generic_name", length = 100)
    private String genericName;

    @Schema(description = "商品名")
    @Column(name = "trade_name", length = 100)
    private String tradeName;

    @Schema(description = "拼音码")
    @Column(name = "pinyin_code", length = 50)
    private String pinyinCode;

    @Schema(description = "自定义码")
    @Column(name = "custom_code", length = 50)
    private String customCode;

    @Schema(description = "药品分类")
    @Enumerated(EnumType.STRING)
    @Column(name = "drug_category", length = 20, nullable = false)
    private DrugCategory drugCategory;

    @Schema(description = "剂型")
    @Column(name = "drug_form", length = 20)
    private String drugForm;

    @Schema(description = "规格")
    @Column(name = "drug_spec", length = 50)
    private String drugSpec;

    @Schema(description = "最小单位")
    @Column(name = "drug_unit", length = 20)
    private String drugUnit;

    @Schema(description = "包装单位")
    @Column(name = "package_unit", length = 20)
    private String packageUnit;

    @Schema(description = "包装数量")
    @Column(name = "package_quantity")
    private Integer packageQuantity;

    @Schema(description = "生产厂家")
    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Schema(description = "产地")
    @Column(name = "origin", length = 50)
    private String origin;

    @Schema(description = "批准文号")
    @Column(name = "approval_no", length = 50)
    private String approvalNo;

    @Schema(description = "进价")
    @Column(name = "purchase_price", precision = 10, scale = 4)
    private BigDecimal purchasePrice;

    @Schema(description = "零售价")
    @Column(name = "retail_price", precision = 10, scale = 4, nullable = false)
    private BigDecimal retailPrice;

    @Schema(description = "调价日期")
    @Column(name = "price_date")
    private LocalDate priceDate;

    @Schema(description = "是否处方药")
    @Column(name = "is_prescription", nullable = false)
    private Boolean isPrescription = false;

    @Schema(description = "是否OTC")
    @Column(name = "is_otc", nullable = false)
    private Boolean isOtc = false;

    @Schema(description = "是否基药")
    @Column(name = "is_essential", nullable = false)
    private Boolean isEssential = false;

    @Schema(description = "是否医保")
    @Column(name = "is_insurance", nullable = false)
    private Boolean isInsurance = false;

    @Schema(description = "医保编码")
    @Column(name = "insurance_code", length = 50)
    private String insuranceCode;

    @Schema(description = "医保类型(甲类/乙类)")
    @Column(name = "insurance_type", length = 20)
    private String insuranceType;

    @Schema(description = "储存条件")
    @Column(name = "storage_condition", length = 50)
    private String storageCondition;

    @Schema(description = "有效期(月)")
    @Column(name = "shelf_life")
    private Integer shelfLife;

    @Schema(description = "效期预警天数")
    @Column(name = "alert_days")
    private Integer alertDays = 180;

    @Schema(description = "库存下限")
    @Column(name = "min_stock", precision = 10, scale = 2)
    private BigDecimal minStock;

    @Schema(description = "库存上限")
    @Column(name = "max_stock", precision = 10, scale = 2)
    private BigDecimal maxStock;

    @Schema(description = "状态")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private DrugStatus status = DrugStatus.NORMAL;
}