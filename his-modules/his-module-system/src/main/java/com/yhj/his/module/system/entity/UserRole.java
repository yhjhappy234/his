package com.yhj.his.module.system.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user_role", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_role_id", columnList = "role_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_role", columnNames = {"user_id", "role_id"})
})
public class UserRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    /**
     * 角色ID
     */
    @Column(name = "role_id", length = 36, nullable = false)
    private String roleId;
}