package com.yhj.his.module.lis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 检验结果VO
 */
@Data
@Schema(description = "检验结果信息")
public class TestResultVO {

    @Schema(description = "结果ID")
    private String id;

    @Schema(description = "申请ID")
    private String requestId;

    @Schema(description = "申请单号")
    private String requestNo;

    @Schema(description = "样本ID")
    private String sampleId;

    @Schema(description = "样本编号")
    private String sampleNo;

    @Schema(description = "申请明细ID")
    private String requestItemId;

    @Schema(description = "项目ID")
    private String itemId;

    @Schema(description = "项目编码")
    private String itemCode;

    @Schema(description = "项目名称")
    private String itemName;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "检测值")
    private String testValue;

    @Schema(description = "数值结果")
    private BigDecimal numericValue;

    @Schema(description = "文本结果")
    private String textResult;

    @Schema(description = "结果标识")
    private String resultFlag;

    @Schema(description = "结果标识描述")
    private String resultFlagDesc;

    @Schema(description = "是否异常")
    private Boolean abnormalFlag;

    @Schema(description = "是否危急值")
    private Boolean criticalFlag;

    @Schema(description = "参考值下限")
    private BigDecimal referenceMin;

    @Schema(description = "参考值上限")
    private BigDecimal referenceMax;

    @Schema(description = "参考范围描述")
    private String referenceRange;

    @Schema(description = "仪器ID")
    private String instrumentId;

    @Schema(description = "仪器名称")
    private String instrumentName;

    @Schema(description = "试剂批号")
    private String reagentLot;

    @Schema(description = "检测时间")
    private LocalDateTime testTime;

    @Schema(description = "检测人ID")
    private String testerId;

    @Schema(description = "检测人姓名")
    private String testerName;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核人ID")
    private String auditorId;

    @Schema(description = "审核人姓名")
    private String auditorName;

    @Schema(description = "修改原因")
    private String modifyReason;

    @Schema(description = "修改时间")
    private LocalDateTime modifyTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}