package com.yhj.his.module.pacs.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 诊断报告实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exam_report", indexes = {
        @Index(name = "idx_exam_id", columnList = "exam_id"),
        @Index(name = "idx_patient_id", columnList = "patient_id")
})
public class ExamReport extends BaseEntity {

    @Column(name = "report_no", length = 30, nullable = false, unique = true)
    private String reportNo;

    @Column(name = "exam_id", length = 36, nullable = false)
    private String examId;

    @Column(name = "request_id", length = 36)
    private String requestId;

    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Column(name = "report_type", length = 20)
    private String reportType;

    @Column(name = "report_template_id", length = 36)
    private String reportTemplateId;

    @Column(name = "exam_description", columnDefinition = "TEXT")
    private String examDescription;

    @Column(name = "diagnosis_result", columnDefinition = "TEXT")
    private String diagnosisResult;

    @Column(name = "diagnosis_code", length = 50)
    private String diagnosisCode;

    @Column(name = "diagnosis_name", length = 200)
    private String diagnosisName;

    @Column(name = "key_images", columnDefinition = "TEXT")
    private String keyImages;

    @Column(name = "report_status", length = 20, nullable = false)
    private String reportStatus = "草稿";

    @Column(name = "writer_id", length = 20)
    private String writerId;

    @Column(name = "writer_name", length = 50)
    private String writerName;

    @Column(name = "write_time")
    private LocalDateTime writeTime;

    @Column(name = "reviewer_id", length = 20)
    private String reviewerId;

    @Column(name = "reviewer_name", length = 50)
    private String reviewerName;

    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Column(name = "publisher_id", length = 20)
    private String publisherId;

    @Column(name = "publisher_name", length = 50)
    private String publisherName;

    @Column(name = "modify_history", columnDefinition = "TEXT")
    private String modifyHistory;

    @Column(name = "print_count")
    private Integer printCount = 0;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
}