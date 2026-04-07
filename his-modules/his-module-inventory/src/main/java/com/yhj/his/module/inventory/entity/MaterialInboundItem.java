package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入库明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "material_inbound_item", indexes = {
    @Index(name = "idx_inbound_id", columnList = "inbound_id"),
    @Index(name = "idx_material_id", columnList = "material_id")
})
public class MaterialInboundItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 入库记录ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_id", nullable = false)
    private MaterialInbound inbound;

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
     * 生产日期
     */
    @Column(name = "production_date")
    private LocalDate productionDate;

    /**
     * 有效期
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * 数量
     */
    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity;

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
     * 金额
     */
    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    /**
     * 库位
     */
    @Column(name = "location", length = 50)
    private String location;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;
}