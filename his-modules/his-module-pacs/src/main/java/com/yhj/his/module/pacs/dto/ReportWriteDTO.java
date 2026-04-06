package com.yhj.his.module.pacs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "报告书写DTO")
public class ReportWriteDTO {

    @Schema(description = "报告ID(更新时必填)")
    private String reportId;

    @Schema(description = "检查ID", required = true)
    @NotBlank(message = "检查ID不能为空")
    private String examId;

    @Schema(description = "申请ID")
    private String requestId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "报告类型")
    private String reportType;

    @Schema(description = "报告模板ID")
    private String reportTemplateId;

    @Schema(description = "检查所见", required = true)
    @NotBlank(message = "检查所见不能为空")
    private String examDescription;

    @Schema(description = "诊断结论", required = true)
    @NotBlank(message = "诊断结论不能为空")
    private String diagnosisResult;

    @Schema(description = "诊断编码")
    private String diagnosisCode;

    @Schema(description = "诊断名称")
    private String diagnosisName;

    @Schema(description = "关键影像")
    private String keyImages;

    @Schema(description = "书写医生ID")
    private String writerId;

    @Schema(description = "书写医生姓名")
    private String writerName;

    @Schema(description = "备注")
    private String remark;
}