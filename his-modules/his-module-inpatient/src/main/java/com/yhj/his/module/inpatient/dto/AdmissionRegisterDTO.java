package com.yhj.his.module.inpatient.dto;

import com.yhj.his.module.inpatient.enums.AdmissionType;
import com.yhj.his.module.inpatient.enums.DietType;
import com.yhj.his.module.inpatient.enums.NursingLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 入院登记请求DTO
 */
@Data
@Schema(description = "入院登记请求")
public class AdmissionRegisterDTO {

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @NotBlank(message = "患者姓名不能为空")
    @Schema(description = "患者姓名")
    private String patientName;

    @NotBlank(message = "性别不能为空")
    @Schema(description = "性别")
    private String gender;

    @Schema(description = "身份证号")
    private String idCardNo;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "住址")
    private String address;

    @NotNull(message = "入院类型不能为空")
    @Schema(description = "入院类型")
    private AdmissionType admissionType;

    @Schema(description = "入院来源")
    private String admissionSource;

    @NotBlank(message = "科室ID不能为空")
    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @NotBlank(message = "病区ID不能为空")
    @Schema(description = "病区ID")
    private String wardId;

    @Schema(description = "病区名称")
    private String wardName;

    @Schema(description = "床位号")
    private String bedNo;

    @NotBlank(message = "主治医生ID不能为空")
    @Schema(description = "主治医生ID")
    private String doctorId;

    @Schema(description = "主治医生姓名")
    private String doctorName;

    @Schema(description = "责任护士ID")
    private String nurseId;

    @Schema(description = "责任护士姓名")
    private String nurseName;

    @Schema(description = "入院诊断")
    private String admissionDiagnosis;

    @Schema(description = "入院诊断编码(ICD-10)")
    private String admissionDiagnosisCode;

    @Schema(description = "护理等级")
    private NursingLevel nursingLevel;

    @Schema(description = "饮食类型")
    private DietType dietType;

    @Schema(description = "过敏信息")
    private String allergyInfo;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "医保卡号")
    private String insuranceNo;

    @Schema(description = "预交金金额")
    private BigDecimal deposit;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;
}