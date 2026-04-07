package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库记录VO
 */
@Data
@Schema(description = "出库记录VO")
public class MaterialOutboundVO {

    @Schema(description = "出库ID")
    private String id;

    @Schema(description = "出库单号")
    private String outboundNo;

    @Schema(description = "出库类型")
    private String outboundType;

    @Schema(description = "出库类型名称")
    private String outboundTypeName;

    @Schema(description = "出库库房ID")
    private String warehouseId;

    @Schema(description = "库房名称")
    private String warehouseName;

    @Schema(description = "目标库房ID")
    private String targetWarehouseId;

    @Schema(description = "目标库房名称")
    private String targetWarehouseName;

    @Schema(description = "目标科室ID")
    private String targetDeptId;

    @Schema(description = "目标科室名称")
    private String targetDeptName;

    @Schema(description = "出库日期")
    private LocalDate outboundDate;

    @Schema(description = "出库时间")
    private LocalDateTime outboundTime;

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

    @Schema(description = "出库人ID")
    private String operatorId;

    @Schema(description = "出库人姓名")
    private String operatorName;

    @Schema(description = "接收人ID")
    private String receiverId;

    @Schema(description = "接收人姓名")
    private String receiverName;

    @Schema(description = "接收时间")
    private LocalDateTime receiveTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "出库明细")
    private List<MaterialOutboundItemVO> items;
}