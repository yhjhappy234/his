package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 排班信息VO
 */
@Data
@Schema(description = "排班信息")
public class ScheduleVO {

    @Schema(description = "排班ID")
    private String id;

    @Schema(description = "员工ID")
    private String employeeId;

    @Schema(description = "工号")
    private String employeeNo;

    @Schema(description = "姓名")
    private String employeeName;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "排班日期")
    private LocalDate scheduleDate;

    @Schema(description = "星期几")
    private Integer weekDay;

    @Schema(description = "班次类型")
    private String scheduleType;

    @Schema(description = "上班时间")
    private LocalTime startTime;

    @Schema(description = "下班时间")
    private LocalTime endTime;

    @Schema(description = "工作地点")
    private String location;

    @Schema(description = "排班模板ID")
    private String templateId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人ID")
    private String creatorId;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}