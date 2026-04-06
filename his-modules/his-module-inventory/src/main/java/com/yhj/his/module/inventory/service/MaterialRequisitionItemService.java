package com.yhj.his.module.inventory.service;

import com.yhj.his.module.inventory.entity.MaterialRequisitionItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 物资申领明细Service接口
 */
public interface MaterialRequisitionItemService {

    /**
     * 根据ID查询申领明细
     */
    Optional<MaterialRequisitionItem> findById(String id);

    /**
     * 查询所有申领明细
     */
    List<MaterialRequisitionItem> findAll();

    /**
     * 根据申领记录ID查询
     */
    List<MaterialRequisitionItem> findByRequisitionId(String requisitionId);

    /**
     * 根据物资ID查询
     */
    List<MaterialRequisitionItem> findByMaterialId(String materialId);

    /**
     * 创建申领明细
     */
    MaterialRequisitionItem create(MaterialRequisitionItem item);

    /**
     * 更新申领明细
     */
    MaterialRequisitionItem update(String id, MaterialRequisitionItem item);

    /**
     * 删除申领明细
     */
    void delete(String id);

    /**
     * 批量删除申领明细
     */
    void deleteBatch(List<String> ids);

    /**
     * 根据申领记录ID删除所有明细
     */
    void deleteByRequisitionId(String requisitionId);

    /**
     * 计算明细金额
     */
    BigDecimal calculateAmount(MaterialRequisitionItem item);

    /**
     * 更新实发数量
     */
    MaterialRequisitionItem updateIssueQuantity(String id, BigDecimal issueQuantity);
}