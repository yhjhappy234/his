package com.yhj.his.module.emr.dto;

import com.yhj.his.module.emr.enums.ProgressRecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 病程记录保存DTO
 */
@Data
@Schema(description = "病程记录保存请求")
public class ProgressRecordSaveDTO {

    @Schema(description = "记录ID(更新时必填)")
    private String id;

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID", required = true)
    private String admissionId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @NotNull(message = "记录类型不能为空")
    @Schema(description = "记录类型", required = true)
    private ProgressRecordType recordType;

    @Schema(description = "记录标题")
    private String recordTitle;

    @NotNull(message = "记录日期不能为空")
    @Schema(description = "记录日期", required = true)
    private LocalDate recordDate;

    @NotBlank(message = "记录内容不能为空")
    @Schema(description = "记录内容", required = true)
    private String recordContent;

    @NotBlank(message = "医生ID不能为空")
    @Schema(description = "医生ID", required = true)
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "医生职称")
    private String doctorTitle;

    @Schema(description = "关联手术ID")
    private String operationId;

    @Schema(description = "关联会诊ID")
    private String consultationId;
}