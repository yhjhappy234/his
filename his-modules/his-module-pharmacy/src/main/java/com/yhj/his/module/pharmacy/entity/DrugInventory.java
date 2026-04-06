package com.yhj.his.module.pharmacy.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.pharmacy.enums.InventoryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 药品库存实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "drug_inventory", indexes = {
        @Index(name = "idx_inv_drug_id", columnList = "drug_id"),
        @Index(name = "idx_inv_expiry_date", columnList = "expiry_date"),
        @Index(name = "idx_inv_pharmacy_id", columnList = "pharmacy_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_drug_batch_pharmacy", columnNames = {"drug_id", "batch_no", "pharmacy_id"})
})
@Schema(description = "药品库存")
public class DrugInventory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "药品ID")
    @Column(name = "drug_id", length = 36, nullable = false)
    private String drugId;

    @Schema(description = "药品编码")
    @Column(name = "drug_code", length = 30, nullable = false)
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

    @Schema(description = "药房ID")
    @Column(name = "pharmacy_id", length = 20, nullable = false)
    private String pharmacyId;

    @Schema(description = "药房名称")
    @Column(name = "pharmacy_name", length = 100)
    private String pharmacyName;

    @Schema(description = "批号")
    @Column(name = "batch_no", length = 50, nullable = false)
    private String batchNo;

    @Schema(description = "生产日期")
    @Column(name = "production_date")
    private LocalDate productionDate;

    @Schema(description = "有效期")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Schema(description = "库存数量")
    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Schema(description = "锁定数量")
    @Column(name = "locked_quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal lockedQuantity = BigDecimal.ZERO;

    @Schema(description = "可用数量")
    @Column(name = "available_quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal availableQuantity = BigDecimal.ZERO;

    @Schema(description = "库位")
    @Column(name = "location", length = 50)
    private String location;

    @Schema(description = "进价")
    @Column(name = "purchase_price", precision = 10, scale = 4)
    private BigDecimal purchasePrice;

    @Schema(description = "零售价")
    @Column(name = "retail_price", precision = 10, scale = 4)
    private BigDecimal retailPrice;

    @Schema(description = "供应商ID")
    @Column(name = "supplier_id", length = 20)
    private String supplierId;

    @Schema(description = "供应商名称")
    @Column(name = "supplier_name", length = 100)
    private String supplierName;

    @Schema(description = "状态")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private InventoryStatus status = InventoryStatus.NORMAL;

    @Transient
    @Schema(description = "药品信息")
    private Drug drug;

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }
}