package com.yhj.his.module.emr.vo;

import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.QcLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入院记录VO
 */
@Data
@Schema(description = "入院记录详情")
public class AdmissionRecordVO {

    @Schema(description = "记录ID")
    private String id;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "入院日期")
    private LocalDate admissionDate;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "病区ID")
    private String wardId;

    @Schema(description = "床位号")
    private String bedNo;

    @Schema(description = "主诉")
    private String chiefComplaint;

    @Schema(description = "现病史")
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

    @Schema(description = "入院诊断编码")
    private String admissionDiagnosisCode;

    @Schema(description = "入院诊断名称")
    private String admissionDiagnosisName;

    @Schema(description = "诊疗计划")
    private String treatmentPlan;

    @Schema(description = "医生ID")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "书写时间")
    private LocalDateTime recordTime;

    @Schema(description = "上级医生姓名")
    private String superiorDoctorName;

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
}