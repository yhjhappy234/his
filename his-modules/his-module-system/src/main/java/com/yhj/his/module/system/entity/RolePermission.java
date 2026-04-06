package com.yhj.his.module.system.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role_permission", indexes = {
        @Index(name = "idx_rp_role_id", columnList = "role_id"),
        @Index(name = "idx_rp_perm_id", columnList = "perm_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_perm", columnNames = {"role_id", "perm_id"})
})
public class RolePermission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @Column(name = "role_id", length = 36, nullable = false)
    private String roleId;

    /**
     * 权限ID
     */
    @Column(name = "perm_id", length = 36, nullable = false)
    private String permId;
}