package com.yhj.his.module.hr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.hr.enums.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 排班信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_schedule", indexes = {
    @Index(name = "idx_schedule_date", columnList = "schedule_date"),
    @Index(name = "idx_schedule_emp", columnList = "employee_id"),
    @Index(name = "idx_schedule_dept_date", columnList = "dept_id, schedule_date")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_employee_date", columnNames = {"employee_id", "schedule_date"})
})
@Schema(description = "排班信息")
public class Schedule extends BaseEntity {

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

    @Column(name = "dept_id", length = 36, nullable = false)
    @Schema(description = "科室ID")
    private String deptId;

    @Column(name = "dept_name", length = 100)
    @Schema(description = "科室名称")
    private String deptName;

    @Column(name = "schedule_date", nullable = false)
    @Schema(description = "排班日期")
    private LocalDate scheduleDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", length = 20, nullable = false)
    @Schema(description = "班次类型")
    private ScheduleType scheduleType;

    @Column(name = "start_time")
    @Schema(description = "上班时间")
    private LocalTime startTime;

    @Column(name = "end_time")
    @Schema(description = "下班时间")
    private LocalTime endTime;

    @Column(name = "location", length = 50)
    @Schema(description = "工作地点")
    private String location;

    @Column(name = "template_id", length = 36)
    @Schema(description = "排班模板ID")
    private String templateId;

    @Column(name = "status", length = 20)
    @Schema(description = "状态")
    private String status = "正常";

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;

    @Column(name = "creator_id", length = 36)
    @Schema(description = "创建人ID")
    private String creatorId;

    @Column(name = "creator_name", length = 50)
    @Schema(description = "创建人姓名")
    private String creatorName;
}