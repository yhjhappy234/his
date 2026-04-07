package com.yhj.his.module.outpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 待诊患者列表VO
 */
@Data
@Schema(description = "待诊患者列表")
public class PendingPatientsVO {

    @Schema(description = "总数")
    private Integer total;

    @Schema(description = "已就诊数")
    private Integer visited;

    @Schema(description = "等候数")
    private Integer waiting;

    @Schema(description = "过号数")
    private Integer passed;

    @Schema(description = "患者列表")
    private List<PatientInfoVO> patients;

    /**
     * 患者信息
     */
    @Data
    @Schema(description = "待诊患者信息")
    public static class PatientInfoVO {

        @Schema(description = "挂号ID")
        private String registrationId;

        @Schema(description = "排队序号")
        private Integer queueNo;

        @Schema(description = "患者ID")
        private String patientId;

        @Schema(description = "患者姓名")
        private String patientName;

        @Schema(description = "性别")
        private String gender;

        @Schema(description = "年龄")
        private Integer age;

        @Schema(description = "状态")
        private String status;

        @Schema(description = "就诊状态")
        private String visitStatus;

        @Schema(description = "等候时间(分钟)")
        private Integer waitTime;

        @Schema(description = "历史就诊次数")
        private Integer visitCount;

        @Schema(description = "上次就诊日期")
        private LocalDate lastVisitDate;

        @Schema(description = "签到时间")
        private LocalDateTime checkInTime;

        @Schema(description = "是否复诊")
        private Boolean isRevisit;

        @Schema(description = "挂号类型")
        private String registrationType;
    }
}