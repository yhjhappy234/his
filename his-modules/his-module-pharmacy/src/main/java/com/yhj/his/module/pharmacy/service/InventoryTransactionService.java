package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.entity.InventoryTransaction;
import com.yhj.his.module.pharmacy.enums.InventoryOperationType;

import java.time.LocalDate;
import java.util.List;

/**
 * 库存流水服务接口
 */
public interface InventoryTransactionService {

    /**
     * 根据ID查询流水
     */
    Result<InventoryTransaction> getTransactionById(String transactionId);

    /**
     * 根据流水号查询
     */
    Result<InventoryTransaction> getTransactionByNo(String transactionNo);

    /**
     * 分页查询流水列表
     */
    Result<PageResult<InventoryTransaction>> queryTransactions(String pharmacyId, String drugId,
                                                                InventoryOperationType operationType,
                                                                LocalDate startDate, LocalDate endDate,
                                                                Integer pageNum, Integer pageSize);

    /**
     * 查询药品流水记录
     */
    Result<List<InventoryTransaction>> getDrugTransactions(String drugId);

    /**
     * 查询药房流水记录
     */
    Result<PageResult<InventoryTransaction>> getPharmacyTransactions(String pharmacyId,
                                                                      LocalDate startDate, LocalDate endDate,
                                                                      Integer pageNum, Integer pageSize);

    /**
     * 查询关联单据流水
     */
    Result<List<InventoryTransaction>> getTransactionsByRelatedId(String relatedId);

    /**
     * 查询入库流水
     */
    Result<PageResult<InventoryTransaction>> getInboundTransactions(String pharmacyId,
                                                                     LocalDate startDate, LocalDate endDate,
                                                                     Integer pageNum, Integer pageSize);

    /**
     * 查询出库流水
     */
    Result<PageResult<InventoryTransaction>> getOutboundTransactions(String pharmacyId,
                                                                      LocalDate startDate, LocalDate endDate,
                                                                      Integer pageNum, Integer pageSize);

    /**
     * 统计药房出入库汇总
     */
    Result<Object> getTransactionSummary(String pharmacyId, LocalDate startDate, LocalDate endDate);
}