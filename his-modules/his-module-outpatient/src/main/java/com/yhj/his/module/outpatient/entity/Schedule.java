package com.yhj.his.module.outpatient.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 排班信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "schedule", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"doctor_id", "schedule_date", "time_period"})
}, indexes = {
        @Index(name = "idx_schedule_date", columnList = "schedule_date"),
        @Index(name = "idx_schedule_dept_date", columnList = "dept_id, schedule_date")
})
public class Schedule extends BaseEntity {

    /**
     * 科室ID
     */
    @Column(name = "dept_id", length = 20, nullable = false)
    private String deptId;

    /**
     * 科室名称
     */
    @Column(name = "dept_name", length = 100)
    private String deptName;

    /**
     * 医生ID
     */
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    /**
     * 医生姓名
     */
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    /**
     * 医生职称
     */
    @Column(name = "doctor_title", length = 50)
    private String doctorTitle;

    /**
     * 排班日期
     */
    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    /**
     * 时间段: 上午/下午/全天
     */
    @Column(name = "time_period", length = 20, nullable = false)
    private String timePeriod;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalTime endTime;

    /**
     * 总号源数
     */
    @Column(name = "total_quota", nullable = false)
    private Integer totalQuota = 0;

    /**
     * 已预约数
     */
    @Column(name = "booked_quota", nullable = false)
    private Integer bookedQuota = 0;

    /**
     * 剩余号源数
     */
    @Column(name = "available_quota", nullable = false)
    private Integer availableQuota = 0;

    /**
     * 挂号类型: 普通/专家/特需
     */
    @Column(name = "registration_type", length = 20)
    private String registrationType;

    /**
     * 挂号费
     */
    @Column(name = "registration_fee", precision = 10, scale = 2)
    private BigDecimal registrationFee;

    /**
     * 诊查费
     */
    @Column(name = "diagnosis_fee", precision = 10, scale = 2)
    private BigDecimal diagnosisFee;

    /**
     * 状态: 正常/停诊
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "正常";

    /**
     * 停诊原因
     */
    @Column(name = "stop_reason", length = 200)
    private String stopReason;

    /**
     * 诊室
     */
    @Column(name = "clinic_room", length = 50)
    private String clinicRoom;

    /**
     * 排班备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}