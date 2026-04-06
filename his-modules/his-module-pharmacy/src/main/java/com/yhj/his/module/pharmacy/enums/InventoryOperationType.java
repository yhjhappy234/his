package com.yhj.his.module.pharmacy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum InventoryOperationType {

    IN_PURCHASE("采购入库", "采购入库"),
    IN_RETURN("退药入库", "退药入库"),
    IN_ADJUST("盘盈入库", "盘盈入库"),
    INBOUND("入库", "入库"),
    OUT_DISPENSE("发药出库", "发药出库"),
    OUT_RETURN("退货出库", "退货出库"),
    OUT_EXPIRE("过期报损", "过期报损"),
    OUT_ADJUST("盘亏出库", "盘亏出库"),
    OUTBOUND("出库", "出库"),
    TRANSFER_IN("调拨入库", "调拨入库"),
    TRANSFER_OUT("调拨出库", "调拨出库"),
    ADJUSTMENT("库存调整", "库存调整");

    private final String name;
    private final String description;
}