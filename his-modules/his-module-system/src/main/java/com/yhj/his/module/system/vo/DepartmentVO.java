package com.yhj.his.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 科室VO
 */
@Data
@Schema(description = "科室信息")
public class DepartmentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "科室ID")
    private String id;

    @Schema(description = "科室编码")
    private String deptCode;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "科室简称")
    private String shortName;

    @Schema(description = "父级ID")
    private String parentId;

    @Schema(description = "层级")
    private Integer deptLevel;

    @Schema(description = "科室类型")
    private String deptType;

    @Schema(description = "科室负责人ID")
    private String leaderId;

    @Schema(description = "科室负责人姓名")
    private String leaderName;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "科室地址")
    private String address;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子科室列表")
    private java.util.List<DepartmentVO> children;
}