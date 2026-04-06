package com.yhj.his.module.system.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_dictionary", indexes = {
        @Index(name = "idx_dict_type", columnList = "dict_type"),
        @Index(name = "idx_parent_code", columnList = "parent_code")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_dict_type_code", columnNames = {"dict_type", "dict_code"})
})
public class DataDictionary extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 字典类型
     */
    @Column(name = "dict_type", length = 50, nullable = false)
    private String dictType;

    /**
     * 字典编码
     */
    @Column(name = "dict_code", length = 50, nullable = false)
    private String dictCode;

    /**
     * 字典名称
     */
    @Column(name = "dict_name", length = 100, nullable = false)
    private String dictName;

    /**
     * 字典值
     */
    @Column(name = "dict_value", length = 200)
    private String dictValue;

    /**
     * 父级编码
     */
    @Column(name = "parent_code", length = 50)
    private String parentCode;

    /**
     * 层级
     */
    @Column(name = "dict_level", nullable = false)
    private Integer dictLevel = 1;

    /**
     * 排序号
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    /**
     * 是否默认值
     */
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    /**
     * 描述
     */
    @Column(name = "description", length = 200)
    private String description;
}