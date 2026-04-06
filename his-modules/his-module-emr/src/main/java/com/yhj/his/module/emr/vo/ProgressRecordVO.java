package com.yhj.his.module.emr.vo;

import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.ProgressRecordType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 病程记录VO
 */
@Data
@Schema(description = "病程记录详情")
public class ProgressRecordVO {

    @Schema(description = "记录ID")
    private String id;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "记录类型")
    private ProgressRecordType recordType;

    @Schema(description = "记录标题")
    private String recordTitle;

    @Schema(description = "记录日期")
    private LocalDate recordDate;

    @Schema(description = "记录时间")
    private LocalDateTime recordTime;

    @Schema(description = "记录内容")
    private String recordContent;

    @Schema(description = "医生ID")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "医生职称")
    private String doctorTitle;

    @Schema(description = "审核医生姓名")
    private String reviewerName;

    @Schema(description = "审核时间")
    private LocalDateTime reviewTime;

    @Schema(description = "状态")
    private EmrStatus status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}