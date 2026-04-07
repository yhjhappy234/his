package com.yhj.his.module.system.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科室实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_department", indexes = {
        @Index(name = "idx_dept_code", columnList = "dept_code"),
        @Index(name = "idx_parent_id", columnList = "parent_id"),
        @Index(name = "idx_dept_type", columnList = "dept_type")
})
public class Department extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 科室编码
     */
    @Column(name = "dept_code", length = 20, nullable = false, unique = true)
    private String deptCode;

    /**
     * 科室名称
     */
    @Column(name = "dept_name", length = 100, nullable = false)
    private String deptName;

    /**
     * 科室简称
     */
    @Column(name = "short_name", length = 50)
    private String shortName;

    /**
     * 父级ID
     */
    @Column(name = "parent_id", length = 36)
    private String parentId;

    /**
     * 层级
     */
    @Column(name = "dept_level", nullable = false)
    private Integer deptLevel = 1;

    /**
     * 科室类型(临床/医技/行政/后勤)
     */
    @Column(name = "dept_type", length = 20, nullable = false)
    private String deptType;

    /**
     * 科室负责人
     */
    @Column(name = "leader_id", length = 36)
    private String leaderId;

    /**
     * 科室负责人姓名
     */
    @Column(name = "leader_name", length = 50)
    private String leaderName;

    /**
     * 联系电话
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 科室地址
     */
    @Column(name = "address", length = 200)
    private String address;

    /**
     * 排序号
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * 状态
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "NORMAL";

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}