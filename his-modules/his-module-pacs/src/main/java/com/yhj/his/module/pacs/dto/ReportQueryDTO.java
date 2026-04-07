package com.yhj.his.module.pacs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "报告查询DTO")
public class ReportQueryDTO {

    @Schema(description = "报告编号")
    private String reportNo;

    @Schema(description = "检查ID")
    private String examId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "报告状态")
    private String reportStatus;

    @Schema(description = "书写医生ID")
    private String writerId;

    @Schema(description = "审核医生ID")
    private String reviewerId;

    @Schema(description = "书写时间起始")
    private LocalDateTime writeTimeStart;

    @Schema(description = "书写时间截止")
    private LocalDateTime writeTimeEnd;

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}