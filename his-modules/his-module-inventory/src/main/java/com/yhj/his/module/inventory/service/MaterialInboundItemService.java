package com.yhj.his.module.inventory.service;

import com.yhj.his.module.inventory.entity.MaterialInboundItem;

import java.util.List;
import java.util.Optional;

/**
 * 入库明细Service接口
 */
public interface MaterialInboundItemService {

    /**
     * 根据ID查询入库明细
     */
    Optional<MaterialInboundItem> findById(String id);

    /**
     * 查询所有入库明细
     */
    List<MaterialInboundItem> findAll();

    /**
     * 根据入库记录ID查询
     */
    List<MaterialInboundItem> findByInboundId(String inboundId);

    /**
     * 根据物资ID查询
     */
    List<MaterialInboundItem> findByMaterialId(String materialId);

    /**
     * 创建入库明细
     */
    MaterialInboundItem create(MaterialInboundItem item);

    /**
     * 更新入库明细
     */
    MaterialInboundItem update(String id, MaterialInboundItem item);

    /**
     * 删除入库明细
     */
    void delete(String id);

    /**
     * 批量删除入库明细
     */
    void deleteBatch(List<String> ids);

    /**
     * 根据入库记录ID删除所有明细
     */
    void deleteByInboundId(String inboundId);

    /**
     * 计算明细金额
     */
    java.math.BigDecimal calculateAmount(MaterialInboundItem item);
}