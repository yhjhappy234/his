package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 库存预警实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "material_alert", indexes = {
    @Index(name = "idx_material_id", columnList = "material_id"),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_alert_type", columnList = "alert_type"),
    @Index(name = "idx_status", columnList = "status")
})
public class MaterialAlert extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 预警类型 (LOW_STOCK-库存下限, HIGH_STOCK-库存上限, EXPIRY-效期预警)
     */
    @Column(name = "alert_type", length = 20, nullable = false)
    private String alertType;

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
    @Column(name = "warehouse_id", length = 36)
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
     * 当前库存量
     */
    @Column(name = "current_quantity", precision = 10, scale = 2)
    private BigDecimal currentQuantity;

    /**
     * 预警阈值
     */
    @Column(name = "alert_threshold", precision = 10, scale = 2)
    private BigDecimal alertThreshold;

    /**
     * 有效期
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * 预警内容
     */
    @Column(name = "alert_content", length = 200)
    private String alertContent;

    /**
     * 状态 (0-未处理, 1-已处理)
     */
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    /**
     * 处理人ID
     */
    @Column(name = "handler_id", length = 20)
    private String handlerId;

    /**
     * 处理人姓名
     */
    @Column(name = "handler_name", length = 50)
    private String handlerName;

    /**
     * 处理时间
     */
    @Column(name = "handle_time")
    private java.time.LocalDateTime handleTime;

    /**
     * 处理备注
     */
    @Column(name = "handle_remark", length = 200)
    private String handleRemark;

    /**
     * 备注
     */
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
}