package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.inventory.enums.WarehouseType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库房信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "warehouse", indexes = {
    @Index(name = "idx_warehouse_code", columnList = "warehouse_code"),
    @Index(name = "idx_warehouse_type", columnList = "warehouse_type"),
    @Index(name = "idx_dept_id", columnList = "dept_id")
})
public class Warehouse extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 库房编码
     */
    @Column(name = "warehouse_code", length = 20, nullable = false, unique = true)
    private String warehouseCode;

    /**
     * 库房名称
     */
    @Column(name = "warehouse_name", length = 100, nullable = false)
    private String warehouseName;

    /**
     * 库房类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "warehouse_type", length = 20)
    private WarehouseType warehouseType;

    /**
     * 所属科室ID
     */
    @Column(name = "dept_id", length = 20)
    private String deptId;

    /**
     * 科室名称
     */
    @Column(name = "dept_name", length = 100)
    private String deptName;

    /**
     * 位置
     */
    @Column(name = "location", length = 100)
    private String location;

    /**
     * 管理员ID
     */
    @Column(name = "manager_id", length = 20)
    private String managerId;

    /**
     * 管理员姓名
     */
    @Column(name = "manager_name", length = 50)
    private String managerName;

    /**
     * 联系电话
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 状态 (0-禁用, 1-启用)
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;
}