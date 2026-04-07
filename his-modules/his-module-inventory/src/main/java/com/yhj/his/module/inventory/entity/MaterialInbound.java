package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.inventory.enums.InboundStatus;
import com.yhj.his.module.inventory.enums.InboundType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 入库记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"items"})
@Entity
@Table(name = "material_inbound", indexes = {
    @Index(name = "idx_inbound_no", columnList = "inbound_no"),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_inbound_date", columnList = "inbound_date")
})
public class MaterialInbound extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 入库单号
     */
    @Column(name = "inbound_no", length = 30, nullable = false, unique = true)
    private String inboundNo;

    /**
     * 入库类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "inbound_type", length = 20, nullable = false)
    private InboundType inboundType;

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
     * 入库日期
     */
    @Column(name = "inbound_date", nullable = false)
    private LocalDate inboundDate;

    /**
     * 入库时间
     */
    @Column(name = "inbound_time")
    private LocalDateTime inboundTime;

    /**
     * 总数量
     */
    @Column(name = "total_quantity", precision = 10, scale = 2)
    private BigDecimal totalQuantity = BigDecimal.ZERO;

    /**
     * 总金额
     */
    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * 申请人ID
     */
    @Column(name = "applicant_id", length = 20)
    private String applicantId;

    /**
     * 申请人姓名
     */
    @Column(name = "applicant_name", length = 50)
    private String applicantName;

    /**
     * 申请时间
     */
    @Column(name = "apply_time")
    private LocalDateTime applyTime;

    /**
     * 审核人ID
     */
    @Column(name = "auditor_id", length = 20)
    private String auditorId;

    /**
     * 审核人姓名
     */
    @Column(name = "auditor_name", length = 50)
    private String auditorName;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    /**
     * 审核意见
     */
    @Column(name = "audit_remark", length = 200)
    private String auditRemark;

    /**
     * 入库人ID
     */
    @Column(name = "operator_id", length = 20)
    private String operatorId;

    /**
     * 入库人姓名
     */
    @Column(name = "operator_name", length = 50)
    private String operatorName;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;

    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private InboundStatus status = InboundStatus.PENDING;

    /**
     * 入库明细
     */
    @OneToMany(mappedBy = "inbound", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaterialInboundItem> items = new ArrayList<>();
}