package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 检查检验申请请求DTO
 */
@Data
@Schema(description = "检查检验申请请求")
public class ExaminationRequestDto {

    @NotBlank(message = "挂号ID不能为空")
    @Schema(description = "挂号ID", required = true)
    private String registrationId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @NotBlank(message = "申请类型不能为空")
    @Schema(description = "申请类型: 检验/检查", required = true)
    private String requestType;

    @Schema(description = "诊断编码")
    private String diagnosisCode;

    @Schema(description = "诊断名称")
    private String diagnosisName;

    @NotEmpty(message = "检查项目不能为空")
    @Schema(description = "检查/检验项目列表", required = true)
    private List<String> examItems;

    @Schema(description = "临床摘要")
    private String clinicalSummary;

    @Schema(description = "是否急诊")
    private Boolean isEmergency;

    @Schema(description = "备注")
    private String remark;
}