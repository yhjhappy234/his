package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工证件资质VO
 */
@Data
@Schema(description = "员工证件资质信息")
public class EmployeeCertificateVO {

    @Schema(description = "证件ID")
    private String id;

    @Schema(description = "员工ID")
    private String employeeId;

    @Schema(description = "员工工号")
    private String employeeNo;

    @Schema(description = "员工姓名")
    private String employeeName;

    @Schema(description = "证件类型")
    private String certType;

    @Schema(description = "证件名称")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}