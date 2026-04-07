package com.yhj.his.module.pacs.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 影像文件实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exam_image", indexes = {
        @Index(name = "idx_series_id", columnList = "series_id"),
        @Index(name = "idx_exam_id", columnList = "exam_id")
})
public class ExamImage extends BaseEntity {

    @Column(name = "series_id", length = 36, nullable = false)
    private String seriesId;

    @Column(name = "exam_id", length = 36)
    private String examId;

    @Column(name = "image_no")
    private Integer imageNo;

    @Column(name = "image_uid", length = 100)
    private String imageUid;

    @Column(name = "sop_uid", length = 100)
    private String sopUid;

    @Column(name = "image_path", length = 200)
    private String imagePath;

    @Column(name = "thumbnail_path", length = 200)
    private String thumbnailPath;

    @Column(name = "image_width")
    private Integer imageWidth;

    @Column(name = "image_height")
    private Integer imageHeight;

    @Column(name = "bits_allocated")
    private Integer bitsAllocated;

    @Column(name = "bits_stored")
    private Integer bitsStored;

    @Column(name = "window_center", precision = 10, scale = 2)
    private BigDecimal windowCenter;

    @Column(name = "window_width", precision = 10, scale = 2)
    private BigDecimal windowWidth;

    @Column(name = "is_key_image")
    private Boolean isKeyImage = false;

    @Column(name = "image_description", length = 200)
    private String imageDescription;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_format", length = 20)
    private String fileFormat;
}