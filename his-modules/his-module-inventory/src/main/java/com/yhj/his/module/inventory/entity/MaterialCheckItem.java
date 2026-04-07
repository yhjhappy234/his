package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库存盘点明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "material_check_item", indexes = {
    @Index(name = "idx_check_id", columnList = "check_id"),
    @Index(name = "idx_material_id", columnList = "material_id")
})
public class MaterialCheckItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 盘点记录ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_id", nullable = false)
    private MaterialCheck check;

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
     * 批号
     */
    @Column(name = "batch_no", length = 50)
    private String batchNo;

    /**
     * 库存ID
     */
    @Column(name = "inventory_id", length = 36)
    private String inventoryId;

    /**
     * 账面数量
     */
    @Column(name = "book_quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal bookQuantity;

    /**
     * 实盘数量
     */
    @Column(name = "actual_quantity", precision = 10, scale = 2)
    private BigDecimal actualQuantity;

    /**
     * 差异数量
     */
    @Column(name = "diff_quantity", precision = 10, scale = 2)
    private BigDecimal diffQuantity;

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
     * 差异金额
     */
    @Column(name = "diff_amount", precision = 12, scale = 2)
    private BigDecimal diffAmount;

    /**
     * 差异类型 (PROFIT-盘盈, LOSS-盘亏, NONE-无差异)
     */
    @Column(name = "diff_type", length = 10)
    private String diffType;

    /**
     * 调整状态 (0-未调整, 1-已调整)
     */
    @Column(name = "adjusted", nullable = false)
    private Boolean adjusted = false;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;
}