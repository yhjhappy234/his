package com.yhj.his.module.pacs.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 检查记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exam_record", indexes = {
        @Index(name = "idx_request_id", columnList = "request_id"),
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_exam_no", columnList = "exam_no")
})
public class ExamRecord extends BaseEntity {

    @Column(name = "exam_no", length = 30, nullable = false, unique = true)
    private String examNo;

    @Column(name = "request_id", length = 36, nullable = false)
    private String requestId;

    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Column(name = "accession_no", length = 50)
    private String accessionNo;

    @Column(name = "study_id", length = 50)
    private String studyId;

    @Column(name = "exam_type", length = 20, nullable = false)
    private String examType;

    @Column(name = "exam_part", length = 50)
    private String examPart;

    @Column(name = "modality", length = 20)
    private String modality;

    @Column(name = "equipment_id", length = 20)
    private String equipmentId;

    @Column(name = "equipment_name", length = 100)
    private String equipmentName;

    @Column(name = "room_no", length = 20)
    private String roomNo;

    @Column(name = "technician_id", length = 20)
    private String technicianId;

    @Column(name = "technician_name", length = 50)
    private String technicianName;

    @Column(name = "exam_time", nullable = false)
    private LocalDateTime examTime;

    @Column(name = "exam_duration")
    private Integer examDuration;

    @Column(name = "series_count")
    private Integer seriesCount = 0;

    @Column(name = "image_count")
    private Integer imageCount = 0;

    @Column(name = "storage_path", length = 200)
    private String storagePath;

    @Column(name = "contrast_agent", length = 50)
    private String contrastAgent;

    @Column(name = "contrast_dose", precision = 10, scale = 2)
    private BigDecimal contrastDose;

    @Column(name = "radiation_dose", precision = 10, scale = 2)
    private BigDecimal radiationDose;

    @Column(name = "exam_status", length = 20, nullable = false)
    private String examStatus = "已登记";

    @Column(name = "report_status", length = 20)
    private String reportStatus;

    @Column(name = "exam_description", columnDefinition = "TEXT")
    private String examDescription;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
}