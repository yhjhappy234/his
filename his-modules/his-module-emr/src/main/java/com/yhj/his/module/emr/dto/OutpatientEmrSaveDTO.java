package com.yhj.his.module.emr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 门诊病历保存DTO
 */
@Data
@Schema(description = "门诊病历保存请求")
public class OutpatientEmrSaveDTO {

    @Schema(description = "病历ID(更新时必填)")
    private String id;

    @NotBlank(message = "就诊ID不能为空")
    @Schema(description = "就诊ID", required = true)
    private String visitId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @NotBlank(message = "科室ID不能为空")
    @Schema(description = "科室ID", required = true)
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @NotBlank(message = "医生ID不能为空")
    @Schema(description = "医生ID", required = true)
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @NotBlank(message = "主诉不能为空")
    @Size(min = 10, max = 500, message = "主诉长度应在10-500字之间")
    @Schema(description = "主诉", required = true)
    private String chiefComplaint;

    @NotBlank(message = "现病史不能为空")
    @Size(min = 50, message = "现病史应至少50字")
    @Schema(description = "现病史", required = true)
    private String presentIllness;

    @Schema(description = "既往史")
    private String pastHistory;

    @Schema(description = "个人史")
    private String personalHistory;

    @Schema(description = "家族史")
    private String familyHistory;

    @Schema(description = "过敏史")
    private String allergyHistory;

    // 体格检查
    @Schema(description = "体温")
    private BigDecimal temperature;

    @Schema(description = "脉搏")
    private Integer pulse;

    @Schema(description = "呼吸")
    private Integer respiration;

    @Schema(description = "血压")
    private String bloodPressure;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "身高(cm)")
    private Integer height;

    @Schema(description = "一般检查")
    private String generalExam;

    @Schema(description = "专科检查")
    private String specialistExam;

    @Schema(description = "辅助检查")
    private String auxiliaryExam;

    // 诊断
    @Schema(description = "主要诊断编码")
    private String primaryDiagnosisCode;

    @Schema(description = "主要诊断名称")
    private String primaryDiagnosisName;

    @Schema(description = "次要诊断")
    private String secondaryDiagnosis;

    @Schema(description = "治疗方案")
    private String treatmentPlan;

    @Schema(description = "医嘱/注意事项")
    private String medicalAdvice;

    @Schema(description = "模板ID")
    private String templateId;
}