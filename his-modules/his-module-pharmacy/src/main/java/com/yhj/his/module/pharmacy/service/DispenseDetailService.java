package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.entity.DispenseDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发药明细服务接口
 */
public interface DispenseDetailService {

    /**
     * 创建发药明细
     */
    Result<DispenseDetail> createDispenseDetail(DispenseDetail detail);

    /**
     * 更新发药明细
     */
    Result<DispenseDetail> updateDispenseDetail(DispenseDetail detail);

    /**
     * 根据ID删除发药明细
     */
    Result<Void> deleteDispenseDetail(String detailId);

    /**
     * 根据ID查询发药明细
     */
    Result<DispenseDetail> getDispenseDetailById(String detailId);

    /**
     * 根据发药ID查询明细列表
     */
    Result<List<DispenseDetail>> getDetailsByDispenseId(String dispenseId);

    /**
     * 根据药品ID查询明细列表
     */
    Result<List<DispenseDetail>> getDetailsByDrugId(String drugId);

    /**
     * 根据批号查询明细列表
     */
    Result<List<DispenseDetail>> getDetailsByBatchNo(String batchNo);

    /**
     * 更新审核结果
     */
    Result<Void> updateAuditResult(String detailId, String auditResult, String auditRemark);

    /**
     * 批量创建发药明细
     */
    Result<List<DispenseDetail>> batchCreateDetails(List<DispenseDetail> details);

    /**
     * 删除发药记录的所有明细
     */
    Result<Void> deleteByDispenseId(String dispenseId);

    /**
     * 批量查询发药明细
     */
    Result<List<DispenseDetail>> getDetailsByIds(List<String> detailIds);

    /**
     * 计算发药明细总金额
     */
    BigDecimal calculateTotalAmount(List<DispenseDetail> details);
}