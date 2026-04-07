package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.inventory.enums.RequisitionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 物资申领实体
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"items"})
@Entity
@Table(name = "material_requisition", indexes = {
    @Index(name = "idx_requisition_no", columnList = "requisition_no"),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_dept_id", columnList = "dept_id"),
    @Index(name = "idx_requisition_date", columnList = "requisition_date")
})
public class MaterialRequisition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 申领单号
     */
    @Column(name = "requisition_no", length = 30, nullable = false, unique = true)
    private String requisitionNo;

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
     * 申领科室ID
     */
    @Column(name = "dept_id", length = 20, nullable = false)
    private String deptId;

    /**
     * 申领科室名称
     */
    @Column(name = "dept_name", length = 100)
    private String deptName;

    /**
     * 申领日期
     */
    @Column(name = "requisition_date", nullable = false)
    private LocalDate requisitionDate;

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
     * 审批人ID
     */
    @Column(name = "approver_id", length = 20)
    private String approverId;

    /**
     * 审批人姓名
     */
    @Column(name = "approver_name", length = 50)
    private String approverName;

    /**
     * 审批时间
     */
    @Column(name = "approve_time")
    private LocalDateTime approveTime;

    /**
     * 审批意见
     */
    @Column(name = "approve_remark", length = 200)
    private String approveRemark;

    /**
     * 发放人ID
     */
    @Column(name = "issuer_id", length = 20)
    private String issuerId;

    /**
     * 发放人姓名
     */
    @Column(name = "issuer_name", length = 50)
    private String issuerName;

    /**
     * 发放时间
     */
    @Column(name = "issue_time")
    private LocalDateTime issueTime;

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
    private RequisitionStatus status = RequisitionStatus.PENDING;

    /**
     * 申领明细
     */
    @OneToMany(mappedBy = "requisition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaterialRequisitionItem> items = new ArrayList<>();
}