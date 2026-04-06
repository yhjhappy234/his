package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 出院小结请求DTO
 */
@Data
@Schema(description = "出院小结请求")
public class DischargeSummaryDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "入院诊断")
    private String admissionDiagnosis;

    @Schema(description = "出院诊断")
    private String dischargeDiagnosis;

    @Schema(description = "主要诊断")
    private String mainDiagnosis;

    @Schema(description = "其他诊断")
    private String secondaryDiagnosis;

    @Schema(description = "手术情况")
    private String operationInfo;

    @Schema(description = "治疗经过")
    private String treatmentSummary;

    @Schema(description = "出院医嘱")
    private String dischargeAdvice;

    @Schema(description = "复诊日期")
    private LocalDate followUpDate;

    @Schema(description = "书写医生ID")
    private String doctorId;

    @Schema(description = "书写医生姓名")
    private String doctorName;
}