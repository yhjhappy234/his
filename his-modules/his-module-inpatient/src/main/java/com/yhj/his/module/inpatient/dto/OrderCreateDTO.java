package com.yhj.his.module.inpatient.dto;

import com.yhj.his.module.inpatient.enums.OrderCategory;
import com.yhj.his.module.inpatient.enums.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医嘱开立请求DTO
 */
@Data
@Schema(description = "医嘱开立请求")
public class OrderCreateDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @NotNull(message = "医嘱类型不能为空")
    @Schema(description = "医嘱类型")
    private OrderType orderType;

    @NotNull(message = "医嘱分类不能为空")
    @Schema(description = "医嘱分类")
    private OrderCategory orderCategory;

    @NotBlank(message = "医嘱内容不能为空")
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

    @NotBlank(message = "开立医生ID不能为空")
    @Schema(description = "开立医生ID")
    private String doctorId;

    @Schema(description = "开立医生姓名")
    private String doctorName;

    @Schema(description = "组号(成组医嘱)")
    private Integer groupNo;
}