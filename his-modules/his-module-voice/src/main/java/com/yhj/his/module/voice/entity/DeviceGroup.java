package com.yhj.his.module.voice.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备分组实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "device_group")
public class DeviceGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分组编码
     */
    @Column(name = "group_code", length = 30, nullable = false, unique = true)
    private String groupCode;

    /**
     * 分组名称
     */
    @Column(name = "group_name", length = 100, nullable = false)
    private String groupName;

    /**
     * 父分组ID
     */
    @Column(name = "parent_id", length = 36)
    private String parentId;

    /**
     * 分组类型
     */
    @Column(name = "group_type", length = 20)
    private String groupType;

    /**
     * 位置描述
     */
    @Column(name = "location", length = 100)
    private String location;

    /**
     * 默认音量
     */
    @Column(name = "default_volume")
    private Integer defaultVolume = 80;

    /**
     * 排序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    /**
     * 描述
     */
    @Column(name = "description", length = 200)
    private String description;
}