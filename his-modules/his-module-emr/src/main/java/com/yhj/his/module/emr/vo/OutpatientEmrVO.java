package com.yhj.his.module.emr.vo;

import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.QcLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门诊病历VO
 */
@Data
@Schema(description = "门诊病历详情")
public class OutpatientEmrVO {

    @Schema(description = "病历ID")
    private String id;

    @Schema(description = "就诊ID")
    private String visitId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "就诊日期")
    private LocalDate visitDate;

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

    @Schema(description = "主诉")
    private String chiefComplaint;

    @Schema(description = "现病史")
    private String presentIllness;

    @Schema(description = "既往史")
    private String pastHistory;

    @Schema(description = "个人史")
    private String personalHistory;

    @Schema(description = "家族史")
    private String familyHistory;

    @Schema(description = "过敏史")
    private String allergyHistory;

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

    @Schema(description = "状态")
    private EmrStatus status;

    @Schema(description = "质控评分")
    private Integer qcScore;

    @Schema(description = "质控等级")
    private QcLevel qcLevel;

    @Schema(description = "缺陷列表")
    private String qcDefects;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核人姓名")
    private String auditorName;
}