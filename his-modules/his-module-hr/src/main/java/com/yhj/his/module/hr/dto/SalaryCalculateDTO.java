package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 薪资计算请求DTO
 */
@Data
@Schema(description = "薪资计算请求")
public class SalaryCalculateDTO {

    @NotBlank(message = "薪资月份不能为空")
    @Schema(description = "薪资月份(yyyy-MM)", required = true)
    private String salaryMonth;

    @Schema(description = "员工ID列表(为空则计算全部在职员工)")
    private List<String> employeeIds;

    @Schema(description = "科室ID(为空则不限制科室)")
    private String deptId;
}