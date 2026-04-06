package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 危急值通知请求
 */
@Data
@Schema(description = "危急值通知请求")
public class CriticalValueRequest {

    @Schema(description = "科室名称", required = true)
    @NotBlank(message = "科室名称不能为空")
    private String deptName;

    @Schema(description = "床号")
    private String bedNo;

    @Schema(description = "患者姓名", required = true)
    @NotBlank(message = "患者姓名不能为空")
    private String patientName;

    @Schema(description = "检验/检查项目", required = true)
    @NotBlank(message = "项目名称不能为空")
    private String itemName;

    @Schema(description = "数值/结果")
    private String value;

    @Schema(description = "目标设备分组编码列表")
    private String[] targetGroups;

    @Schema(description = "目标设备ID列表")
    private String[] targetDevices;

    @Schema(description = "关联业务ID")
    private String bizId;
}