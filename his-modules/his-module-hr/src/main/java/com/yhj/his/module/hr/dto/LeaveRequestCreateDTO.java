package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 请假申请创建请求DTO
 */
@Data
@Schema(description = "请假申请创建请求")
public class LeaveRequestCreateDTO {

    @NotBlank(message = "员工ID不能为空")
    @Schema(description = "员工ID", required = true)
    private String employeeId;

    @NotBlank(message = "请假类型不能为空")
    @Schema(description = "请假类型", required = true)
    private String leaveType;

    @Schema(description = "请假原因")
    private String leaveReason;

    @NotNull(message = "开始日期不能为空")
    @Schema(description = "开始日期", required = true)
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    @Schema(description = "结束日期", required = true)
    private LocalDate endDate;

    @Schema(description = "请假天数")
    private BigDecimal leaveDays;

    @Schema(description = "附件URL")
    private String attachment;
}