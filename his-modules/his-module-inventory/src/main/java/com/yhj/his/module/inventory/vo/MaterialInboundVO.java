package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 入库记录VO
 */
@Data
@Schema(description = "入库记录VO")
public class MaterialInboundVO {

    @Schema(description = "入库ID")
    private String id;

    @Schema(description = "入库单号")
    private String inboundNo;

    @Schema(description = "入库类型")
    private String inboundType;

    @Schema(description = "入库类型名称")
    private String inboundTypeName;

    @Schema(description = "库房ID")
    private String warehouseId;

    @Schema(description = "库房名称")
    private String warehouseName;

    @Schema(description = "供应商ID")
    private String supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "入库日期")
    private LocalDate inboundDate;

    @Schema(description = "入库时间")
    private LocalDateTime inboundTime;

    @Schema(description = "总数量")
    private BigDecimal totalQuantity;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "申请人ID")
    private String applicantId;

    @Schema(description = "申请人姓名")
    private String applicantName;

    @Schema(description = "申请时间")
    private LocalDateTime applyTime;

    @Schema(description = "审核人ID")
    private String auditorId;

    @Schema(description = "审核人姓名")
    private String auditorName;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核意见")
    private String auditRemark;

    @Schema(description = "入库人ID")
    private String operatorId;

    @Schema(description = "入库人姓名")
    private String operatorName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "入库明细")
    private List<MaterialInboundItemVO> items;
}