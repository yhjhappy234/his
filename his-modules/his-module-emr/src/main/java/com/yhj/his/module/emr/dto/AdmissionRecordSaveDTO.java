package com.yhj.his.module.emr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 入院记录保存DTO
 */
@Data
@Schema(description = "入院记录保存请求")
public class AdmissionRecordSaveDTO {

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

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "年龄")
    private Integer age;

    @NotBlank(message = "科室ID不能为空")
    @Schema(description = "科室ID", required = true)
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "病区ID")
    private String wardId;

    @Schema(description = "床位号")
    private String bedNo;

    @NotBlank(message = "主诉不能为空")
    @Size(min = 10, max = 500, message = "主诉长度应在10-500字之间")
    @Schema(description = "主诉", required = true)
    private String chiefComplaint;

    @NotBlank(message = "现病史不能为空")
    @Size(min = 100, message = "现病史应至少100字")
    @Schema(description = "现病史", required = true)
    private String presentIllness;

    @Schema(description = "既往史")
    private String pastHistory;

    @Schema(description = "个人史")
    private String personalHistory;

    @Schema(description = "婚育史")
    private String marriageHistory;

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
    @Schema(description = "入院诊断编码")
    private String admissionDiagnosisCode;

    @Schema(description = "入院诊断名称")
    private String admissionDiagnosisName;

    @Schema(description = "诊疗计划")
    private String treatmentPlan;

    @NotBlank(message = "医生ID不能为空")
    @Schema(description = "医生ID", required = true)
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "上级医生ID")
    private String superiorDoctorId;

    @Schema(description = "上级医生姓名")
    private String superiorDoctorName;
}