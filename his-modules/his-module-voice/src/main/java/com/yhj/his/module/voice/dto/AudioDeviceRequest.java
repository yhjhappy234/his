package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 音频设备请求
 */
@Data
@Schema(description = "音频设备请求")
public class AudioDeviceRequest {

    @Schema(description = "设备编码", required = true, example = "SPEAKER_01")
    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    @Schema(description = "设备名称", required = true)
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "设备分组ID")
    private String deviceGroupId;

    @Schema(description = "Windows设备ID")
    private String windowsDeviceId;

    @Schema(description = "IP地址(网络音频设备)")
    private String ipAddress;

    @Schema(description = "端口号")
    private Integer port;

    @Schema(description = "音量(0-100)", example = "80")
    @NotNull(message = "音量不能为空")
    private Integer volume;

    @Schema(description = "是否启用")
    private Boolean isEnabled = true;

    @Schema(description = "安装位置")
    private String location;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序号")
    private Integer sortOrder = 0;
}