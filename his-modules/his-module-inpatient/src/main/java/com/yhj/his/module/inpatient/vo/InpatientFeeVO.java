package com.yhj.his.module.inpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 住院费用VO
 */
@Data
@Schema(description = "住院费用")
public class InpatientFeeVO {

    @Schema(description = "费用ID")
    private String feeId;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "费用日期")
    private String feeDate;

    @Schema(description = "费用分类")
    private String feeCategory;

    @Schema(description = "项目编码")
    private String feeItemCode;

    @Schema(description = "项目名称")
    private String feeItemName;

    @Schema(description = "规格")
    private String feeSpec;

    @Schema(description = "单位")
    private String feeUnit;

    @Schema(description = "单价")
    private BigDecimal feePrice;

    @Schema(description = "数量")
    private BigDecimal feeQuantity;

    @Schema(description = "金额")
    private BigDecimal feeAmount;

    @Schema(description = "关联医嘱号")
    private String orderNo;

    @Schema(description = "执行科室名称")
    private String deptName;

    @Schema(description = "是否医保")
    private Boolean isInsurance;

    @Schema(description = "结算状态")
    private String payStatus;
}