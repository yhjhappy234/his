package com.yhj.his.module.pharmacy.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.pharmacy.enums.InventoryOperationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存流水实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inventory_transaction", indexes = {
        @Index(name = "idx_trans_drug_id", columnList = "drug_id"),
        @Index(name = "idx_trans_pharmacy_id", columnList = "pharmacy_id"),
        @Index(name = "idx_trans_type_time", columnList = "transaction_type, operate_time"),
        @Index(name = "idx_trans_related_id", columnList = "related_id")
})
@Schema(description = "库存流水")
public class InventoryTransaction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "流水号")
    @Column(name = "transaction_no", length = 30, nullable = false)
    private String transactionNo;

    @Schema(description = "操作类型")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 20, nullable = false)
    private InventoryOperationType transactionType;

    @Schema(description = "药品ID")
    @Column(name = "drug_id", length = 36, nullable = false)
    private String drugId;

    @Schema(description = "药品编码")
    @Column(name = "drug_code", length = 30)
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

    @Schema(description = "批号")
    @Column(name = "batch_no", length = 50)
    private String batchNo;

    @Schema(description = "有效期")
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Schema(description = "变动前数量")
    @Column(name = "quantity_before", precision = 10, scale = 2)
    private BigDecimal quantityBefore;

    @Schema(description = "变动数量(正数入库/负数出库)")
    @Column(name = "quantity_change", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantityChange;

    @Schema(description = "变动后数量")
    @Column(name = "quantity_after", precision = 10, scale = 2)
    private BigDecimal quantityAfter;

    @Schema(description = "零售价")
    @Column(name = "retail_price", precision = 10, scale = 4)
    private BigDecimal retailPrice;

    @Schema(description = "进价")
    @Column(name = "purchase_price", precision = 10, scale = 4)
    private BigDecimal purchasePrice;

    @Schema(description = "金额")
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Schema(description = "关联单据ID")
    @Column(name = "related_id", length = 36)
    private String relatedId;

    @Schema(description = "关联单据号")
    @Column(name = "related_no", length = 30)
    private String relatedNo;

    @Schema(description = "原因")
    @Column(name = "reason", length = 200)
    private String reason;

    @Schema(description = "操作员ID")
    @Column(name = "operator_id", length = 20)
    private String operatorId;

    @Schema(description = "操作员姓名")
    @Column(name = "operator_name", length = 50)
    private String operatorName;

    @Schema(description = "操作时间")
    @Column(name = "operate_time", nullable = false)
    private LocalDateTime operateTime;
}