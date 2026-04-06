package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 检验报告生成DTO
 */
@Data
@Schema(description = "检验报告生成请求")
public class TestReportGenerateDTO {

    @Schema(description = "申请ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申请ID不能为空")
    private String requestId;

    @Schema(description = "样本ID")
    private String sampleId;

    @Schema(description = "报告类型")
    private String reportType;

    @Schema(description = "报告分类")
    private String reportCategory;

    @Schema(description = "检验人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "检验人ID不能为空")
    private String testerId;

    @Schema(description = "检验人姓名")
    private String testerName;

    @Schema(description = "报告时间")
    private LocalDateTime reportTime;

    @Schema(description = "备注")
    private String remark;
}