package com.yhj.his.module.outpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 待收费项目VO
 */
@Data
@Schema(description = "待收费项目")
public class PendingBillingVO {

    @Schema(description = "患者信息")
    private PatientBriefVO patientInfo;

    @Schema(description = "收费项目列表")
    private List<BillingItemVO> items;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    /**
     * 患者简要信息
     */
    @Data
    @Schema(description = "患者简要信息")
    public static class PatientBriefVO {

        @Schema(description = "患者ID")
        private String patientId;

        @Schema(description = "患者姓名")
        private String patientName;

        @Schema(description = "性别")
        private String gender;

        @Schema(description = "年龄")
        private Integer age;
    }

    /**
     * 收费项目
     */
    @Data
    @Schema(description = "收费项目")
    public static class BillingItemVO {

        @Schema(description = "项目ID")
        private String itemId;

        @Schema(description = "项目类型: 挂号费/诊查费/处方/检查/检验")
        private String itemType;

        @Schema(description = "项目编号")
        private String itemNo;

        @Schema(description = "项目名称")
        private String itemName;

        @Schema(description = "描述")
        private String description;

        @Schema(description = "金额")
        private BigDecimal amount;

        @Schema(description = "收费状态")
        private String payStatus;
    }
}