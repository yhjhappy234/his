package com.yhj.his.module.emr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 出院记录保存DTO
 */
@Data
@Schema(description = "出院记录保存请求")
public class DischargeRecordSaveDTO {

    @Schema(description = "记录ID(更新时必填)")
    private String id;

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID", required = true)
    private String admissionId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "入院日期")
    private LocalDate admissionDate;

    @Schema(description = "出院日期")
    private LocalDate dischargeDate;

    @Schema(description = "住院天数")
    private Integer hospitalDays;

    @Schema(description = "入院时情况")
    private String admissionSituation;

    @Schema(description = "诊疗经过")
    private String treatmentProcess;

    @Schema(description = "出院诊断编码")
    private String dischargeDiagnosisCode;

    @Schema(description = "出院诊断名称")
    private String dischargeDiagnosisName;

    @Schema(description = "出院时情况")
    private String dischargeCondition;

    @Schema(description = "出院医嘱")
    private String dischargeAdvice;

    @Schema(description = "出院带药")
    private String dischargeMedication;

    @Schema(description = "复诊日期")
    private LocalDate followUpDate;

    @Schema(description = "复诊科室")
    private String followUpDept;

    @NotBlank(message = "医生ID不能为空")
    @Schema(description = "医生ID", required = true)
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;
}