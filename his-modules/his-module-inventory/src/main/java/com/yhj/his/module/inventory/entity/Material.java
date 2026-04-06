package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.inventory.enums.MaterialStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 物资信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "material", indexes = {
    @Index(name = "idx_material_code", columnList = "material_code"),
    @Index(name = "idx_material_name", columnList = "material_name"),
    @Index(name = "idx_category_id", columnList = "category_id")
})
public class Material extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 物资编码
     */
    @Column(name = "material_code", length = 30, nullable = false, unique = true)
    private String materialCode;

    /**
     * 物资名称
     */
    @Column(name = "material_name", length = 100, nullable = false)
    private String materialName;

    /**
     * 分类ID
     */
    @Column(name = "category_id", length = 36)
    private String categoryId;

    /**
     * 分类名称
     */
    @Column(name = "category_name", length = 50)
    private String categoryName;

    /**
     * 规格
     */
    @Column(name = "material_spec", length = 50)
    private String materialSpec;

    /**
     * 单位
     */
    @Column(name = "material_unit", length = 20)
    private String materialUnit;

    /**
     * 生产厂家
     */
    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    /**
     * 品牌
     */
    @Column(name = "brand", length = 50)
    private String brand;

    /**
     * 产地
     */
    @Column(name = "origin", length = 50)
    private String origin;

    /**
     * 进价
     */
    @Column(name = "purchase_price", precision = 10, scale = 4)
    private BigDecimal purchasePrice;

    /**
     * 零售价
     */
    @Column(name = "retail_price", precision = 10, scale = 4)
    private BigDecimal retailPrice;

    /**
     * 调价日期
     */
    @Column(name = "price_date")
    private LocalDate priceDate;

    /**
     * 库存下限
     */
    @Column(name = "min_stock", precision = 10, scale = 2)
    private BigDecimal minStock;

    /**
     * 库存上限
     */
    @Column(name = "max_stock", precision = 10, scale = 2)
    private BigDecimal maxStock;

    /**
     * 安全库存
     */
    @Column(name = "safety_stock", precision = 10, scale = 2)
    private BigDecimal safetyStock;

    /**
     * 有效期(月)
     */
    @Column(name = "shelf_life")
    private Integer shelfLife;

    /**
     * 储存条件
     */
    @Column(name = "storage_condition", length = 50)
    private String storageCondition;

    /**
     * 是否医疗耗材
     */
    @Column(name = "is_medical", nullable = false)
    private Boolean isMedical = false;

    /**
     * 是否无菌
     */
    @Column(name = "is_sterile", nullable = false)
    private Boolean isSterile = false;

    /**
     * 是否可复用
     */
    @Column(name = "is_reusable", nullable = false)
    private Boolean isReusable = false;

    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private MaterialStatus status = MaterialStatus.NORMAL;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;
}