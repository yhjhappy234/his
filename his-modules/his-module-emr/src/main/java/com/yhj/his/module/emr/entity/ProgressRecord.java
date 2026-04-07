package com.yhj.his.module.emr.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.ProgressRecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 病程记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "progress_record", indexes = {
    @Index(name = "idx_prog_admission_id", columnList = "admission_id"),
    @Index(name = "idx_prog_patient_id", columnList = "patient_id"),
    @Index(name = "idx_prog_type_date", columnList = "record_type, record_date")
})
@Schema(description = "病程记录")
public class ProgressRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "住院ID")
    @Column(name = "admission_id", length = 36, nullable = false)
    private String admissionId;

    @Schema(description = "患者ID")
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    @Schema(description = "患者姓名")
    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Schema(description = "记录类型")
    @Column(name = "record_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ProgressRecordType recordType;

    @Schema(description = "记录标题")
    @Column(name = "record_title", length = 100)
    private String recordTitle;

    @Schema(description = "记录日期")
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Schema(description = "记录时间")
    @Column(name = "record_time")
    private LocalDateTime recordTime;

    @Schema(description = "记录内容")
    @Column(name = "record_content", columnDefinition = "TEXT", nullable = false)
    private String recordContent;

    // 医生信息
    @Schema(description = "书写医生ID")
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    @Schema(description = "书写医生姓名")
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    @Schema(description = "医生职称")
    @Column(name = "doctor_title", length = 20)
    private String doctorTitle;

    @Schema(description = "审核医生ID")
    @Column(name = "reviewer_id", length = 20)
    private String reviewerId;

    @Schema(description = "审核医生姓名")
    @Column(name = "reviewer_name", length = 50)
    private String reviewerName;

    @Schema(description = "审核时间")
    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    // 状态
    @Schema(description = "状态")
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private EmrStatus status = EmrStatus.DRAFT;

    @Schema(description = "关联手术ID")
    @Column(name = "operation_id", length = 36)
    private String operationId;

    @Schema(description = "关联会诊ID")
    @Column(name = "consultation_id", length = 36)
    private String consultationId;
}