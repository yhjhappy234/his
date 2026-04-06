package com.yhj.his.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限DTO
 */
@Data
@Schema(description = "权限创建/更新请求")
public class PermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "权限ID(更新时必填)")
    private String id;

    @NotBlank(message = "权限编码不能为空")
    @Size(max = 100, message = "权限编码长度不能超过100")
    @Schema(description = "权限编码", required = true)
    private String permCode;

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过50")
    @Schema(description = "权限名称", required = true)
    private String permName;

    @NotBlank(message = "权限类型不能为空")
    @Schema(description = "权限类型: MENU/FUNCTION/DATA/API", required = true)
    private String permType;

    @Schema(description = "父级ID")
    private String parentId;

    @Schema(description = "路径/接口地址")
    @Size(max = 200, message = "路径长度不能超过200")
    private String path;

    @Schema(description = "图标")
    @Size(max = 100, message = "图标长度不能超过100")
    private String icon;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "状态: NORMAL/DISABLED")
    private String status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;
}