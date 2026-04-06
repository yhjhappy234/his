package com.yhj.his.module.emr.vo;

import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.QcLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 出院记录VO
 */
@Data
@Schema(description = "出院记录详情")
public class DischargeRecordVO {

    @Schema(description = "记录ID")
    private String id;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
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

    @Schema(description = "医生ID")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "科室名称")
    private String deptName;

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