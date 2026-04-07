package com.yhj.his.module.emr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.emr.enums.EmrStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 手术记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "operation_record", indexes = {
    @Index(name = "idx_op_admission_id", columnList = "admission_id"),
    @Index(name = "idx_op_patient_id", columnList = "patient_id"),
    @Index(name = "idx_op_date", columnList = "operation_date")
})
@Schema(description = "手术记录")
public class OperationRecord extends BaseEntity {

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

    // 手术基本信息
    @Schema(description = "手术日期")
    @Column(name = "operation_date", nullable = false)
    private LocalDate operationDate;

    @Schema(description = "开始时间")
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Schema(description = "手术时长(分钟)")
    @Column(name = "operation_duration")
    private Integer operationDuration;

    // 诊断
    @Schema(description = "术前诊断")
    @Column(name = "pre_op_diagnosis", length = 200)
    private String preOpDiagnosis;

    @Schema(description = "术后诊断")
    @Column(name = "post_op_diagnosis", length = 200)
    private String postOpDiagnosis;

    @Schema(description = "手术名称")
    @Column(name = "operation_name", length = 100, nullable = false)
    private String operationName;

    @Schema(description = "手术编码(ICD-9-CM)")
    @Column(name = "operation_code", length = 50)
    private String operationCode;

    // 手术人员
    @Schema(description = "主刀医生ID")
    @Column(name = "surgeon_id", length = 20, nullable = false)
    private String surgeonId;

    @Schema(description = "主刀医生姓名")
    @Column(name = "surgeon_name", length = 50)
    private String surgeonName;

    @Schema(description = "助手列表(JSON)")
    @Column(name = "assistants", columnDefinition = "TEXT")
    private String assistants;

    @Schema(description = "麻醉医生ID")
    @Column(name = "anesthesiologist_id", length = 20)
    private String anesthesiologistId;

    @Schema(description = "麻醉医生姓名")
    @Column(name = "anesthesiologist_name", length = 50)
    private String anesthesiologistName;

    @Schema(description = "麻醉方式")
    @Column(name = "anesthesia_method", length = 50)
    private String anesthesiaMethod;

    @Schema(description = "手术室")
    @Column(name = "operating_room", length = 50)
    private String operatingRoom;

    @Schema(description = "洗手护士")
    @Column(name = "scrub_nurse", length = 50)
    private String scrubNurse;

    @Schema(description = "巡回护士")
    @Column(name = "circulating_nurse", length = 50)
    private String circulatingNurse;

    // 手术过程
    @Schema(description = "切口描述")
    @Column(name = "incision", columnDefinition = "TEXT")
    private String incision;

    @Schema(description = "手术过程")
    @Column(name = "procedure_detail", columnDefinition = "TEXT", nullable = false)
    private String procedureDetail;

    @Schema(description = "手术所见")
    @Column(name = "operation_findings", columnDefinition = "TEXT")
    private String operationFindings;

    @Schema(description = "标本处理")
    @Column(name = "specimens", columnDefinition = "TEXT")
    private String specimens;

    @Schema(description = "并发症")
    @Column(name = "complications", columnDefinition = "TEXT")
    private String complications;

    // 出血与输血
    @Schema(description = "出血量(ml)")
    @Column(name = "blood_loss")
    private Integer bloodLoss;

    @Schema(description = "输血情况")
    @Column(name = "transfusion", columnDefinition = "TEXT")
    private String transfusion;

    @Schema(description = "植入物(JSON)")
    @Column(name = "implants", columnDefinition = "TEXT")
    private String implants;

    // 状态
    @Schema(description = "状态")
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private EmrStatus status = EmrStatus.DRAFT;

    @Schema(description = "科室ID")
    @Column(name = "dept_id", length = 20)
    private String deptId;

    @Schema(description = "科室名称")
    @Column(name = "dept_name", length = 100)
    private String deptName;
}