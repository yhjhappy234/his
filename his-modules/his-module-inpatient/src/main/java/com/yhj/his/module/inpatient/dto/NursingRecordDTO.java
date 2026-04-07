package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 护理记录请求DTO
 */
@Data
@Schema(description = "护理记录请求")
public class NursingRecordDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @NotBlank(message = "记录时间不能为空")
    @Schema(description = "记录时间")
    private LocalDateTime recordTime;

    @Schema(description = "入量(ml)")
    private BigDecimal intake;

    @Schema(description = "出量(ml)")
    private BigDecimal output;

    @Schema(description = "尿量(ml)")
    private BigDecimal urine;

    @Schema(description = "大便情况")
    private String stool;

    @Schema(description = "护理内容")
    private String nursingContent;

    @Schema(description = "护理措施")
    private String nursingMeasures;

    @NotBlank(message = "记录护士ID不能为空")
    @Schema(description = "记录护士ID")
    private String nurseId;

    @Schema(description = "记录护士姓名")
    private String nurseName;
}