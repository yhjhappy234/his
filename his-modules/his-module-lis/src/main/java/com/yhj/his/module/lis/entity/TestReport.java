package com.yhj.his.module.lis.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.lis.enums.TestReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 检验报告实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_report", indexes = {
        @Index(name = "idx_report_no", columnList = "report_no", unique = true),
        @Index(name = "idx_report_request_id", columnList = "request_id"),
        @Index(name = "idx_report_patient_id", columnList = "patient_id"),
        @Index(name = "idx_report_status", columnList = "status")
})
@Schema(description = "检验报告")
public class TestReport extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "报告编号")
    @Column(name = "report_no", length = 30, nullable = false, unique = true)
    private String reportNo;

    @Schema(description = "申请ID")
    @Column(name = "request_id", length = 36, nullable = false)
    private String requestId;

    @Schema(description = "患者ID")
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    @Schema(description = "患者姓名")
    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Schema(description = "样本ID")
    @Column(name = "sample_id", length = 36)
    private String sampleId;

    @Schema(description = "报告类型")
    @Column(name = "report_type", length = 20)
    private String reportType;

    @Schema(description = "报告分类")
    @Column(name = "report_category", length = 20)
    private String reportCategory;

    @Schema(description = "报告时间")
    @Column(name = "report_time", nullable = false)
    private LocalDateTime reportTime;

    @Schema(description = "检验人ID")
    @Column(name = "tester_id", length = 20)
    private String testerId;

    @Schema(description = "检验人姓名")
    @Column(name = "tester_name", length = 50)
    private String testerName;

    @Schema(description = "审核时间")
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Schema(description = "审核人ID")
    @Column(name = "auditor_id", length = 20)
    private String auditorId;

    @Schema(description = "审核人姓名")
    @Column(name = "auditor_name", length = 50)
    private String auditorName;

    @Schema(description = "发布时间")
    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Schema(description = "发布人ID")
    @Column(name = "publisher_id", length = 20)
    private String publisherId;

    @Schema(description = "发布人姓名")
    @Column(name = "publisher_name", length = 50)
    private String publisherName;

    @Schema(description = "危急值报告")
    @Column(name = "critical_report")
    private Boolean criticalReport = false;

    @Schema(description = "危急值通知时间")
    @Column(name = "critical_notify_time")
    private LocalDateTime criticalNotifyTime;

    @Schema(description = "危急值确认时间")
    @Column(name = "critical_confirm_time")
    private LocalDateTime criticalConfirmTime;

    @Schema(description = "危急值接收人")
    @Column(name = "critical_receiver", length = 50)
    private String criticalReceiver;

    @Schema(description = "状态")
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private TestReportStatus status = TestReportStatus.DRAFT;

    @Schema(description = "打印次数")
    @Column(name = "print_count")
    private Integer printCount = 0;

    @Schema(description = "最后打印时间")
    @Column(name = "last_print_time")
    private LocalDateTime lastPrintTime;

    @Schema(description = "退回原因")
    @Column(name = "return_reason", length = 200)
    private String returnReason;

    @Schema(description = "备注")
    @Column(name = "remark", length = 500)
    private String remark;
}