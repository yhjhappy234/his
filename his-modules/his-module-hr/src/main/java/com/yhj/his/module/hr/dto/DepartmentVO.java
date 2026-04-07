package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 科室信息VO
 */
@Data
@Schema(description = "科室信息")
public class DepartmentVO {

    @Schema(description = "科室ID")
    private String id;

    @Schema(description = "科室编码")
    private String deptCode;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "科室别名")
    private String deptAlias;

    @Schema(description = "科室类型")
    private String deptType;

    @Schema(description = "科室分类")
    private String deptCategory;

    @Schema(description = "上级科室ID")
    private String parentId;

    @Schema(description = "上级科室名称")
    private String parentName;

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

    @Schema(description = "员工数量")
    private Long employeeCount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}