package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 取药提醒请求
 */
@Data
@Schema(description = "取药提醒请求")
public class MedicationNoticeRequest {

    @Schema(description = "患者姓名", required = true)
    @NotBlank(message = "患者姓名不能为空")
    private String patientName;

    @Schema(description = "药房窗口号", required = true)
    @NotBlank(message = "窗口号不能为空")
    private String windowNo;

    @Schema(description = "药房名称")
    private String pharmacyName;

    @Schema(description = "取药码")
    private String medicationCode;

    @Schema(description = "目标设备分组编码列表")
    private String[] targetGroups;

    @Schema(description = "目标设备ID列表")
    private String[] targetDevices;

    @Schema(description = "关联业务ID")
    private String bizId;
}