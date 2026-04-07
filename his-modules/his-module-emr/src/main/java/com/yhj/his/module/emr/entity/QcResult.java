package com.yhj.his.module.emr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.emr.enums.QcLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 质控结果实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "qc_result", indexes = {
    @Index(name = "idx_qc_record", columnList = "record_id, record_type"),
    @Index(name = "idx_qc_patient_id", columnList = "patient_id")
})
@Schema(description = "质控结果")
public class QcResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "病历记录ID")
    @Column(name = "record_id", length = 36, nullable = false)
    private String recordId;

    @Schema(description = "记录类型(门诊病历/入院记录/病程记录/出院记录/手术记录)")
    @Column(name = "record_type", length = 20, nullable = false)
    private String recordType;

    @Schema(description = "患者ID")
    @Column(name = "patient_id", length = 20)
    private String patientId;

    @Schema(description = "患者姓名")
    @Column(name = "patient_name", length = 50)
    private String patientName;

    // 质控结果
    @Schema(description = "质控评分")
    @Column(name = "qc_score")
    private Integer qcScore;

    @Schema(description = "质控等级")
    @Column(name = "qc_level", length = 20)
    @Enumerated(EnumType.STRING)
    private QcLevel qcLevel;

    @Schema(description = "缺陷数量")
    @Column(name = "defect_count")
    private Integer defectCount;

    @Schema(description = "缺陷详情(JSON)")
    @Column(name = "defect_details", columnDefinition = "TEXT")
    private String defectDetails;

    // 时限检查
    @Schema(description = "时限检查结果")
    @Column(name = "time_limit_check", columnDefinition = "TEXT")
    private String timeLimitCheck;

    @Schema(description = "时限是否合格")
    @Column(name = "time_limit_passed")
    private Boolean timeLimitPassed;

    // 内容检查
    @Schema(description = "内容检查结果")
    @Column(name = "content_check", columnDefinition = "TEXT")
    private String contentCheck;

    @Schema(description = "内容是否合格")
    @Column(name = "content_passed")
    private Boolean contentPassed;

    // 质控信息
    @Schema(description = "质控人ID")
    @Column(name = "qc_user_id", length = 20)
    private String qcUserId;

    @Schema(description = "质控人姓名")
    @Column(name = "qc_user_name", length = 50)
    private String qcUserName;

    @Schema(description = "质控时间")
    @Column(name = "qc_time")
    private LocalDateTime qcTime;

    @Schema(description = "质控备注")
    @Column(name = "qc_comment", length = 500)
    private String qcComment;

    // 整改信息
    @Schema(description = "是否需要整改")
    @Column(name = "need_rectification")
    private Boolean needRectification;

    @Schema(description = "整改状态(待整改/已整改/已超期)")
    @Column(name = "rectification_status", length = 20)
    private String rectificationStatus;

    @Schema(description = "整改通知时间")
    @Column(name = "notify_time")
    private LocalDateTime notifyTime;

    @Schema(description = "整改完成时间")
    @Column(name = "rectify_time")
    private LocalDateTime rectifyTime;

    @Schema(description = "整改备注")
    @Column(name = "rectify_comment", length = 500)
    private String rectifyComment;
}