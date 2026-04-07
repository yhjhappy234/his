package com.yhj.his.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色DTO
 */
@Data
@Schema(description = "角色创建/更新请求")
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID(更新时必填)")
    private String id;

    @NotBlank(message = "角色编码不能为空")
    @Size(max = 30, message = "角色编码长度不能超过30")
    @Schema(description = "角色编码", required = true)
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50")
    @Schema(description = "角色名称", required = true)
    private String roleName;

    @Schema(description = "角色描述")
    @Size(max = 200, message = "角色描述长度不能超过200")
    private String description;

    @Schema(description = "数据权限级别: ALL/DEPARTMENT/PERSONAL")
    private String dataScope;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否系统角色")
    private Boolean isSystem;

    @Schema(description = "状态: NORMAL/DISABLED")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    @Schema(description = "权限ID列表")
    private java.util.List<String> permIds;
}