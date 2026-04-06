package com.yhj.his.module.pharmacy.vo;

import com.yhj.his.module.pharmacy.enums.AuditStatus;
import com.yhj.his.module.pharmacy.enums.DispenseStatus;
import com.yhj.his.module.pharmacy.enums.VisitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 发药记录VO
 */
@Data
@Schema(description = "发药记录响应")
public class DispenseRecordVO {

    @Schema(description = "发药ID")
    private String dispenseId;

    @Schema(description = "发药单号")
    private String dispenseNo;

    @Schema(description = "处方ID")
    private String prescriptionId;

    @Schema(description = "处方号")
    private String prescriptionNo;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "就诊类型")
    private VisitType visitType;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "医生ID")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "药房ID")
    private String pharmacyId;

    @Schema(description = "药房名称")
    private String pharmacyName;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "审核状态")
    private AuditStatus auditStatus;

    @Schema(description = "审核人姓名")
    private String auditorName;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核意见")
    private String auditRemark;

    @Schema(description = "发药状态")
    private DispenseStatus dispenseStatus;

    @Schema(description = "发药人姓名")
    private String dispenserName;

    @Schema(description = "发药时间")
    private LocalDateTime dispenseTime;

    @Schema(description = "发药明细")
    private List<DispenseDetailVO> details;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Data
    @Schema(description = "发药明细响应")
    public static class DispenseDetailVO {

        @Schema(description = "明细ID")
        private String detailId;

        @Schema(description = "药品ID")
        private String drugId;

        @Schema(description = "药品编码")
        private String drugCode;

        @Schema(description = "药品名称")
        private String drugName;

        @Schema(description = "规格")
        private String drugSpec;

        @Schema(description = "单位")
        private String drugUnit;

        @Schema(description = "批号")
        private String batchNo;

        @Schema(description = "有效期")
        private String expiryDate;

        @Schema(description = "数量")
        private BigDecimal quantity;

        @Schema(description = "零售价")
        private BigDecimal retailPrice;

        @Schema(description = "金额")
        private BigDecimal amount;

        @Schema(description = "用法")
        private String dosage;

        @Schema(description = "频次")
        private String frequency;

        @Schema(description = "天数")
        private Integer days;

        @Schema(description = "给药途径")
        private String route;

        @Schema(description = "审核结果")
        private String auditResult;

        @Schema(description = "审核说明")
        private String auditRemark;
    }
}