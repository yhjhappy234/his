package com.yhj.his.module.voice.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.voice.enums.DeviceStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 音频设备实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "audio_device")
public class AudioDevice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 设备编码
     */
    @Column(name = "device_code", length = 30, nullable = false, unique = true)
    private String deviceCode;

    /**
     * 设备名称
     */
    @Column(name = "device_name", length = 100, nullable = false)
    private String deviceName;

    /**
     * 设备类型
     */
    @Column(name = "device_type", length = 20)
    private String deviceType;

    /**
     * 设备分组ID
     */
    @Column(name = "device_group_id", length = 36)
    private String deviceGroupId;

    /**
     * 设备分组名称
     */
    @Column(name = "device_group_name", length = 100)
    private String deviceGroupName;

    /**
     * Windows设备ID
     */
    @Column(name = "windows_device_id", length = 200)
    private String windowsDeviceId;

    /**
     * IP地址(网络音频设备)
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 端口号
     */
    @Column(name = "port")
    private Integer port;

    /**
     * 音量(0-100)
     */
    @Column(name = "volume")
    private Integer volume = 80;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    /**
     * 设备状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private DeviceStatus status = DeviceStatus.OFFLINE;

    /**
     * 最后心跳时间
     */
    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;

    /**
     * 安装位置
     */
    @Column(name = "location", length = 100)
    private String location;

    /**
     * 描述
     */
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 排序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}