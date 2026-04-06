package com.yhj.his.module.outpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者信息VO
 */
@Data
@Schema(description = "患者信息")
public class PatientVO {

    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "患者唯一标识")
    private String patientId;

    @Schema(description = "身份证号")
    private String idCardNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "年龄")
    private Integer age;

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

    @Schema(description = "医保卡号")
    private String medicalInsuranceNo;

    @Schema(description = "患者状态")
    private String status;

    @Schema(description = "爽约次数")
    private Integer noShowCount;

    @Schema(description = "是否黑名单")
    private Boolean isBlacklist;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}