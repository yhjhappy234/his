package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存预警VO
 */
@Data
@Schema(description = "库存预警VO")
public class MaterialAlertVO {

    @Schema(description = "预警ID")
    private String id;

    @Schema(description = "预警类型")
    private String alertType;

    @Schema(description = "预警类型名称")
    private String alertTypeName;

    @Schema(description = "物资ID")
    private String materialId;

    @Schema(description = "物资编码")
    private String materialCode;

    @Schema(description = "物资名称")
    private String materialName;

    @Schema(description = "规格")
    private String materialSpec;

    @Schema(description = "单位")
    private String materialUnit;

    @Schema(description = "库房ID")
    private String warehouseId;

    @Schema(description = "库房名称")
    private String warehouseName;

    @Schema(description = "批号")
    private String batchNo;

    @Schema(description = "当前库存量")
    private BigDecimal currentQuantity;

    @Schema(description = "预警阈值")
    private BigDecimal alertThreshold;

    @Schema(description = "有效期")
    private LocalDate expiryDate;

    @Schema(description = "预警内容")
    private String alertContent;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "处理人ID")
    private String handlerId;

    @Schema(description = "处理人姓名")
    private String handlerName;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理备注")
    private String handleRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}