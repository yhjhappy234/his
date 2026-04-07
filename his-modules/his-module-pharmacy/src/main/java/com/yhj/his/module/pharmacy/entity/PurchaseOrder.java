package com.yhj.his.module.pharmacy.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.pharmacy.enums.PurchaseOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购订单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "purchase_order", indexes = {
        @Index(name = "idx_order_no", columnList = "order_no", unique = true),
        @Index(name = "idx_supplier_id", columnList = "supplier_id"),
        @Index(name = "idx_order_status", columnList = "status")
})
@Schema(description = "采购订单")
public class PurchaseOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单号")
    @Column(name = "order_no", length = 30, nullable = false, unique = true)
    private String orderNo;

    @Schema(description = "供应商ID")
    @Column(name = "supplier_id", length = 20, nullable = false)
    private String supplierId;

    @Schema(description = "供应商名称")
    @Column(name = "supplier_name", length = 100)
    private String supplierName;

    @Schema(description = "订单日期")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Schema(description = "预计到货日期")
    @Column(name = "expected_date")
    private LocalDate expectedDate;

    @Schema(description = "总数量")
    @Column(name = "total_quantity", precision = 10, scale = 2)
    private BigDecimal totalQuantity;

    @Schema(description = "总金额")
    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Schema(description = "状态")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PurchaseOrderStatus status = PurchaseOrderStatus.PENDING;

    @Schema(description = "申请人ID")
    @Column(name = "applicant_id", length = 20)
    private String applicantId;

    @Schema(description = "申请人姓名")
    @Column(name = "applicant_name", length = 50)
    private String applicantName;

    @Schema(description = "申请时间")
    @Column(name = "apply_time")
    private LocalDateTime applyTime;

    @Schema(description = "审核人ID")
    @Column(name = "auditor_id", length = 20)
    private String auditorId;

    @Schema(description = "审核人姓名")
    @Column(name = "auditor_name", length = 50)
    private String auditorName;

    @Schema(description = "审核时间")
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Schema(description = "审核意见")
    @Column(name = "audit_remark", length = 500)
    private String auditRemark;
}