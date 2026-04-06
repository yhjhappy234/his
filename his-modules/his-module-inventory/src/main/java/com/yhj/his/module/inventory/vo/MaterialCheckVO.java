package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 库存盘点VO
 */
@Data
@Schema(description = "库存盘点VO")
public class MaterialCheckVO {

    @Schema(description = "盘点ID")
    private String id;

    @Schema(description = "盘点单号")
    private String checkNo;

    @Schema(description = "盘点类型")
    private String checkType;

    @Schema(description = "盘点类型名称")
    private String checkTypeName;

    @Schema(description = "库房ID")
    private String warehouseId;

    @Schema(description = "库房名称")
    private String warehouseName;

    @Schema(description = "盘点日期")
    private LocalDate checkDate;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "盘点人ID")
    private String checkerId;

    @Schema(description = "盘点人姓名")
    private String checkerName;

    @Schema(description = "盘点物资数")
    private Integer totalCount;

    @Schema(description = "盘盈数量")
    private Integer profitCount;

    @Schema(description = "盘亏数量")
    private Integer lossCount;

    @Schema(description = "盘盈金额")
    private BigDecimal profitAmount;

    @Schema(description = "盘亏金额")
    private BigDecimal lossAmount;

    @Schema(description = "调整人ID")
    private String adjusterId;

    @Schema(description = "调整人姓名")
    private String adjusterName;

    @Schema(description = "调整时间")
    private LocalDateTime adjustTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "盘点明细")
    private List<MaterialCheckItemVO> items;
}