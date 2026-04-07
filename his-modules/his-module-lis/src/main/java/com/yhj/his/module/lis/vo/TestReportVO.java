package com.yhj.his.module.lis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检验报告VO
 */
@Data
@Schema(description = "检验报告信息")
public class TestReportVO {

    @Schema(description = "报告ID")
    private String id;

    @Schema(description = "报告编号")
    private String reportNo;

    @Schema(description = "申请ID")
    private String requestId;

    @Schema(description = "申请单号")
    private String requestNo;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "样本ID")
    private String sampleId;

    @Schema(description = "样本编号")
    private String sampleNo;

    @Schema(description = "报告类型")
    private String reportType;

    @Schema(description = "报告分类")
    private String reportCategory;

    @Schema(description = "报告时间")
    private LocalDateTime reportTime;

    @Schema(description = "检验人ID")
    private String testerId;

    @Schema(description = "检验人姓名")
    private String testerName;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核人ID")
    private String auditorId;

    @Schema(description = "审核人姓名")
    private String auditorName;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "发布人ID")
    private String publisherId;

    @Schema(description = "发布人姓名")
    private String publisherName;

    @Schema(description = "危急值报告")
    private Boolean criticalReport;

    @Schema(description = "危急值通知时间")
    private LocalDateTime criticalNotifyTime;

    @Schema(description = "危急值确认时间")
    private LocalDateTime criticalConfirmTime;

    @Schema(description = "危急值接收人")
    private String criticalReceiver;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "打印次数")
    private Integer printCount;

    @Schema(description = "最后打印时间")
    private LocalDateTime lastPrintTime;

    @Schema(description = "退回原因")
    private String returnReason;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "检验结果列表")
    private List<TestResultVO> results;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}