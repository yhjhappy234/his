package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 科室创建请求DTO
 */
@Data
@Schema(description = "科室创建请求")
public class DepartmentCreateDTO {

    @NotBlank(message = "科室编码不能为空")
    @Schema(description = "科室编码", required = true)
    private String deptCode;

    @NotBlank(message = "科室名称不能为空")
    @Schema(description = "科室名称", required = true)
    private String deptName;

    @Schema(description = "科室别名")
    private String deptAlias;

    @NotBlank(message = "科室类型不能为空")
    @Schema(description = "科室类型", required = true)
    private String deptType;

    @Schema(description = "科室分类")
    private String deptCategory;

    @Schema(description = "上级科室ID")
    private String parentId;

    @Schema(description = "科室层级")
    private Integer deptLevel;

    @Schema(description = "科室路径")
    private String deptPath;

    @Schema(description = "负责人ID")
    private String deptLeaderId;

    @Schema(description = "负责人姓名")
    private String deptLeaderName;

    @Schema(description = "科室电话")
    private String deptPhone;

    @Schema(description = "科室位置")
    private String deptLocation;

    @Schema(description = "床位数")
    private Integer bedCount;

    @Schema(description = "门诊诊室")
    private String outpatientRoom;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;
}