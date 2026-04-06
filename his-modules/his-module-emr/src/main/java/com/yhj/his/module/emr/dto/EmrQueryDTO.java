package com.yhj.his.module.emr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 病历查询DTO
 */
@Data
@Schema(description = "病历查询请求")
public class EmrQueryDTO {

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "医生ID")
    private String doctorId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}