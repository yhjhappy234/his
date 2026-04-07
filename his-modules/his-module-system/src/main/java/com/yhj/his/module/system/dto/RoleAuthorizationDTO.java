package com.yhj.his.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色授权DTO
 */
@Data
@Schema(description = "角色授权请求")
public class RoleAuthorizationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID", required = true)
    private String roleId;

    @Schema(description = "权限ID列表", required = true)
    private java.util.List<String> permIds;
}