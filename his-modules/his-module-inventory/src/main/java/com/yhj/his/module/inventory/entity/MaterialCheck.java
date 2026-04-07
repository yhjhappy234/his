package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.inventory.enums.CheckStatus;
import com.yhj.his.module.inventory.enums.CheckType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 库存盘点实体
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"items"})
@Entity
@Table(name = "material_check", indexes = {
    @Index(name = "idx_check_no", columnList = "check_no"),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_check_date", columnList = "check_date")
})
public class MaterialCheck extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 盘点单号
     */
    @Column(name = "check_no", length = 30, nullable = false, unique = true)
    private String checkNo;

    /**
     * 盘点类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "check_type", length = 20, nullable = false)
    private CheckType checkType;

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
     * 盘点日期
     */
    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 盘点人ID
     */
    @Column(name = "checker_id", length = 20)
    private String checkerId;

    /**
     * 盘点人姓名
     */
    @Column(name = "checker_name", length = 50)
    private String checkerName;

    /**
     * 盘点物资数
     */
    @Column(name = "total_count")
    private Integer totalCount = 0;

    /**
     * 盘盈数量
     */
    @Column(name = "profit_count")
    private Integer profitCount = 0;

    /**
     * 盘亏数量
     */
    @Column(name = "loss_count")
    private Integer lossCount = 0;

    /**
     * 盘盈金额
     */
    @Column(name = "profit_amount", precision = 12, scale = 2)
    private java.math.BigDecimal profitAmount = java.math.BigDecimal.ZERO;

    /**
     * 盘亏金额
     */
    @Column(name = "loss_amount", precision = 12, scale = 2)
    private java.math.BigDecimal lossAmount = java.math.BigDecimal.ZERO;

    /**
     * 调整人ID
     */
    @Column(name = "adjuster_id", length = 20)
    private String adjusterId;

    /**
     * 调整人姓名
     */
    @Column(name = "adjuster_name", length = 50)
    private String adjusterName;

    /**
     * 调整时间
     */
    @Column(name = "adjust_time")
    private LocalDateTime adjustTime;

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
    private CheckStatus status = CheckStatus.PENDING;

    /**
     * 盘点明细
     */
    @OneToMany(mappedBy = "check", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaterialCheckItem> items = new ArrayList<>();
}