package com.yhj.his.module.inpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 护理记录VO
 */
@Data
@Schema(description = "护理记录")
public class NursingRecordVO {

    @Schema(description = "记录ID")
    private String recordId;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "记录时间")
    private LocalDateTime recordTime;

    @Schema(description = "记录类型")
    private String recordType;

    @Schema(description = "体温(℃)")
    private BigDecimal temperature;

    @Schema(description = "脉搏(次/分)")
    private Integer pulse;

    @Schema(description = "呼吸(次/分)")
    private Integer respiration;

    @Schema(description = "收缩压")
    private Integer bloodPressureSystolic;

    @Schema(description = "舒张压")
    private Integer bloodPressureDiastolic;

    @Schema(description = "血氧饱和度(%)")
    private Integer spo2;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "身高(cm)")
    private Integer height;

    @Schema(description = "入量(ml)")
    private BigDecimal intake;

    @Schema(description = "出量(ml)")
    private BigDecimal output;

    @Schema(description = "尿量(ml)")
    private BigDecimal urine;

    @Schema(description = "大便情况")
    private String stool;

    @Schema(description = "护理内容")
    private String nursingContent;

    @Schema(description = "护理措施")
    private String nursingMeasures;

    @Schema(description = "记录护士姓名")
    private String nurseName;
}