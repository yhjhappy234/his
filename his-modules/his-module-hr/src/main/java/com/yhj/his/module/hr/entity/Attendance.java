package com.yhj.his.module.hr.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.hr.enums.AttendanceStatus;
import com.yhj.his.module.hr.enums.LeaveType;
import com.yhj.his.module.hr.enums.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 考勤记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_attendance", indexes = {
    @Index(name = "idx_att_emp_date", columnList = "employee_id, attendance_date"),
    @Index(name = "idx_att_date", columnList = "attendance_date"),
    @Index(name = "idx_att_dept", columnList = "dept_id")
})
@Schema(description = "考勤记录")
public class Attendance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "employee_id", length = 36, nullable = false)
    @Schema(description = "员工ID")
    private String employeeId;

    @Column(name = "employee_no", length = 20)
    @Schema(description = "工号")
    private String employeeNo;

    @Column(name = "employee_name", length = 50)
    @Schema(description = "姓名")
    private String employeeName;

    @Column(name = "dept_id", length = 36)
    @Schema(description = "科室ID")
    private String deptId;

    @Column(name = "dept_name", length = 100)
    @Schema(description = "科室名称")
    private String deptName;

    @Column(name = "attendance_date", nullable = false)
    @Schema(description = "考勤日期")
    private LocalDate attendanceDate;

    @Column(name = "clock_in_time")
    @Schema(description = "签到时间")
    private LocalTime clockInTime;

    @Column(name = "clock_out_time")
    @Schema(description = "签退时间")
    private LocalTime clockOutTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", length = 20)
    @Schema(description = "应到班次")
    private ScheduleType scheduleType;

    @Column(name = "schedule_start")
    @Schema(description = "应到时间")
    private LocalTime scheduleStart;

    @Column(name = "schedule_end")
    @Schema(description = "应退时间")
    private LocalTime scheduleEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", length = 20)
    @Schema(description = "考勤状态")
    private AttendanceStatus attendanceStatus;

    @Column(name = "late_minutes")
    @Schema(description = "迟到分钟")
    private Integer lateMinutes;

    @Column(name = "early_minutes")
    @Schema(description = "早退分钟")
    private Integer earlyMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", length = 20)
    @Schema(description = "请假类型")
    private LeaveType leaveType;

    @Column(name = "leave_id", length = 36)
    @Schema(description = "请假申请ID")
    private String leaveId;

    @Column(name = "overtime_hours", precision = 4, scale = 1)
    @Schema(description = "加班时长(小时)")
    private BigDecimal overtimeHours;

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;

    @Column(name = "schedule_id", length = 36)
    @Schema(description = "关联排班ID")
    private String scheduleId;
}