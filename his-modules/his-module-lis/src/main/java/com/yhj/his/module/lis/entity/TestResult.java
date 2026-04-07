package com.yhj.his.module.lis.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.lis.enums.ResultFlag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 检验结果实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_result", indexes = {
        @Index(name = "idx_result_request_id", columnList = "request_id"),
        @Index(name = "idx_result_sample_id", columnList = "sample_id"),
        @Index(name = "idx_result_item_id", columnList = "item_id"),
        @Index(name = "idx_critical_flag", columnList = "critical_flag")
})
@Schema(description = "检验结果")
public class TestResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "申请ID")
    @Column(name = "request_id", length = 36, nullable = false)
    private String requestId;

    @Schema(description = "样本ID")
    @Column(name = "sample_id", length = 36, nullable = false)
    private String sampleId;

    @Schema(description = "申请明细ID")
    @Column(name = "request_item_id", length = 36)
    private String requestItemId;

    @Schema(description = "项目ID")
    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    @Schema(description = "项目编码")
    @Column(name = "item_code", length = 20)
    private String itemCode;

    @Schema(description = "项目名称")
    @Column(name = "item_name", length = 100)
    private String itemName;

    @Schema(description = "单位")
    @Column(name = "unit", length = 20)
    private String unit;

    @Schema(description = "检测值")
    @Column(name = "test_value", length = 100, nullable = false)
    private String testValue;

    @Schema(description = "数值结果")
    @Column(name = "numeric_value", precision = 18, scale = 6)
    private BigDecimal numericValue;

    @Schema(description = "文本结果")
    @Column(name = "text_result", columnDefinition = "TEXT")
    private String textResult;

    @Schema(description = "结果标识")
    @Column(name = "result_flag", length = 20)
    @Enumerated(EnumType.STRING)
    private ResultFlag resultFlag;

    @Schema(description = "是否异常")
    @Column(name = "abnormal_flag")
    private Boolean abnormalFlag = false;

    @Schema(description = "是否危急值")
    @Column(name = "critical_flag")
    private Boolean criticalFlag = false;

    @Schema(description = "参考值下限")
    @Column(name = "reference_min", precision = 10, scale = 4)
    private BigDecimal referenceMin;

    @Schema(description = "参考值上限")
    @Column(name = "reference_max", precision = 10, scale = 4)
    private BigDecimal referenceMax;

    @Schema(description = "参考范围描述")
    @Column(name = "reference_range", length = 100)
    private String referenceRange;

    @Schema(description = "仪器ID")
    @Column(name = "instrument_id", length = 20)
    private String instrumentId;

    @Schema(description = "仪器名称")
    @Column(name = "instrument_name", length = 100)
    private String instrumentName;

    @Schema(description = "试剂批号")
    @Column(name = "reagent_lot", length = 50)
    private String reagentLot;

    @Schema(description = "检测时间")
    @Column(name = "test_time")
    private LocalDateTime testTime;

    @Schema(description = "检测人ID")
    @Column(name = "tester_id", length = 20)
    private String testerId;

    @Schema(description = "检测人姓名")
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

    @Schema(description = "修改原因")
    @Column(name = "modify_reason", length = 200)
    private String modifyReason;

    @Schema(description = "修改时间")
    @Column(name = "modify_time")
    private LocalDateTime modifyTime;

    @Schema(description = "备注")
    @Column(name = "remark", length = 500)
    private String remark;
}