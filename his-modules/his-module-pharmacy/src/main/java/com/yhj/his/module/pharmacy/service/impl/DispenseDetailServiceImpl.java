package com.yhj.his.module.pharmacy.service.impl;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.pharmacy.entity.DispenseDetail;
import com.yhj.his.module.pharmacy.repository.DispenseDetailRepository;
import com.yhj.his.module.pharmacy.service.DispenseDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发药明细服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DispenseDetailServiceImpl implements DispenseDetailService {

    private final DispenseDetailRepository dispenseDetailRepository;

    @Override
    @Transactional
    public Result<DispenseDetail> createDispenseDetail(DispenseDetail detail) {
        // 计算金额
        if (detail.getAmount() == null && detail.getQuantity() != null && detail.getRetailPrice() != null) {
            detail.setAmount(detail.getQuantity().multiply(detail.getRetailPrice()));
        }

        DispenseDetail saved = dispenseDetailRepository.save(detail);
        log.info("创建发药明细成功: dispenseId={}, drugId={}", saved.getDispenseId(), saved.getDrugId());
        return Result.success("发药明细创建成功", saved);
    }

    @Override
    @Transactional
    public Result<DispenseDetail> updateDispenseDetail(DispenseDetail detail) {
        DispenseDetail existing = dispenseDetailRepository.findById(detail.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "发药明细不存在"));

        // 计算金额
        if (detail.getQuantity() != null && detail.getRetailPrice() != null) {
            detail.setAmount(detail.getQuantity().multiply(detail.getRetailPrice()));
        }

        DispenseDetail saved = dispenseDetailRepository.save(detail);
        log.info("更新发药明细成功: detailId={}", saved.getId());
        return Result.success("发药明细更新成功", saved);
    }

    @Override
    @Transactional
    public Result<Void> deleteDispenseDetail(String detailId) {
        DispenseDetail detail = dispenseDetailRepository.findById(detailId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "发药明细不存在"));

        detail.setDeleted(true);
        dispenseDetailRepository.save(detail);
        log.info("删除发药明细成功: detailId={}", detailId);
        return Result.successVoid();
    }

    @Override
    public Result<DispenseDetail> getDispenseDetailById(String detailId) {
        DispenseDetail detail = dispenseDetailRepository.findById(detailId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "发药明细不存在"));
        return Result.success(detail);
    }

    @Override
    public Result<List<DispenseDetail>> getDetailsByDispenseId(String dispenseId) {
        List<DispenseDetail> details = dispenseDetailRepository.findByDispenseIdOrderById(dispenseId);
        return Result.success(details);
    }

    @Override
    public Result<List<DispenseDetail>> getDetailsByDrugId(String drugId) {
        List<DispenseDetail> details = dispenseDetailRepository.findByDrugId(drugId);
        return Result.success(details);
    }

    @Override
    public Result<List<DispenseDetail>> getDetailsByBatchNo(String batchNo) {
        List<DispenseDetail> details = dispenseDetailRepository.findByBatchNo(batchNo);
        return Result.success(details);
    }

    @Override
    @Transactional
    public Result<Void> updateAuditResult(String detailId, String auditResult, String auditRemark) {
        DispenseDetail detail = dispenseDetailRepository.findById(detailId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "发药明细不存在"));

        detail.setAuditResult(auditResult);
        detail.setAuditRemark(auditRemark);
        dispenseDetailRepository.save(detail);

        log.info("更新发药明细审核结果: detailId={}, auditResult={}", detailId, auditResult);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<List<DispenseDetail>> batchCreateDetails(List<DispenseDetail> details) {
        // 计算金额
        for (DispenseDetail detail : details) {
            if (detail.getAmount() == null && detail.getQuantity() != null && detail.getRetailPrice() != null) {
                detail.setAmount(detail.getQuantity().multiply(detail.getRetailPrice()));
            }
        }

        List<DispenseDetail> savedDetails = dispenseDetailRepository.saveAll(details);
        log.info("批量创建发药明细成功: count={}", savedDetails.size());
        return Result.success("发药明细批量创建成功", savedDetails);
    }

    @Override
    @Transactional
    public Result<Void> deleteByDispenseId(String dispenseId) {
        dispenseDetailRepository.deleteByDispenseId(dispenseId);
        log.info("删除发药记录所有明细成功: dispenseId={}", dispenseId);
        return Result.successVoid();
    }

    @Override
    public Result<List<DispenseDetail>> getDetailsByIds(List<String> detailIds) {
        List<DispenseDetail> details = dispenseDetailRepository.findAllById(detailIds);
        return Result.success(details);
    }

    @Override
    public BigDecimal calculateTotalAmount(List<DispenseDetail> details) {
        return details.stream()
                .filter(detail -> detail.getAmount() != null)
                .map(DispenseDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}