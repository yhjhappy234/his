package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 考勤记录VO
 */
@Data
@Schema(description = "考勤记录")
public class AttendanceVO {

    @Schema(description = "考勤ID")
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

    @Schema(description = "考勤日期")
    private LocalDate attendanceDate;

    @Schema(description = "签到时间")
    private LocalTime clockInTime;

    @Schema(description = "签退时间")
    private LocalTime clockOutTime;

    @Schema(description = "应到班次")
    private String scheduleType;

    @Schema(description = "应到时间")
    private LocalTime scheduleStart;

    @Schema(description = "应退时间")
    private LocalTime scheduleEnd;

    @Schema(description = "考勤状态")
    private String attendanceStatus;

    @Schema(description = "迟到分钟")
    private Integer lateMinutes;

    @Schema(description = "早退分钟")
    private Integer earlyMinutes;

    @Schema(description = "请假类型")
    private String leaveType;

    @Schema(description = "请假申请ID")
    private String leaveId;

    @Schema(description = "加班时长(小时)")
    private BigDecimal overtimeHours;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}