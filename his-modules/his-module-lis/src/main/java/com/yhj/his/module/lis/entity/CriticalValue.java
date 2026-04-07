package com.yhj.his.module.lis.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.lis.enums.CriticalValueStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 危急值记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "critical_value", indexes = {
        @Index(name = "idx_cv_request_id", columnList = "request_id"),
        @Index(name = "idx_cv_patient_id", columnList = "patient_id"),
        @Index(name = "idx_cv_status", columnList = "status")
})
@Schema(description = "危急值记录")
public class CriticalValue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "申请ID")
    @Column(name = "request_id", length = 36, nullable = false)
    private String requestId;

    @Schema(description = "样本ID")
    @Column(name = "sample_id", length = 36)
    private String sampleId;

    @Schema(description = "结果ID")
    @Column(name = "result_id", length = 36)
    private String resultId;

    @Schema(description = "患者ID")
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    @Schema(description = "患者姓名")
    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Schema(description = "项目ID")
    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    @Schema(description = "项目名称")
    @Column(name = "item_name", length = 100)
    private String itemName;

    @Schema(description = "检测值")
    @Column(name = "test_value", length = 100)
    private String testValue;

    @Schema(description = "危急级别(高/低)")
    @Column(name = "critical_level", length = 20)
    private String criticalLevel;

    @Schema(description = "危急值范围")
    @Column(name = "critical_range", length = 100)
    private String criticalRange;

    @Schema(description = "发现时间")
    @Column(name = "detect_time")
    private LocalDateTime detectTime;

    @Schema(description = "发现人ID")
    @Column(name = "detecter_id", length = 20)
    private String detecterId;

    @Schema(description = "发现人姓名")
    @Column(name = "detecter_name", length = 50)
    private String detecterName;

    @Schema(description = "通知时间")
    @Column(name = "notify_time")
    private LocalDateTime notifyTime;

    @Schema(description = "通知方式(电话/系统)")
    @Column(name = "notify_method", length = 20)
    private String notifyMethod;

    @Schema(description = "通知人ID")
    @Column(name = "notifier_id", length = 20)
    private String notifierId;

    @Schema(description = "通知人姓名")
    @Column(name = "notifier_name", length = 50)
    private String notifierName;

    @Schema(description = "接收确认时间")
    @Column(name = "receive_time")
    private LocalDateTime receiveTime;

    @Schema(description = "接收科室")
    @Column(name = "receiver_dept", length = 100)
    private String receiverDept;

    @Schema(description = "接收人姓名")
    @Column(name = "receiver_name", length = 50)
    private String receiverName;

    @Schema(description = "接收电话")
    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone;

    @Schema(description = "处理时间")
    @Column(name = "handle_time")
    private LocalDateTime handleTime;

    @Schema(description = "处理人ID")
    @Column(name = "handler_id", length = 20)
    private String handlerId;

    @Schema(description = "处理人姓名")
    @Column(name = "handler_name", length = 50)
    private String handlerName;

    @Schema(description = "处理结果")
    @Column(name = "handle_result", columnDefinition = "TEXT")
    private String handleResult;

    @Schema(description = "状态")
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private CriticalValueStatus status = CriticalValueStatus.PENDING;

    @Schema(description = "备注")
    @Column(name = "remark", length = 500)
    private String remark;
}