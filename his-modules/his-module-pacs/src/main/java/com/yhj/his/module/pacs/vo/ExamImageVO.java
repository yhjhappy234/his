package com.yhj.his.module.pacs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "影像文件VO")
public class ExamImageVO {

    @Schema(description = "影像ID")
    private String id;

    @Schema(description = "序列ID")
    private String seriesId;

    @Schema(description = "检查ID")
    private String examId;

    @Schema(description = "影像编号")
    private Integer imageNo;

    @Schema(description = "影像UID")
    private String imageUid;

    @Schema(description = "SOP实例UID")
    private String sopUid;

    @Schema(description = "文件路径")
    private String imagePath;

    @Schema(description = "缩略图路径")
    private String thumbnailPath;

    @Schema(description = "影像宽度")
    private Integer imageWidth;

    @Schema(description = "影像高度")
    private Integer imageHeight;

    @Schema(description = "分配位数")
    private Integer bitsAllocated;

    @Schema(description = "存储位数")
    private Integer bitsStored;

    @Schema(description = "窗位")
    private BigDecimal windowCenter;

    @Schema(description = "窗宽")
    private BigDecimal windowWidth;

    @Schema(description = "是否关键影像")
    private Boolean isKeyImage;

    @Schema(description = "影像描述")
    private String imageDescription;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "文件格式")
    private String fileFormat;
}