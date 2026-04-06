package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库房信息VO
 */
@Data
@Schema(description = "库房信息VO")
public class WarehouseVO {

    @Schema(description = "库房ID")
    private String id;

    @Schema(description = "库房编码")
    private String warehouseCode;

    @Schema(description = "库房名称")
    private String warehouseName;

    @Schema(description = "库房类型")
    private String warehouseType;

    @Schema(description = "库房类型名称")
    private String warehouseTypeName;

    @Schema(description = "所属科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "管理员ID")
    private String managerId;

    @Schema(description = "管理员姓名")
    private String managerName;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "库存物资种类数")
    private Integer materialCount;

    @Schema(description = "库存总金额")
    private BigDecimal totalAmount;
}