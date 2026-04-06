package com.yhj.his.module.pacs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "机房排班DTO")
public class RoomScheduleDTO {

    @Schema(description = "排班ID(更新时必填)")
    private String id;

    @Schema(description = "机房号", required = true)
    private String roomNo;

    @Schema(description = "机房名称")
    private String roomName;

    @Schema(description = "设备ID")
    private String equipmentId;

    @Schema(description = "设备名称")
    private String equipmentName;

    @Schema(description = "排班日期", required = true)
    private LocalDate scheduleDate;

    @Schema(description = "班次", required = true)
    private String shift;

    @Schema(description = "开始时间", required = true)
    private LocalTime startTime;

    @Schema(description = "结束时间", required = true)
    private LocalTime endTime;

    @Schema(description = "总号源数", required = true)
    private Integer totalQuota = 0;

    @Schema(description = "检查类型限制")
    private String examTypeLimit;

    @Schema(description = "医生ID")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "技师ID")
    private String technicianId;

    @Schema(description = "技师姓名")
    private String technicianName;

    @Schema(description = "状态")
    private String status = "开放";

    @Schema(description = "备注")
    private String remark;
}