package com.yhj.his.module.pacs.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 机房排班实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "room_schedule")
public class RoomSchedule extends BaseEntity {

    @Column(name = "room_no", length = 20, nullable = false)
    private String roomNo;

    @Column(name = "room_name", length = 50)
    private String roomName;

    @Column(name = "equipment_id", length = 36)
    private String equipmentId;

    @Column(name = "equipment_name", length = 100)
    private String equipmentName;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "shift", length = 20, nullable = false)
    private String shift;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "total_quota", nullable = false)
    private Integer totalQuota = 0;

    @Column(name = "scheduled_count", nullable = false)
    private Integer scheduledCount = 0;

    @Column(name = "available_quota", nullable = false)
    private Integer availableQuota = 0;

    @Column(name = "exam_type_limit", length = 50)
    private String examTypeLimit;

    @Column(name = "doctor_id", length = 20)
    private String doctorId;

    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    @Column(name = "technician_id", length = 20)
    private String technicianId;

    @Column(name = "technician_name", length = 50)
    private String technicianName;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "开放";

    @Column(name = "remark", length = 500)
    private String remark;
}