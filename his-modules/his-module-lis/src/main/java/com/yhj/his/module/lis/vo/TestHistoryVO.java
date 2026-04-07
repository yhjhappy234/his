package com.yhj.his.module.lis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 检验历史对比VO
 */
@Data
@Schema(description = "检验历史对比信息")
public class TestHistoryVO {

    @Schema(description = "项目ID")
    private String itemId;

    @Schema(description = "项目名称")
    private String itemName;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "参考值下限")
    private BigDecimal referenceMin;

    @Schema(description = "参考值上限")
    private BigDecimal referenceMax;

    @Schema(description = "参考范围描述")
    private String referenceRange;

    @Schema(description = "历史结果列表")
    private List<HistoryItemVO> historyItems;

    /**
     * 历史结果项VO
     */
    @Data
    @Schema(description = "历史结果项")
    public static class HistoryItemVO {

        @Schema(description = "申请ID")
        private String requestId;

        @Schema(description = "申请单号")
        private String requestNo;

        @Schema(description = "检测值")
        private String testValue;

        @Schema(description = "数值结果")
        private BigDecimal numericValue;

        @Schema(description = "结果标识")
        private String resultFlag;

        @Schema(description = "检测时间")
        private LocalDateTime testTime;

        @Schema(description = "就诊类型")
        private String visitType;

        @Schema(description = "科室名称")
        private String deptName;
    }
}