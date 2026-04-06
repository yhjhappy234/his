package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 寻人播报请求
 */
@Data
@Schema(description = "寻人播报请求")
public class FindPersonRequest {

    @Schema(description = "被寻人姓名", required = true)
    @NotBlank(message = "被寻人姓名不能为空")
    private String personName;

    /**
     * 寻人类型: PATIENT-患者, FAMILY-家属, STAFF-医护人员
     */
    @Schema(description = "寻人类型: PATIENT-患者, FAMILY-家属, STAFF-医护人员", required = true, example = "PATIENT")
    @NotBlank(message = "寻人类型不能为空")
    private String personType;

    @Schema(description = "目标位置", required = true)
    @NotBlank(message = "目标位置不能为空")
    private String targetLocation;

    @Schema(description = "原因说明")
    private String reason;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "家属关系(寻家属时)")
    private String familyRelation;

    @Schema(description = "医生姓名(寻医护人员时)")
    private String doctorName;

    @Schema(description = "是否紧急")
    private Boolean urgent = false;

    @Schema(description = "目标设备分组编码列表")
    private String[] targetGroups;

    @Schema(description = "目标设备ID列表")
    private String[] targetDevices;
}