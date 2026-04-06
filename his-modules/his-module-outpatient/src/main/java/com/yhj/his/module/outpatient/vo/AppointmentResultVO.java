package com.yhj.his.module.outpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 预约挂号结果VO
 */
@Data
@Schema(description = "预约挂号结果")
public class AppointmentResultVO {

    @Schema(description = "挂号ID")
    private String appointmentId;

    @Schema(description = "排队序号")
    private Integer queueNo;

    @Schema(description = "就诊序号")
    private String visitNo;

    @Schema(description = "预约时间段")
    private String scheduleTime;

    @Schema(description = "挂号费")
    private BigDecimal registrationFee;

    @Schema(description = "诊查费")
    private BigDecimal diagnosisFee;

    @Schema(description = "总费用")
    private BigDecimal totalFee;

    @Schema(description = "诊室")
    private String clinicRoom;

    @Schema(description = "医生姓名")
    private String doctorName;
}