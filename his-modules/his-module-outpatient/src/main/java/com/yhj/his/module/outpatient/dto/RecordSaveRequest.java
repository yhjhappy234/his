package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 病历保存请求DTO
 */
@Data
@Schema(description = "病历保存请求")
public class RecordSaveRequest {

    @NotBlank(message = "挂号ID不能为空")
    @Schema(description = "挂号ID", required = true)
    private String registrationId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @Schema(description = "主诉")
    private String chiefComplaint;

    @Schema(description = "现病史")
    private String presentIllness;

    @Schema(description = "既往史")
    private String pastHistory;

    @Schema(description = "过敏史")
    private String allergyHistory;

    @Schema(description = "个人史")
    private String personalHistory;

    @Schema(description = "家族史")
    private String familyHistory;

    @Schema(description = "体温")
    private BigDecimal temperature;

    @Schema(description = "脉搏")
    private Integer pulse;

    @Schema(description = "呼吸")
    private Integer respiration;

    @Schema(description = "血压")
    private String bloodPressure;

    @Schema(description = "身高(cm)")
    private Integer height;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "体格检查")
    private String physicalExam;

    @Schema(description = "辅助检查")
    private String auxiliaryExam;

    @Schema(description = "诊断编码(ICD-10)")
    private String diagnosisCode;

    @Schema(description = "诊断名称")
    private String diagnosisName;

    @Schema(description = "诊断类型: 主要/次要")
    private String diagnosisType;

    @Schema(description = "治疗方案")
    private String treatmentPlan;

    @Schema(description = "医嘱/注意事项")
    private String medicalAdvice;

    @Schema(description = "状态: 草稿/已提交")
    private String status;
}