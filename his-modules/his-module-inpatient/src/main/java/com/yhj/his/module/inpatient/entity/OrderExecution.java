package com.yhj.his.module.inpatient.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.inpatient.enums.ExecutionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 医嘱执行记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "order_execution", indexes = {
        @Index(name = "idx_order", columnList = "order_id"),
        @Index(name = "idx_admission", columnList = "admission_id"),
        @Index(name = "idx_execute_time", columnList = "execute_time")
})
public class OrderExecution extends BaseEntity {

    /**
     * 医嘱ID
     */
    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    /**
     * 住院ID
     */
    @Column(name = "admission_id", length = 36, nullable = false)
    private String admissionId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    /**
     * 执行时间
     */
    @Column(name = "execute_time", nullable = false)
    private LocalDateTime executeTime;

    /**
     * 执行护士ID
     */
    @Column(name = "execute_nurse_id", length = 20, nullable = false)
    private String executeNurseId;

    /**
     * 执行护士姓名
     */
    @Column(name = "execute_nurse_name", length = 50)
    private String executeNurseName;

    /**
     * 执行结果
     */
    @Column(name = "execute_result", columnDefinition = "TEXT")
    private String executeResult;

    /**
     * 执行详情(JSON)
     */
    @Column(name = "execute_detail", columnDefinition = "TEXT")
    private String executeDetail;

    /**
     * 执行状态
     */
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;
}