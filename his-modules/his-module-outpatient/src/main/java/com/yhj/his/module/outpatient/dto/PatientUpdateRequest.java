package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 患者信息更新请求DTO
 */
@Data
@Schema(description = "患者信息更新请求")
public class PatientUpdateRequest {

    @Schema(description = "患者ID", required = true)
    private String patientId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "现住址")
    private String address;

    @Schema(description = "紧急联系人")
    private String emergencyContact;

    @Schema(description = "紧急联系电话")
    private String emergencyPhone;

    @Schema(description = "血型")
    private String bloodType;

    @Schema(description = "过敏史")
    private String allergyHistory;

    @Schema(description = "病史")
    private String medicalHistory;

    @Schema(description = "备注")
    private String remark;
}