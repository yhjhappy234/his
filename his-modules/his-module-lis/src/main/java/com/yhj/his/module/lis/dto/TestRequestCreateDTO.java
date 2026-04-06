package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 检验申请创建DTO
 */
@Data
@Schema(description = "检验申请创建请求")
public class TestRequestCreateDTO {

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "患者ID不能为空")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "身份证号")
    private String idCardNo;

    @Schema(description = "就诊类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "就诊类型不能为空")
    private String visitType;

    @Schema(description = "就诊ID")
    private String visitId;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "申请科室ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申请科室ID不能为空")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "申请医生ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申请医生ID不能为空")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "临床诊断")
    private String clinicalDiagnosis;

    @Schema(description = "临床信息")
    private String clinicalInfo;

    @Schema(description = "是否急诊")
    private Boolean emergency;

    @Schema(description = "急诊级别")
    private String emergencyLevel;

    @Schema(description = "检验项目列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "检验项目列表不能为空")
    private List<TestRequestItemDTO> items;

    @Schema(description = "备注")
    private String remark;

    /**
     * 申请项目DTO
     */
    @Data
    @Schema(description = "检验申请项目")
    public static class TestRequestItemDTO {

        @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "项目ID不能为空")
        private String itemId;

        @Schema(description = "项目编码")
        private String itemCode;

        @Schema(description = "项目名称")
        private String itemName;
    }
}