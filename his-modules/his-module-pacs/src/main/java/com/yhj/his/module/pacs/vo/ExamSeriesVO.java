package com.yhj.his.module.pacs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Schema(description = "影像序列VO")
public class ExamSeriesVO {

    @Schema(description = "序列ID")
    private String id;

    @Schema(description = "检查ID")
    private String examId;

    @Schema(description = "序列号")
    private String seriesNo;

    @Schema(description = "序列UID")
    private String seriesUid;

    @Schema(description = "序列描述")
    private String seriesDescription;

    @Schema(description = "设备类型")
    private String modality;

    @Schema(description = "检查部位")
    private String bodyPart;

    @Schema(description = "影像数量")
    private Integer imageCount;

    @Schema(description = "存储路径")
    private String storagePath;

    @Schema(description = "扫描日期")
    private LocalDate scanDate;

    @Schema(description = "扫描时间")
    private LocalTime scanTime;

    @Schema(description = "管电压(kV)")
    private BigDecimal kvp;

    @Schema(description = "管电流(mAs)")
    private BigDecimal mas;

    @Schema(description = "层厚(mm)")
    private BigDecimal sliceThickness;

    @Schema(description = "像素间距")
    private String pixelSpacing;

    @Schema(description = "序列方向")
    private String seriesDirection;

    @Schema(description = "协议名称")
    private String protocolName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "影像列表")
    private List<ExamImageVO> imageList;

    @Schema(description = "创建时间")
    private LocalDate createTime;
}