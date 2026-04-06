package com.yhj.his.module.pacs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备信息VO")
public class EquipmentVO {

    @Schema(description = "设备ID")
    private String id;

    @Schema(description = "设备编码")
    private String equipmentCode;

    @Schema(description = "设备名称")
    private String equipmentName;

    @Schema(description = "设备类型")
    private String equipmentType;

    @Schema(description = "设备型号")
    private String model;

    @Schema(description = "制造商")
    private String manufacturer;

    @Schema(description = "序列号")
    private String serialNumber;

    @Schema(description = "AE标题")
    private String aeTitle;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "端口号")
    private Integer port;

    @Schema(description = "机房号")
    private String roomNo;

    @Schema(description = "机房名称")
    private String roomName;

    @Schema(description = "购买日期")
    private String purchaseDate;

    @Schema(description = "启用日期")
    private String enableDate;

    @Schema(description = "设备状态")
    private String status;

    @Schema(description = "管理员ID")
    private String managerId;

    @Schema(description = "管理员姓名")
    private String managerName;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private String createTime;
}