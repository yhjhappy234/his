package com.yhj.his.module.lis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 危急值VO
 */
@Data
@Schema(description = "危急值信息")
public class CriticalValueVO {

    @Schema(description = "记录ID")
    private String id;

    @Schema(description = "申请ID")
    private String requestId;

    @Schema(description = "申请单号")
    private String requestNo;

    @Schema(description = "样本ID")
    private String sampleId;

    @Schema(description = "样本编号")
    private String sampleNo;

    @Schema(description = "结果ID")
    private String resultId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "项目ID")
    private String itemId;

    @Schema(description = "项目名称")
    private String itemName;

    @Schema(description = "检测值")
    private String testValue;

    @Schema(description = "危急级别(高/低)")
    private String criticalLevel;

    @Schema(description = "危急值范围")
    private String criticalRange;

    @Schema(description = "发现时间")
    private LocalDateTime detectTime;

    @Schema(description = "发现人ID")
    private String detecterId;

    @Schema(description = "发现人姓名")
    private String detecterName;

    @Schema(description = "通知时间")
    private LocalDateTime notifyTime;

    @Schema(description = "通知方式")
    private String notifyMethod;

    @Schema(description = "通知人ID")
    private String notifierId;

    @Schema(description = "通知人姓名")
    private String notifierName;

    @Schema(description = "接收确认时间")
    private LocalDateTime receiveTime;

    @Schema(description = "接收科室")
    private String receiverDept;

    @Schema(description = "接收人姓名")
    private String receiverName;

    @Schema(description = "接收电话")
    private String receiverPhone;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理人ID")
    private String handlerId;

    @Schema(description = "处理人姓名")
    private String handlerName;

    @Schema(description = "处理结果")
    private String handleResult;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}