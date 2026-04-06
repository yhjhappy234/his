package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 患者建档请求DTO
 */
@Data
@Schema(description = "患者建档请求")
public class PatientCreateRequest {

    @Schema(description = "身份证号")
    private String idCardNo;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名", required = true)
    private String name;

    @NotBlank(message = "性别不能为空")
    @Schema(description = "性别: 男/女/未知", required = true)
    private String gender;

    @NotNull(message = "出生日期不能为空")
    @Schema(description = "出生日期", required = true)
    private LocalDate birthDate;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "现住址")
    private String address;

    @Schema(description = "紧急联系人")
    private String emergencyContact;

    @Schema(description = "紧急联系电话")
    private String emergencyPhone;

    @Schema(description = "血型: A/B/AB/O/未知")
    private String bloodType;

    @Schema(description = "过敏史")
    private String allergyHistory;

    @Schema(description = "病史")
    private String medicalHistory;

    @Schema(description = "医保卡号")
    private String medicalInsuranceNo;

    @Schema(description = "备注")
    private String remark;
}