package com.yhj.his.module.pacs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "诊断报告VO")
public class ExamReportVO {

    @Schema(description = "报告ID")
    private String id;

    @Schema(description = "报告编号")
    private String reportNo;

    @Schema(description = "检查ID")
    private String examId;

    @Schema(description = "申请ID")
    private String requestId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "报告类型")
    private String reportType;

    @Schema(description = "报告模板ID")
    private String reportTemplateId;

    @Schema(description = "检查所见")
    private String examDescription;

    @Schema(description = "诊断结论")
    private String diagnosisResult;

    @Schema(description = "诊断编码")
    private String diagnosisCode;

    @Schema(description = "诊断名称")
    private String diagnosisName;

    @Schema(description = "关键影像")
    private String keyImages;

    @Schema(description = "报告状态")
    private String reportStatus;

    @Schema(description = "书写医生ID")
    private String writerId;

    @Schema(description = "书写医生姓名")
    private String writerName;

    @Schema(description = "书写时间")
    private LocalDateTime writeTime;

    @Schema(description = "审核医生ID")
    private String reviewerId;

    @Schema(description = "审核医生姓名")
    private String reviewerName;

    @Schema(description = "审核时间")
    private LocalDateTime reviewTime;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "发布人ID")
    private String publisherId;

    @Schema(description = "发布人姓名")
    private String publisherName;

    @Schema(description = "打印次数")
    private Integer printCount;

    @Schema(description = "审核意见")
    private String reviewComment;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}