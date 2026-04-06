package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报告通知请求
 */
@Data
@Schema(description = "报告通知请求")
public class ReportNoticeRequest {

    @Schema(description = "患者姓名", required = true)
    @NotBlank(message = "患者姓名不能为空")
    private String patientName;

    @Schema(description = "报告类型: LAB-检验, RADIOLOGY-影像, PATHOLOGY-病理", required = true)
    @NotBlank(message = "报告类型不能为空")
    private String reportType;

    @Schema(description = "检查类型/项目名称")
    private String examType;

    @Schema(description = "取报告窗口", required = true)
    @NotBlank(message = "取报告窗口不能为空")
    private String windowNo;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "目标设备分组编码列表")
    private String[] targetGroups;

    @Schema(description = "目标设备ID列表")
    private String[] targetDevices;

    @Schema(description = "关联业务ID")
    private String bizId;
}