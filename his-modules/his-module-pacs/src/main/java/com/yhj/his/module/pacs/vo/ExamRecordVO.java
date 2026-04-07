package com.yhj.his.module.pacs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "检查记录VO")
public class ExamRecordVO {

    @Schema(description = "检查ID")
    private String id;

    @Schema(description = "检查编号")
    private String examNo;

    @Schema(description = "申请ID")
    private String requestId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "流水号")
    private String accessionNo;

    @Schema(description = "检查号")
    private String studyId;

    @Schema(description = "检查类型")
    private String examType;

    @Schema(description = "检查部位")
    private String examPart;

    @Schema(description = "检查模态")
    private String modality;

    @Schema(description = "设备ID")
    private String equipmentId;

    @Schema(description = "设备名称")
    private String equipmentName;

    @Schema(description = "机房号")
    private String roomNo;

    @Schema(description = "技师ID")
    private String technicianId;

    @Schema(description = "技师姓名")
    private String technicianName;

    @Schema(description = "检查时间")
    private LocalDateTime examTime;

    @Schema(description = "检查时长(分钟)")
    private Integer examDuration;

    @Schema(description = "序列数量")
    private Integer seriesCount;

    @Schema(description = "影像数量")
    private Integer imageCount;

    @Schema(description = "存储路径")
    private String storagePath;

    @Schema(description = "造影剂")
    private String contrastAgent;

    @Schema(description = "造影剂剂量")
    private BigDecimal contrastDose;

    @Schema(description = "辐射剂量")
    private BigDecimal radiationDose;

    @Schema(description = "检查状态")
    private String examStatus;

    @Schema(description = "报告状态")
    private String reportStatus;

    @Schema(description = "检查描述")
    private String examDescription;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "序列列表")
    private List<ExamSeriesVO> seriesList;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}