package com.yhj.his.module.pharmacy.service.impl;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.pharmacy.entity.PurchaseOrderItem;
import com.yhj.his.module.pharmacy.repository.PurchaseOrderItemRepository;
import com.yhj.his.module.pharmacy.service.PurchaseOrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购订单明细服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderItemServiceImpl implements PurchaseOrderItemService {

    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Override
    @Transactional
    public Result<PurchaseOrderItem> createPurchaseOrderItem(PurchaseOrderItem item) {
        // 计算金额
        if (item.getAmount() == null && item.getQuantity() != null && item.getPurchasePrice() != null) {
            item.setAmount(item.getQuantity().multiply(item.getPurchasePrice()));
        }

        // 设置默认已入库数量
        if (item.getReceivedQuantity() == null) {
            item.setReceivedQuantity(BigDecimal.ZERO);
        }

        PurchaseOrderItem saved = purchaseOrderItemRepository.save(item);
        log.info("创建采购订单明细成功: orderId={}, drugId={}", saved.getOrderId(), saved.getDrugId());
        return Result.success("采购订单明细创建成功", saved);
    }

    @Override
    @Transactional
    public Result<PurchaseOrderItem> updatePurchaseOrderItem(PurchaseOrderItem item) {
        PurchaseOrderItem existing = purchaseOrderItemRepository.findById(item.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "采购订单明细不存在"));

        // 计算金额
        if (item.getQuantity() != null && item.getPurchasePrice() != null) {
            item.setAmount(item.getQuantity().multiply(item.getPurchasePrice()));
        }

        PurchaseOrderItem saved = purchaseOrderItemRepository.save(item);
        log.info("更新采购订单明细成功: itemId={}", saved.getId());
        return Result.success("采购订单明细更新成功", saved);
    }

    @Override
    @Transactional
    public Result<Void> deletePurchaseOrderItem(String itemId) {
        PurchaseOrderItem item = purchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "采购订单明细不存在"));

        item.setDeleted(true);
        purchaseOrderItemRepository.save(item);
        log.info("删除采购订单明细成功: itemId={}", itemId);
        return Result.successVoid();
    }

    @Override
    public Result<PurchaseOrderItem> getPurchaseOrderItemById(String itemId) {
        PurchaseOrderItem item = purchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "采购订单明细不存在"));
        return Result.success(item);
    }

    @Override
    public Result<List<PurchaseOrderItem>> getItemsByOrderId(String orderId) {
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderIdOrderById(orderId);
        return Result.success(items);
    }

    @Override
    public Result<List<PurchaseOrderItem>> getItemsByDrugId(String drugId) {
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByDrugId(drugId);
        return Result.success(items);
    }

    @Override
    @Transactional
    public Result<Void> updateReceivedQuantity(String itemId, BigDecimal receivedQuantity) {
        PurchaseOrderItem item = purchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "采购订单明细不存在"));

        // 验证入库数量不能超过采购数量
        if (receivedQuantity.compareTo(item.getQuantity()) > 0) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "入库数量不能超过采购数量");
        }

        item.setReceivedQuantity(receivedQuantity);
        purchaseOrderItemRepository.save(item);

        log.info("更新采购订单明细已入库数量: itemId={}, receivedQuantity={}", itemId, receivedQuantity);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> addReceivedQuantity(String itemId, BigDecimal quantity) {
        PurchaseOrderItem item = purchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "采购订单明细不存在"));

        BigDecimal newReceivedQuantity = item.getReceivedQuantity().add(quantity);

        // 验证入库数量不能超过采购数量
        if (newReceivedQuantity.compareTo(item.getQuantity()) > 0) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "入库数量不能超过采购数量");
        }

        item.setReceivedQuantity(newReceivedQuantity);
        purchaseOrderItemRepository.save(item);

        log.info("增加采购订单明细已入库数量: itemId={}, addQuantity={}, totalReceived={}",
                itemId, quantity, newReceivedQuantity);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<List<PurchaseOrderItem>> batchCreateItems(List<PurchaseOrderItem> items) {
        // 设置默认值并计算金额
        for (PurchaseOrderItem item : items) {
            if (item.getReceivedQuantity() == null) {
                item.setReceivedQuantity(BigDecimal.ZERO);
            }
            if (item.getAmount() == null && item.getQuantity() != null && item.getPurchasePrice() != null) {
                item.setAmount(item.getQuantity().multiply(item.getPurchasePrice()));
            }
        }

        List<PurchaseOrderItem> savedItems = purchaseOrderItemRepository.saveAll(items);
        log.info("批量创建采购订单明细成功: count={}", savedItems.size());
        return Result.success("采购订单明细批量创建成功", savedItems);
    }

    @Override
    @Transactional
    public Result<Void> deleteByOrderId(String orderId) {
        purchaseOrderItemRepository.deleteByOrderId(orderId);
        log.info("删除订单所有明细成功: orderId={}", orderId);
        return Result.successVoid();
    }

    @Override
    public Result<List<PurchaseOrderItem>> getItemsByIds(List<String> itemIds) {
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllById(itemIds);
        return Result.success(items);
    }

    @Override
    public BigDecimal calculateTotalAmount(List<PurchaseOrderItem> items) {
        return items.stream()
                .filter(item -> item.getAmount() != null)
                .map(PurchaseOrderItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalQuantity(List<PurchaseOrderItem> items) {
        return items.stream()
                .filter(item -> item.getQuantity() != null)
                .map(PurchaseOrderItem::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}