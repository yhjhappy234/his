package com.yhj.his.module.system.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.system.enums.PermissionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_permission", indexes = {
        @Index(name = "idx_perm_code", columnList = "perm_code"),
        @Index(name = "idx_parent_id", columnList = "parent_id")
})
public class Permission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 权限编码
     */
    @Column(name = "perm_code", length = 100, nullable = false, unique = true)
    private String permCode;

    /**
     * 权限名称
     */
    @Column(name = "perm_name", length = 50, nullable = false)
    private String permName;

    /**
     * 权限类型
     */
    @Column(name = "perm_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionType permType;

    /**
     * 父级ID
     */
    @Column(name = "parent_id", length = 36)
    private String parentId;

    /**
     * 路径/接口地址
     */
    @Column(name = "path", length = 200)
    private String path;

    /**
     * 图标
     */
    @Column(name = "icon", length = 100)
    private String icon;

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