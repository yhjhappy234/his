package com.yhj.his.module.pacs.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 影像序列实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exam_series", indexes = {
        @Index(name = "idx_exam_id", columnList = "exam_id")
})
public class ExamSeries extends BaseEntity {

    @Column(name = "exam_id", length = 36, nullable = false)
    private String examId;

    @Column(name = "series_no", length = 50, nullable = false)
    private String seriesNo;

    @Column(name = "series_uid", length = 100)
    private String seriesUid;

    @Column(name = "series_description", length = 100)
    private String seriesDescription;

    @Column(name = "modality", length = 20)
    private String modality;

    @Column(name = "body_part", length = 50)
    private String bodyPart;

    @Column(name = "image_count")
    private Integer imageCount = 0;

    @Column(name = "storage_path", length = 200)
    private String storagePath;

    @Column(name = "scan_date")
    private LocalDate scanDate;

    @Column(name = "scan_time")
    private LocalTime scanTime;

    @Column(name = "kvp", precision = 10, scale = 2)
    private BigDecimal kvp;

    @Column(name = "mas", precision = 10, scale = 2)
    private BigDecimal mas;

    @Column(name = "slice_thickness", precision = 10, scale = 2)
    private BigDecimal sliceThickness;

    @Column(name = "pixel_spacing", length = 50)
    private String pixelSpacing;

    @Column(name = "series_direction", length = 50)
    private String seriesDirection;

    @Column(name = "protocol_name", length = 100)
    private String protocolName;

    @Column(name = "remark", length = 500)
    private String remark;
}