package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 设备分组请求
 */
@Data
@Schema(description = "设备分组请求")
public class DeviceGroupRequest {

    @Schema(description = "分组编码", required = true, example = "OUTPATIENT_HALL")
    @NotBlank(message = "分组编码不能为空")
    private String groupCode;

    @Schema(description = "分组名称", required = true)
    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    @Schema(description = "父分组ID")
    private String parentId;

    @Schema(description = "分组类型")
    private String groupType;

    @Schema(description = "位置描述")
    private String location;

    @Schema(description = "默认音量(0-100)", example = "80")
    private Integer defaultVolume = 80;

    @Schema(description = "排序号")
    private Integer sortOrder = 0;

    @Schema(description = "是否启用")
    private Boolean isEnabled = true;

    @Schema(description = "描述")
    private String description;
}