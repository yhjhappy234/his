package com.yhj.his.module.pacs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 关键影像VO
 */
@Data
@Schema(description = "关键影像VO")
public class KeyImageVO {

    @Schema(description = "影像UID")
    private String imageUid;

    @Schema(description = "影像ID")
    private String imageId;

    @Schema(description = "缩略图路径")
    private String thumbnailPath;

    @Schema(description = "文件路径")
    private String imagePath;

    @Schema(description = "影像编号")
    private Integer imageNo;

    @Schema(description = "标注说明")
    private String annotation;
}