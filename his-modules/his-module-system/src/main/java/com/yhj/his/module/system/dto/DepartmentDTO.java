package com.yhj.his.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 科室DTO
 */
@Data
@Schema(description = "科室创建/更新请求")
public class DepartmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "科室ID(更新时必填)")
    private String id;

    @Schema(description = "科室编码(创建时必填)")
    @Size(max = 20, message = "科室编码长度不能超过20")
    private String deptCode;

    @NotBlank(message = "科室名称不能为空")
    @Size(max = 100, message = "科室名称长度不能超过100")
    @Schema(description = "科室名称", required = true)
    private String deptName;

    @Schema(description = "科室简称")
    @Size(max = 50, message = "科室简称长度不能超过50")
    private String shortName;

    @Schema(description = "父级ID")
    private String parentId;

    @Schema(description = "层级")
    private Integer deptLevel;

    @NotBlank(message = "科室类型不能为空")
    @Schema(description = "科室类型: 临床/医技/行政/后勤", required = true)
    private String deptType;

    @Schema(description = "科室负责人ID")
    private String leaderId;

    @Schema(description = "科室负责人姓名")
    @Size(max = 50, message = "负责人姓名长度不能超过50")
    private String leaderName;

    @Schema(description = "联系电话")
    @Size(max = 20, message = "联系电话长度不能超过20")
    private String phone;

    @Schema(description = "科室地址")
    @Size(max = 200, message = "科室地址长度不能超过200")
    private String address;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "状态: NORMAL/DISABLED")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;
}