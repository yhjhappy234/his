package com.yhj.his.module.pharmacy.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.entity.InventoryTransaction;
import com.yhj.his.module.pharmacy.enums.InventoryOperationType;
import com.yhj.his.module.pharmacy.repository.InventoryTransactionRepository;
import com.yhj.his.module.pharmacy.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 库存流水服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;

    @Override
    public Result<InventoryTransaction> getTransactionById(String transactionId) {
        Optional<InventoryTransaction> optional = transactionRepository.findById(transactionId);
        if (!optional.isPresent()) {
            return Result.error("流水记录不存在: " + transactionId);
        }
        return Result.success(optional.get());
    }

    @Override
    public Result<InventoryTransaction> getTransactionByNo(String transactionNo) {
        Optional<InventoryTransaction> optional = transactionRepository.findByTransactionNo(transactionNo);
        if (!optional.isPresent()) {
            return Result.error("流水记录不存在: " + transactionNo);
        }
        return Result.success(optional.get());
    }

    @Override
    public Result<PageResult<InventoryTransaction>> queryTransactions(String pharmacyId, String drugId,
                                                                       InventoryOperationType operationType,
                                                                       LocalDate startDate, LocalDate endDate,
                                                                       Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        Page<InventoryTransaction> page = transactionRepository.queryTransactions(
                pharmacyId, drugId, operationType, startDateTime, endDateTime, pageable);
        List<InventoryTransaction> list = page.getContent();
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<List<InventoryTransaction>> getDrugTransactions(String drugId) {
        List<InventoryTransaction> list = transactionRepository.findByDrugIdOrderByOperateTimeDesc(drugId);
        return Result.success(list);
    }

    @Override
    public Result<PageResult<InventoryTransaction>> getPharmacyTransactions(String pharmacyId,
                                                                             LocalDate startDate, LocalDate endDate,
                                                                             Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        Page<InventoryTransaction> page = transactionRepository.findByPharmacyIdAndDateRange(
                pharmacyId, startDateTime, endDateTime, pageable);
        return Result.success(PageResult.of(page.getContent(), page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<List<InventoryTransaction>> getTransactionsByRelatedId(String relatedId) {
        List<InventoryTransaction> list = transactionRepository.findByRelatedId(relatedId);
        return Result.success(list);
    }

    @Override
    public Result<PageResult<InventoryTransaction>> getInboundTransactions(String pharmacyId,
                                                                            LocalDate startDate, LocalDate endDate,
                                                                            Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        Page<InventoryTransaction> page = transactionRepository.findByPharmacyIdAndTransactionTypeAndDateRange(
                pharmacyId, InventoryOperationType.INBOUND, startDateTime, endDateTime, pageable);
        return Result.success(PageResult.of(page.getContent(), page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<PageResult<InventoryTransaction>> getOutboundTransactions(String pharmacyId,
                                                                             LocalDate startDate, LocalDate endDate,
                                                                             Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        Page<InventoryTransaction> page = transactionRepository.findByPharmacyIdAndTransactionTypeAndDateRange(
                pharmacyId, InventoryOperationType.OUTBOUND, startDateTime, endDateTime, pageable);
        return Result.success(PageResult.of(page.getContent(), page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<Object> getTransactionSummary(String pharmacyId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        Long inboundCount = transactionRepository.countByPharmacyIdAndTransactionTypeAndDateRange(
                pharmacyId, InventoryOperationType.INBOUND, startDateTime, endDateTime);
        Long outboundCount = transactionRepository.countByPharmacyIdAndTransactionTypeAndDateRange(
                pharmacyId, InventoryOperationType.OUTBOUND, startDateTime, endDateTime);
        java.math.BigDecimal inboundAmount = transactionRepository.sumAmountByPharmacyIdAndTransactionTypeAndDateRange(
                pharmacyId, InventoryOperationType.INBOUND, startDateTime, endDateTime);
        java.math.BigDecimal outboundAmount = transactionRepository.sumAmountByPharmacyIdAndTransactionTypeAndDateRange(
                pharmacyId, InventoryOperationType.OUTBOUND, startDateTime, endDateTime);

        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("inboundCount", inboundCount);
        summary.put("outboundCount", outboundCount);
        summary.put("inboundAmount", inboundAmount != null ? inboundAmount : java.math.BigDecimal.ZERO);
        summary.put("outboundAmount", outboundAmount != null ? outboundAmount : java.math.BigDecimal.ZERO);
        return Result.success(summary);
    }
}