package com.yhj.his.module.lis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 样本VO
 */
@Data
@Schema(description = "样本信息")
public class SampleVO {

    @Schema(description = "样本ID")
    private String id;

    @Schema(description = "样本编号")
    private String sampleNo;

    @Schema(description = "申请ID")
    private String requestId;

    @Schema(description = "申请单号")
    private String requestNo;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "标本类型")
    private String specimenType;

    @Schema(description = "标本类型描述")
    private String specimenTypeDesc;

    @Schema(description = "容器类型")
    private String specimenContainer;

    @Schema(description = "采集时间")
    private LocalDateTime collectionTime;

    @Schema(description = "采集人ID")
    private String collectorId;

    @Schema(description = "采集人姓名")
    private String collectorName;

    @Schema(description = "采集地点")
    private String collectionLocation;

    @Schema(description = "接收时间")
    private LocalDateTime receiveTime;

    @Schema(description = "接收人ID")
    private String receiverId;

    @Schema(description = "接收人姓名")
    private String receiverName;

    @Schema(description = "样本状态")
    private String sampleStatus;

    @Schema(description = "样本状态描述")
    private String sampleStatusDesc;

    @Schema(description = "拒收原因")
    private String rejectReason;

    @Schema(description = "拒收时间")
    private LocalDateTime rejectTime;

    @Schema(description = "拒收人ID")
    private String rejectUserId;

    @Schema(description = "存放位置")
    private String storageLocation;

    @Schema(description = "检验组")
    private String testGroup;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "标签内容")
    private String labelContent;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}