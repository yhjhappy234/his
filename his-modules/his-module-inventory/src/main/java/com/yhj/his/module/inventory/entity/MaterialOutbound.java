package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.inventory.enums.OutboundStatus;
import com.yhj.his.module.inventory.enums.OutboundType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 出库记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"items"})
@Entity
@Table(name = "material_outbound", indexes = {
    @Index(name = "idx_outbound_no", columnList = "outbound_no"),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_outbound_date", columnList = "outbound_date")
})
public class MaterialOutbound extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 出库单号
     */
    @Column(name = "outbound_no", length = 30, nullable = false, unique = true)
    private String outboundNo;

    /**
     * 出库类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "outbound_type", length = 20, nullable = false)
    private OutboundType outboundType;

    /**
     * 出库库房ID
     */
    @Column(name = "warehouse_id", length = 36, nullable = false)
    private String warehouseId;

    /**
     * 库房名称
     */
    @Column(name = "warehouse_name", length = 100)
    private String warehouseName;

    /**
     * 目标库房ID(调拨)
     */
    @Column(name = "target_warehouse_id", length = 36)
    private String targetWarehouseId;

    /**
     * 目标库房名称
     */
    @Column(name = "target_warehouse_name", length = 100)
    private String targetWarehouseName;

    /**
     * 目标科室ID
     */
    @Column(name = "target_dept_id", length = 20)
    private String targetDeptId;

    /**
     * 目标科室名称
     */
    @Column(name = "target_dept_name", length = 100)
    private String targetDeptName;

    /**
     * 出库日期
     */
    @Column(name = "outbound_date", nullable = false)
    private LocalDate outboundDate;

    /**
     * 出库时间
     */
    @Column(name = "outbound_time")
    private LocalDateTime outboundTime;

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
     * 出库人ID
     */
    @Column(name = "operator_id", length = 20)
    private String operatorId;

    /**
     * 出库人姓名
     */
    @Column(name = "operator_name", length = 50)
    private String operatorName;

    /**
     * 接收人ID
     */
    @Column(name = "receiver_id", length = 20)
    private String receiverId;

    /**
     * 接收人姓名
     */
    @Column(name = "receiver_name", length = 50)
    private String receiverName;

    /**
     * 接收时间
     */
    @Column(name = "receive_time")
    private LocalDateTime receiveTime;

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
    private OutboundStatus status = OutboundStatus.PENDING;

    /**
     * 出库明细
     */
    @OneToMany(mappedBy = "outbound", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaterialOutboundItem> items = new ArrayList<>();
}