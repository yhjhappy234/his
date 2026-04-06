package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 检验结果审核DTO
 */
@Data
@Schema(description = "检验结果审核请求")
public class TestResultAuditDTO {

    @Schema(description = "结果ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "结果ID不能为空")
    private String resultId;

    @Schema(description = "审核人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "审核人ID不能为空")
    private String auditorId;

    @Schema(description = "审核人姓名")
    private String auditorName;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "备注")
    private String remark;
}