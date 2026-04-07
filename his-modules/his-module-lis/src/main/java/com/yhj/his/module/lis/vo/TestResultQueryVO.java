package com.yhj.his.module.lis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检验结果查询VO
 */
@Data
@Schema(description = "检验结果查询响应")
public class TestResultQueryVO {

    @Schema(description = "申请ID")
    private String requestId;

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

    @Schema(description = "就诊类型")
    private String visitType;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "临床诊断")
    private String clinicalDiagnosis;

    @Schema(description = "申请时间")
    private LocalDateTime requestTime;

    @Schema(description = "样本编号")
    private String sampleNo;

    @Schema(description = "报告状态")
    private String reportStatus;

    @Schema(description = "报告时间")
    private LocalDateTime reportTime;

    @Schema(description = "检验结果列表")
    private List<ResultItemVO> results;

    /**
     * 结果项目VO
     */
    @Data
    @Schema(description = "检验结果项目")
    public static class ResultItemVO {

        @Schema(description = "项目名称")
        private String itemName;

        @Schema(description = "检测值")
        private String testValue;

        @Schema(description = "单位")
        private String unit;

        @Schema(description = "参考范围")
        private String referenceRange;

        @Schema(description = "结果标识")
        private String resultFlag;

        @Schema(description = "是否危急值")
        private Boolean criticalFlag;
    }
}