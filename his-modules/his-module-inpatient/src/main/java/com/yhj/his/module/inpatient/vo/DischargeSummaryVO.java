package com.yhj.his.module.inpatient.vo;

import com.yhj.his.module.inpatient.enums.DischargeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 出院小结VO
 */
@Data
@Schema(description = "出院小结")
public class DischargeSummaryVO {

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "住院号")
    private String admissionNo;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "入院时间")
    private LocalDateTime admissionTime;

    @Schema(description = "出院时间")
    private LocalDateTime dischargeTime;

    @Schema(description = "住院天数")
    private Integer totalDays;

    @Schema(description = "出院类型")
    private DischargeType dischargeType;

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

    @Schema(description = "总费用")
    private BigDecimal totalCost;

    @Schema(description = "医保支付")
    private BigDecimal insurancePayment;

    @Schema(description = "自付金额")
    private BigDecimal selfPayment;

    @Schema(description = "主治医生姓名")
    private String doctorName;
}