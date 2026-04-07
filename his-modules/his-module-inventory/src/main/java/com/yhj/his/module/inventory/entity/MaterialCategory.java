package com.yhj.his.module.inventory.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物资分类实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "material_category", indexes = {
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_category_code", columnList = "category_code")
})
public class MaterialCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分类编码
     */
    @Column(name = "category_code", length = 30, nullable = false, unique = true)
    private String categoryCode;

    /**
     * 分类名称
     */
    @Column(name = "category_name", length = 50, nullable = false)
    private String categoryName;

    /**
     * 父分类ID
     */
    @Column(name = "parent_id", length = 36)
    private String parentId;

    /**
     * 分类层级
     */
    @Column(name = "level", nullable = false)
    private Integer level = 1;

    /**
     * 排序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;

    /**
     * 状态 (0-禁用, 1-启用)
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;
}