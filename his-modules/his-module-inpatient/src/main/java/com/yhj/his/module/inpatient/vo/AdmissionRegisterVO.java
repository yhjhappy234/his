package com.yhj.his.module.inpatient.vo;

import com.yhj.his.module.inpatient.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 入院登记响应VO
 */
@Data
@Schema(description = "入院登记响应")
public class AdmissionRegisterVO {

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "住院号")
    private String admissionNo;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "床位号")
    private String bedNo;

    @Schema(description = "病房号")
    private String roomNo;

    @Schema(description = "病区名称")
    private String wardName;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "入院时间")
    private LocalDateTime admissionTime;

    @Schema(description = "入院类型")
    private AdmissionType admissionType;

    @Schema(description = "主治医生姓名")
    private String doctorName;

    @Schema(description = "责任护士姓名")
    private String nurseName;

    @Schema(description = "入院诊断")
    private String admissionDiagnosis;

    @Schema(description = "护理等级")
    private NursingLevel nursingLevel;

    @Schema(description = "饮食类型")
    private DietType dietType;

    @Schema(description = "预交金总额")
    private BigDecimal deposit;

    @Schema(description = "状态")
    private AdmissionStatus status;
}