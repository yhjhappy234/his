package com.yhj.his.module.system.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_parameter", indexes = {
        @Index(name = "idx_param_code", columnList = "param_code"),
        @Index(name = "idx_param_group", columnList = "param_group")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_param_code", columnNames = {"param_code"})
})
public class SystemParameter extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 参数编码
     */
    @Column(name = "param_code", length = 50, nullable = false)
    private String paramCode;

    /**
     * 参数名称
     */
    @Column(name = "param_name", length = 100, nullable = false)
    private String paramName;

    /**
     * 参数值
     */
    @Column(name = "param_value", length = 500)
    private String paramValue;

    /**
     * 参数类型
     */
    @Column(name = "param_type", length = 20)
    private String paramType;

    /**
     * 参数分组
     */
    @Column(name = "param_group", length = 50)
    private String paramGroup;

    /**
     * 参数描述
     */
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 是否系统参数
     */
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    /**
     * 是否可编辑
     */
    @Column(name = "is_editable", nullable = false)
    private Boolean isEditable = true;

    /**
     * 排序号
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}