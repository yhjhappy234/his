package com.yhj.his.module.outpatient.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 挂号记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "registration", indexes = {
        @Index(name = "idx_reg_patient", columnList = "patient_id"),
        @Index(name = "idx_reg_schedule", columnList = "schedule_date, dept_id"),
        @Index(name = "idx_reg_status", columnList = "status, schedule_date")
})
public class Registration extends BaseEntity {

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
     * 身份证号
     */
    @Column(name = "id_card_no", length = 18)
    private String idCardNo;

    /**
     * 性别
     */
    @Column(name = "gender", length = 10, nullable = false)
    private String gender;

    /**
     * 年龄
     */
    @Column(name = "age")
    private Integer age;

    /**
     * 联系电话
     */
    @Column(name = "phone", length = 20)
    private String phone;

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
     * 排班ID
     */
    @Column(name = "schedule_id", length = 36)
    private String scheduleId;

    /**
     * 就诊日期
     */
    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    /**
     * 时间段: 上午/下午
     */
    @Column(name = "time_period", length = 20)
    private String timePeriod;

    /**
     * 排队序号
     */
    @Column(name = "queue_no")
    private Integer queueNo;

    /**
     * 就诊序号
     */
    @Column(name = "visit_no", length = 30)
    private String visitNo;

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
     * 总费用
     */
    @Column(name = "total_fee", precision = 10, scale = 2)
    private BigDecimal totalFee;

    /**
     * 挂号状态: 已预约/已挂号/已签到/已就诊/已退号
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    /**
     * 就诊状态: 待诊/就诊中/已完成
     */
    @Column(name = "visit_status", length = 20)
    private String visitStatus;

    /**
     * 来源: 现场/微信/APP/电话
     */
    @Column(name = "source", length = 20)
    private String source;

    /**
     * 预约时间
     */
    @Column(name = "booking_time")
    private LocalDateTime bookingTime;

    /**
     * 签到时间
     */
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

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
     * 操作员ID
     */
    @Column(name = "operator_id", length = 20)
    private String operatorId;

    /**
     * 操作员姓名
     */
    @Column(name = "operator_name", length = 50)
    private String operatorName;

    /**
     * 诊室
     */
    @Column(name = "clinic_room", length = 50)
    private String clinicRoom;

    /**
     * 取消原因
     */
    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;

    /**
     * 取消时间
     */
    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}