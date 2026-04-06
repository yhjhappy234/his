package com.yhj.his.module.pacs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "影像接收DTO")
public class ImageReceiveDTO {

    @Schema(description = "检查ID", required = true)
    @NotBlank(message = "检查ID不能为空")
    private String examId;

    @Schema(description = "序列号")
    private String seriesNo;

    @Schema(description = "Series UID")
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
    private String scanDate;

    @Schema(description = "扫描时间")
    private String scanTime;

    @Schema(description = "管电压(kV)")
    private String kvp;

    @Schema(description = "管电流(mAs)")
    private String mas;

    @Schema(description = "层厚(mm)")
    private String sliceThickness;

    @Schema(description = "像素间距")
    private String pixelSpacing;
}