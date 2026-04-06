package com.yhj.his.module.inpatient.vo;

import com.yhj.his.module.inpatient.enums.OrderCategory;
import com.yhj.his.module.inpatient.enums.OrderStatus;
import com.yhj.his.module.inpatient.enums.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医嘱信息VO
 */
@Data
@Schema(description = "医嘱信息")
public class MedicalOrderVO {

    @Schema(description = "医嘱ID")
    private String orderId;

    @Schema(description = "医嘱编号")
    private String orderNo;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "医嘱类型")
    private OrderType orderType;

    @Schema(description = "医嘱分类")
    private OrderCategory orderCategory;

    @Schema(description = "医嘱内容")
    private String orderContent;

    @Schema(description = "医嘱详情(JSON)")
    private String orderDetail;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "执行时间描述")
    private String executeTime;

    @Schema(description = "执行频次")
    private String frequency;

    @Schema(description = "开立医生姓名")
    private String doctorName;

    @Schema(description = "医嘱时间")
    private LocalDateTime orderTime;

    @Schema(description = "审核护士姓名")
    private String nurseName;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "状态")
    private OrderStatus status;

    @Schema(description = "停止医生姓名")
    private String stopDoctorName;

    @Schema(description = "停止时间")
    private LocalDateTime stopTime;

    @Schema(description = "停止原因")
    private String stopReason;

    @Schema(description = "组号")
    private Integer groupNo;

    @Schema(description = "执行次数")
    private Long executeCount;
}