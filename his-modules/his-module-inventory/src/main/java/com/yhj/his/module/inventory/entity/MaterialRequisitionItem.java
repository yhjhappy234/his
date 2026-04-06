package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 物资申领明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "material_requisition_item", indexes = {
    @Index(name = "idx_requisition_id", columnList = "requisition_id"),
    @Index(name = "idx_material_id", columnList = "material_id")
})
public class MaterialRequisitionItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 申领记录ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id", nullable = false)
    private MaterialRequisition requisition;

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
     * 申请数量
     */
    @Column(name = "apply_quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal applyQuantity;

    /**
     * 实发数量
     */
    @Column(name = "issue_quantity", precision = 10, scale = 2)
    private BigDecimal issueQuantity;

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
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;
}