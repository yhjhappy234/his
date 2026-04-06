package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 检验报告发布DTO
 */
@Data
@Schema(description = "检验报告发布请求")
public class TestReportPublishDTO {

    @Schema(description = "报告ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "报告ID不能为空")
    private String reportId;

    @Schema(description = "发布人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "发布人ID不能为空")
    private String publisherId;

    @Schema(description = "发布人姓名")
    private String publisherName;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "备注")
    private String remark;
}