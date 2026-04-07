package com.yhj.his.module.pacs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "检查申请DTO")
public class ExamRequestDTO {

    @Schema(description = "患者ID", required = true)
    @NotBlank(message = "患者ID不能为空")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "身份证号")
    private String idCardNo;

    @Schema(description = "就诊类型", required = true)
    @NotBlank(message = "就诊类型不能为空")
    private String visitType;

    @Schema(description = "就诊ID")
    private String visitId;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "申请科室ID", required = true)
    @NotBlank(message = "申请科室ID不能为空")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "申请医生ID")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "临床诊断")
    private String clinicalDiagnosis;

    @Schema(description = "临床信息")
    private String clinicalInfo;

    @Schema(description = "检查目的")
    private String examPurpose;

    @Schema(description = "检查项目ID", required = true)
    @NotBlank(message = "检查项目ID不能为空")
    private String itemId;

    @Schema(description = "检查部位")
    private String examPart;

    @Schema(description = "检查方法")
    private String examMethod;

    @Schema(description = "是否急诊")
    private Boolean isEmergency = false;

    @Schema(description = "急诊级别")
    private String emergencyLevel;

    @Schema(description = "费用")
    private BigDecimal totalAmount;

    @Schema(description = "支付状态")
    private String payStatus;

    @Schema(description = "备注")
    private String remark;
}