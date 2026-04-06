package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 员工证件创建请求DTO
 */
@Data
@Schema(description = "员工证件创建请求")
public class EmployeeCertificateCreateDTO {

    @NotBlank(message = "员工ID不能为空")
    @Schema(description = "员工ID", required = true)
    private String employeeId;

    @NotBlank(message = "证件类型不能为空")
    @Schema(description = "证件类型", required = true)
    private String certType;

    @NotBlank(message = "证件名称不能为空")
    @Schema(description = "证件名称", required = true)
    private String certName;

    @Schema(description = "证件编号")
    private String certNo;

    @Schema(description = "证件级别")
    private String certLevel;

    @Schema(description = "发证机构")
    private String issueOrg;

    @Schema(description = "发证日期")
    private LocalDate issueDate;

    @Schema(description = "有效期开始")
    private LocalDate validStartDate;

    @Schema(description = "有效期结束")
    private LocalDate validEndDate;

    @Schema(description = "证件文件URL")
    private String certFile;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;
}