package com.yhj.his.module.inpatient.dto;

import com.yhj.his.module.inpatient.enums.DischargeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 出院申请请求DTO
 */
@Data
@Schema(description = "出院申请请求")
public class DischargeApplyDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "出院类型不能为空")
    @Schema(description = "出院类型")
    private DischargeType dischargeType;

    @Schema(description = "出院诊断")
    private String dischargeDiagnosis;

    @Schema(description = "出院诊断编码")
    private String dischargeDiagnosisCode;

    @Schema(description = "治疗经过")
    private String treatmentSummary;

    @Schema(description = "出院医嘱")
    private String dischargeAdvice;

    @Schema(description = "复诊日期")
    private LocalDate followUpDate;

    @Schema(description = "申请医生ID")
    private String doctorId;

    @Schema(description = "申请医生姓名")
    private String doctorName;
}