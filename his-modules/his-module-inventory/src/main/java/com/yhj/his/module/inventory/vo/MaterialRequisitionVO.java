package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 物资申领VO
 */
@Data
@Schema(description = "物资申领VO")
public class MaterialRequisitionVO {

    @Schema(description = "申领ID")
    private String id;

    @Schema(description = "申领单号")
    private String requisitionNo;

    @Schema(description = "库房ID")
    private String warehouseId;

    @Schema(description = "库房名称")
    private String warehouseName;

    @Schema(description = "申领科室ID")
    private String deptId;

    @Schema(description = "申领科室名称")
    private String deptName;

    @Schema(description = "申领日期")
    private LocalDate requisitionDate;

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

    @Schema(description = "审批人ID")
    private String approverId;

    @Schema(description = "审批人姓名")
    private String approverName;

    @Schema(description = "审批时间")
    private LocalDateTime approveTime;

    @Schema(description = "审批意见")
    private String approveRemark;

    @Schema(description = "发放人ID")
    private String issuerId;

    @Schema(description = "发放人姓名")
    private String issuerName;

    @Schema(description = "发放时间")
    private LocalDateTime issueTime;

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

    @Schema(description = "申领明细")
    private List<MaterialRequisitionItemVO> items;
}