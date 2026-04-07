package com.yhj.his.module.outpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门诊病历VO
 */
@Data
@Schema(description = "门诊病历")
public class OutpatientRecordVO {

    @Schema(description = "病历ID")
    private String recordId;

    @Schema(description = "挂号ID")
    private String registrationId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "就诊序号")
    private String visitNo;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "医生ID")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "就诊日期")
    private LocalDate visitDate;

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

    @Schema(description = "诊断编码")
    private String diagnosisCode;

    @Schema(description = "诊断名称")
    private String diagnosisName;

    @Schema(description = "诊断类型")
    private String diagnosisType;

    @Schema(description = "治疗方案")
    private String treatmentPlan;

    @Schema(description = "医嘱")
    private String medicalAdvice;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;
}