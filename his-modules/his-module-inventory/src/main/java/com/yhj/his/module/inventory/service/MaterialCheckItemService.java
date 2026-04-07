package com.yhj.his.module.inventory.service;

import com.yhj.his.module.inventory.entity.MaterialCheckItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 库存盘点明细Service接口
 */
public interface MaterialCheckItemService {

    /**
     * 根据ID查询盘点明细
     */
    Optional<MaterialCheckItem> findById(String id);

    /**
     * 查询所有盘点明细
     */
    List<MaterialCheckItem> findAll();

    /**
     * 根据盘点记录ID查询
     */
    List<MaterialCheckItem> findByCheckId(String checkId);

    /**
     * 根据物资ID查询
     */
    List<MaterialCheckItem> findByMaterialId(String materialId);

    /**
     * 查询有差异的盘点明细
     */
    List<MaterialCheckItem> findDiffItems(String checkId);

    /**
     * 查询未调整的盘点明细
     */
    List<MaterialCheckItem> findUnadjustedDiffItems(String checkId);

    /**
     * 创建盘点明细
     */
    MaterialCheckItem create(MaterialCheckItem item);

    /**
     * 更新盘点明细
     */
    MaterialCheckItem update(String id, MaterialCheckItem item);

    /**
     * 删除盘点明细
     */
    void delete(String id);

    /**
     * 批量删除盘点明细
     */
    void deleteBatch(List<String> ids);

    /**
     * 根据盘点记录ID删除所有明细
     */
    void deleteByCheckId(String checkId);

    /**
     * 计算差异金额
     */
    BigDecimal calculateDiffAmount(MaterialCheckItem item);

    /**
     * 输入实盘数量
     */
    MaterialCheckItem inputActualQuantity(String id, BigDecimal actualQuantity, String remark);

    /**
     * 调整库存
     */
    MaterialCheckItem adjust(String id);

    /**
     * 计算差异
     */
    void calculateDiff(MaterialCheckItem item);
}