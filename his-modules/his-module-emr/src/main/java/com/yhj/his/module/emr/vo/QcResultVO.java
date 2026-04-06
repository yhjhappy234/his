package com.yhj.his.module.emr.vo;

import com.yhj.his.module.emr.enums.QcLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 质控结果VO
 */
@Data
@Schema(description = "质控结果详情")
public class QcResultVO {

    @Schema(description = "质控结果ID")
    private String id;

    @Schema(description = "病历记录ID")
    private String recordId;

    @Schema(description = "记录类型")
    private String recordType;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "质控评分")
    private Integer qcScore;

    @Schema(description = "质控等级")
    private QcLevel qcLevel;

    @Schema(description = "缺陷数量")
    private Integer defectCount;

    @Schema(description = "缺陷详情")
    private String defectDetails;

    @Schema(description = "时限是否合格")
    private Boolean timeLimitPassed;

    @Schema(description = "内容是否合格")
    private Boolean contentPassed;

    @Schema(description = "质控人姓名")
    private String qcUserName;

    @Schema(description = "质控时间")
    private LocalDateTime qcTime;

    @Schema(description = "质控备注")
    private String qcComment;

    @Schema(description = "是否需要整改")
    private Boolean needRectification;

    @Schema(description = "整改状态")
    private String rectificationStatus;

    @Schema(description = "整改通知时间")
    private LocalDateTime notifyTime;

    @Schema(description = "整改完成时间")
    private LocalDateTime rectifyTime;
}