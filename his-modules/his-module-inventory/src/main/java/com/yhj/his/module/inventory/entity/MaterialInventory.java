package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 物资库存实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "material_inventory", indexes = {
    @Index(name = "idx_material_id", columnList = "material_id"),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_expiry_date", columnList = "expiry_date")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_material_batch_warehouse", columnNames = {"material_id", "batch_no", "warehouse_id"})
})
public class MaterialInventory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 物资ID
     */
    @Column(name = "material_id", length = 36, nullable = false)
    private String materialId;

    /**
     * 物资编码
     */
    @Column(name = "material_code", length = 30)
    private String materialCode;

    /**
     * 物资名称
     */
    @Column(name = "material_name", length = 100)
    private String materialName;

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
     * 库房ID
     */
    @Column(name = "warehouse_id", length = 36, nullable = false)
    private String warehouseId;

    /**
     * 库房名称
     */
    @Column(name = "warehouse_name", length = 100)
    private String warehouseName;

    /**
     * 批号
     */
    @Column(name = "batch_no", length = 50)
    private String batchNo;

    /**
     * 有效期
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * 库存数量
     */
    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO;

    /**
     * 锁定数量
     */
    @Column(name = "locked_quantity", precision = 10, scale = 2)
    private BigDecimal lockedQuantity = BigDecimal.ZERO;

    /**
     * 可用数量
     */
    @Column(name = "available_quantity", precision = 10, scale = 2)
    private BigDecimal availableQuantity = BigDecimal.ZERO;

    /**
     * 库位
     */
    @Column(name = "location", length = 50)
    private String location;

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
     * 供应商ID
     */
    @Column(name = "supplier_id", length = 20)
    private String supplierId;

    /**
     * 供应商名称
     */
    @Column(name = "supplier_name", length = 100)
    private String supplierName;

    /**
     * 入库时间
     */
    @Column(name = "inbound_time")
    private LocalDateTime inboundTime;

    /**
     * 状态 (0-禁用, 1-启用)
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;
}