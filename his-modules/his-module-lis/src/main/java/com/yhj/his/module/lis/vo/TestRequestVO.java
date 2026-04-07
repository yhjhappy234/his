package com.yhj.his.module.lis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 检验申请VO
 */
@Data
@Schema(description = "检验申请信息")
public class TestRequestVO {

    @Schema(description = "申请ID")
    private String id;

    @Schema(description = "申请单号")
    private String requestNo;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "身份证号")
    private String idCardNo;

    @Schema(description = "就诊类型")
    private String visitType;

    @Schema(description = "就诊类型描述")
    private String visitTypeDesc;

    @Schema(description = "就诊ID")
    private String visitId;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "申请科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "申请医生ID")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "临床诊断")
    private String clinicalDiagnosis;

    @Schema(description = "临床信息")
    private String clinicalInfo;

    @Schema(description = "申请时间")
    private LocalDateTime requestTime;

    @Schema(description = "是否急诊")
    private Boolean emergency;

    @Schema(description = "急诊级别")
    private String emergencyLevel;

    @Schema(description = "样本状态")
    private String sampleStatus;

    @Schema(description = "报告状态")
    private String reportStatus;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "收费状态")
    private String payStatus;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "检验项目列表")
    private List<TestRequestItemVO> items;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 申请项目明细VO
     */
    @Data
    @Schema(description = "检验申请明细信息")
    public static class TestRequestItemVO {

        @Schema(description = "明细ID")
        private String id;

        @Schema(description = "项目ID")
        private String itemId;

        @Schema(description = "项目编码")
        private String itemCode;

        @Schema(description = "项目名称")
        private String itemName;

        @Schema(description = "标本类型")
        private String specimenType;

        @Schema(description = "标本类型描述")
        private String specimenTypeDesc;

        @Schema(description = "价格")
        private BigDecimal price;

        @Schema(description = "样本ID")
        private String sampleId;

        @Schema(description = "结果状态")
        private String resultStatus;

        @Schema(description = "备注")
        private String remark;
    }
}