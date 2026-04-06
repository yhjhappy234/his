package com.yhj.his.module.pacs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "机房排班VO")
public class RoomScheduleVO {

    @Schema(description = "排班ID")
    private String id;

    @Schema(description = "机房号")
    private String roomNo;

    @Schema(description = "机房名称")
    private String roomName;

    @Schema(description = "设备ID")
    private String equipmentId;

    @Schema(description = "设备名称")
    private String equipmentName;

    @Schema(description = "排班日期")
    private LocalDate scheduleDate;

    @Schema(description = "班次")
    private String shift;

    @Schema(description = "开始时间")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    private LocalTime endTime;

    @Schema(description = "总号源数")
    private Integer totalQuota;

    @Schema(description = "已预约数")
    private Integer scheduledCount;

    @Schema(description = "剩余号源")
    private Integer availableQuota;

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
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDate createTime;
}