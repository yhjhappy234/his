package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 考勤统计VO
 */
@Data
@Schema(description = "考勤统计")
public class AttendanceStatisticsVO {

    @Schema(description = "员工ID")
    private String employeeId;

    @Schema(description = "工号")
    private String employeeNo;

    @Schema(description = "姓名")
    private String employeeName;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "统计开始日期")
    private LocalDate startDate;

    @Schema(description = "统计结束日期")
    private LocalDate endDate;

    @Schema(description = "应出勤天数")
    private Integer shouldWorkDays;

    @Schema(description = "实际出勤天数")
    private Integer actualWorkDays;

    @Schema(description = "迟到次数")
    private Long lateTimes;

    @Schema(description = "迟到总分钟")
    private Integer totalLateMinutes;

    @Schema(description = "早退次数")
    private Long earlyLeaveTimes;

    @Schema(description = "早退总分钟")
    private Integer totalEarlyMinutes;

    @Schema(description = "旷工天数")
    private Integer absentDays;

    @Schema(description = "请假天数")
    private Double leaveDays;

    @Schema(description = "加班时长(小时)")
    private Double overtimeHours;

    @Schema(description = "正常打卡次数")
    private Long normalTimes;
}