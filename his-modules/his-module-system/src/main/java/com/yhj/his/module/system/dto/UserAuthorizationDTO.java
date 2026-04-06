package com.yhj.his.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户授权DTO
 */
@Data
@Schema(description = "用户授权请求")
public class UserAuthorizationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", required = true)
    private String userId;

    @Schema(description = "角色ID列表", required = true)
    private java.util.List<String> roleIds;
}