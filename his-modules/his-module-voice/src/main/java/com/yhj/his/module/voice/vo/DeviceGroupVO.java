package com.yhj.his.module.voice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备分组VO
 */
@Data
@Schema(description = "设备分组响应")
public class DeviceGroupVO {

    @Schema(description = "分组ID")
    private String groupId;

    @Schema(description = "分组编码")
    private String groupCode;

    @Schema(description = "分组名称")
    private String groupName;

    @Schema(description = "父分组ID")
    private String parentId;

    @Schema(description = "父分组名称")
    private String parentName;

    @Schema(description = "分组类型")
    private String groupType;

    @Schema(description = "位置描述")
    private String location;

    @Schema(description = "默认音量")
    private Integer defaultVolume;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "设备数量")
    private Integer deviceCount;

    @Schema(description = "设备列表")
    private List<AudioDeviceVO> devices;

    @Schema(description = "子分组列表")
    private List<DeviceGroupVO> children;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}