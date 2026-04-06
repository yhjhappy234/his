package com.yhj.his.module.outpatient.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 排队信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "queue", indexes = {
        @Index(name = "idx_queue_registration", columnList = "registration_id"),
        @Index(name = "idx_queue_doctor", columnList = "doctor_id, schedule_date")
})
public class Queue extends BaseEntity {

    /**
     * 挂号ID
     */
    @Column(name = "registration_id", length = 36, nullable = false)
    private String registrationId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    /**
     * 患者姓名
     */
    @Column(name = "patient_name", length = 50, nullable = false)
    private String patientName;

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
    @Column(name = "doctor_id", length = 20)
    private String doctorId;

    /**
     * 医生姓名
     */
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    /**
     * 就诊日期
     */
    @Column(name = "schedule_date", nullable = false)
    private LocalDateTime scheduleDate;

    /**
     * 时间段: 上午/下午
     */
    @Column(name = "time_period", length = 20)
    private String timePeriod;

    /**
     * 排队序号
     */
    @Column(name = "queue_no", nullable = false)
    private Integer queueNo;

    /**
     * 诊室
     */
    @Column(name = "clinic_room", length = 50)
    private String clinicRoom;

    /**
     * 状态: 等候中/就诊中/已完成/过号
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "等候中";

    /**
     * 优先级(数字越小优先级越高)
     */
    @Column(name = "priority")
    private Integer priority = 0;

    /**
     * 签到时间
     */
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    /**
     * 叫号时间
     */
    @Column(name = "call_time")
    private LocalDateTime callTime;

    /**
     * 就诊开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 就诊结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 是否复诊
     */
    @Column(name = "is_revisit")
    private Boolean isRevisit = false;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;
}