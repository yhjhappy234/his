package com.yhj.his.module.voice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 音频设备VO
 */
@Data
@Schema(description = "音频设备响应")
public class AudioDeviceVO {

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "设备分组ID")
    private String deviceGroupId;

    @Schema(description = "设备分组名称")
    private String deviceGroupName;

    @Schema(description = "Windows设备ID")
    private String windowsDeviceId;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "端口")
    private Integer port;

    @Schema(description = "音量")
    private Integer volume;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @Schema(description = "设备状态")
    private String status;

    @Schema(description = "设备状态描述")
    private String statusDesc;

    @Schema(description = "最后心跳时间")
    private LocalDateTime lastHeartbeat;

    @Schema(description = "安装位置")
    private String location;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}