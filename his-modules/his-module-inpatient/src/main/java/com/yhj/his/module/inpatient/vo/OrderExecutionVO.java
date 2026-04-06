package com.yhj.his.module.inpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医嘱执行记录VO
 */
@Data
@Schema(description = "医嘱执行记录")
public class OrderExecutionVO {

    @Schema(description = "执行ID")
    private String executionId;

    @Schema(description = "医嘱ID")
    private String orderId;

    @Schema(description = "医嘱编号")
    private String orderNo;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "执行时间")
    private LocalDateTime executeTime;

    @Schema(description = "执行护士姓名")
    private String executeNurseName;

    @Schema(description = "执行结果")
    private String executeResult;

    @Schema(description = "执行详情(JSON)")
    private String executeDetail;

    @Schema(description = "执行状态")
    private String status;
}