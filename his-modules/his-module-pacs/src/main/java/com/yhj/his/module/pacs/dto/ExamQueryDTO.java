package com.yhj.his.module.pacs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "检查查询DTO")
public class ExamQueryDTO {

    @Schema(description = "申请单号")
    private String requestNo;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "就诊类型")
    private String visitType;

    @Schema(description = "检查类型")
    private String examType;

    @Schema(description = "申请状态")
    private String status;

    @Schema(description = "申请科室ID")
    private String deptId;

    @Schema(description = "是否急诊")
    private Boolean isEmergency;

    @Schema(description = "申请时间起始")
    private LocalDateTime requestTimeStart;

    @Schema(description = "申请时间截止")
    private LocalDateTime requestTimeEnd;

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}