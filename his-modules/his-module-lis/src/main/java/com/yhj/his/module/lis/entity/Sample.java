package com.yhj.his.module.lis.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.lis.enums.SampleStatus;
import com.yhj.his.module.lis.enums.SpecimenType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 样本信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sample", indexes = {
        @Index(name = "idx_sample_no", columnList = "sample_no", unique = true),
        @Index(name = "idx_request_id", columnList = "request_id"),
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_sample_status", columnList = "sample_status")
})
@Schema(description = "样本信息")
public class Sample extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "样本编号")
    @Column(name = "sample_no", length = 30, nullable = false, unique = true)
    private String sampleNo;

    @Schema(description = "申请ID")
    @Column(name = "request_id", length = 36, nullable = false)
    private String requestId;

    @Schema(description = "患者ID")
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    @Schema(description = "患者姓名")
    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Schema(description = "标本类型")
    @Column(name = "specimen_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private SpecimenType specimenType;

    @Schema(description = "容器类型")
    @Column(name = "specimen_container", length = 20)
    private String specimenContainer;

    @Schema(description = "采集时间")
    @Column(name = "collection_time")
    private LocalDateTime collectionTime;

    @Schema(description = "采集人ID")
    @Column(name = "collector_id", length = 20)
    private String collectorId;

    @Schema(description = "采集人姓名")
    @Column(name = "collector_name", length = 50)
    private String collectorName;

    @Schema(description = "采集地点")
    @Column(name = "collection_location", length = 50)
    private String collectionLocation;

    @Schema(description = "接收时间")
    @Column(name = "receive_time")
    private LocalDateTime receiveTime;

    @Schema(description = "接收人ID")
    @Column(name = "receiver_id", length = 20)
    private String receiverId;

    @Schema(description = "接收人姓名")
    @Column(name = "receiver_name", length = 50)
    private String receiverName;

    @Schema(description = "样本状态")
    @Column(name = "sample_status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private SampleStatus sampleStatus = SampleStatus.PENDING;

    @Schema(description = "拒收原因")
    @Column(name = "reject_reason", length = 200)
    private String rejectReason;

    @Schema(description = "拒收时间")
    @Column(name = "reject_time")
    private LocalDateTime rejectTime;

    @Schema(description = "拒收人ID")
    @Column(name = "reject_user_id", length = 20)
    private String rejectUserId;

    @Schema(description = "存放位置")
    @Column(name = "storage_location", length = 50)
    private String storageLocation;

    @Schema(description = "检验组")
    @Column(name = "test_group", length = 20)
    private String testGroup;

    @Schema(description = "备注")
    @Column(name = "remark", length = 500)
    private String remark;
}