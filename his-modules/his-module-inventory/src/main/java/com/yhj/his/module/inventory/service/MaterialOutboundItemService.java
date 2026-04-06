package com.yhj.his.module.inventory.service;

import com.yhj.his.module.inventory.entity.MaterialOutboundItem;

import java.util.List;
import java.util.Optional;

/**
 * 出库明细Service接口
 */
public interface MaterialOutboundItemService {

    /**
     * 根据ID查询出库明细
     */
    Optional<MaterialOutboundItem> findById(String id);

    /**
     * 查询所有出库明细
     */
    List<MaterialOutboundItem> findAll();

    /**
     * 根据出库记录ID查询
     */
    List<MaterialOutboundItem> findByOutboundId(String outboundId);

    /**
     * 根据物资ID查询
     */
    List<MaterialOutboundItem> findByMaterialId(String materialId);

    /**
     * 创建出库明细
     */
    MaterialOutboundItem create(MaterialOutboundItem item);

    /**
     * 更新出库明细
     */
    MaterialOutboundItem update(String id, MaterialOutboundItem item);

    /**
     * 删除出库明细
     */
    void delete(String id);

    /**
     * 批量删除出库明细
     */
    void deleteBatch(List<String> ids);

    /**
     * 根据出库记录ID删除所有明细
     */
    void deleteByOutboundId(String outboundId);

    /**
     * 计算明细金额
     */
    java.math.BigDecimal calculateAmount(MaterialOutboundItem item);
}