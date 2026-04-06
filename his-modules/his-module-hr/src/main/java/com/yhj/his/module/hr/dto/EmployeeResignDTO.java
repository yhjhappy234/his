package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 员工离职请求DTO
 */
@Data
@Schema(description = "员工离职请求")
public class EmployeeResignDTO {

    @Schema(description = "离职日期")
    private LocalDate leaveDate;

    @Schema(description = "离职原因")
    private String leaveReason;
}