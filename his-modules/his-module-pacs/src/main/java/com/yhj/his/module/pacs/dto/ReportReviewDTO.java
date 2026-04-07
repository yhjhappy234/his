package com.yhj.his.module.pacs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "报告审核DTO")
public class ReportReviewDTO {

    @Schema(description = "报告ID", required = true)
    @NotBlank(message = "报告ID不能为空")
    private String reportId;

    @Schema(description = "是否通过", required = true)
    private Boolean approved;

    @Schema(description = "审核医生ID")
    private String reviewerId;

    @Schema(description = "审核医生姓名")
    private String reviewerName;

    @Schema(description = "审核意见")
    private String reviewComment;
}