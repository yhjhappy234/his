package com.yhj.his.module.system.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.system.enums.DataScopeLevel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role", indexes = {
        @Index(name = "idx_role_code", columnList = "role_code")
})
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色编码
     */
    @Column(name = "role_code", length = 30, nullable = false, unique = true)
    private String roleCode;

    /**
     * 角色名称
     */
    @Column(name = "role_name", length = 50, nullable = false)
    private String roleName;

    /**
     * 角色描述
     */
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 数据权限级别
     */
    @Column(name = "data_scope", length = 20)
    @Enumerated(EnumType.STRING)
    private DataScopeLevel dataScope;

    /**
     * 排序号
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * 是否系统角色(不可删除)
     */
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

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