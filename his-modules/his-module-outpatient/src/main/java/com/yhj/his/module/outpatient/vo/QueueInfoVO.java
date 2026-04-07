package com.yhj.his.module.outpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 排队信息VO
 */
@Data
@Schema(description = "排队信息")
public class QueueInfoVO {

    @Schema(description = "当前就诊序号")
    private Integer currentNo;

    @Schema(description = "当前就诊患者(脱敏)")
    private String currentPatient;

    @Schema(description = "等候列表")
    private List<WaitingPatientVO> waitingList;

    @Schema(description = "过号列表")
    private List<PassedPatientVO> passedList;

    @Schema(description = "等候人数")
    private Integer waitCount;

    @Schema(description = "过号人数")
    private Integer passCount;

    /**
     * 等候患者
     */
    @Data
    @Schema(description = "等候患者")
    public static class WaitingPatientVO {

        @Schema(description = "排队序号")
        private Integer queueNo;

        @Schema(description = "患者姓名(脱敏)")
        private String patientName;

        @Schema(description = "状态")
        private String status;

        @Schema(description = "等候时间(分钟)")
        private Integer waitTime;
    }

    /**
     * 过号患者
     */
    @Data
    @Schema(description = "过号患者")
    public static class PassedPatientVO {

        @Schema(description = "排队序号")
        private Integer queueNo;

        @Schema(description = "患者姓名(脱敏)")
        private String patientName;

        @Schema(description = "过号时间")
        private String passTime;
    }
}