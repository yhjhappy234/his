package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.module.inventory.entity.MaterialInbound;
import com.yhj.his.module.inventory.entity.MaterialInboundItem;
import com.yhj.his.module.inventory.enums.InboundStatus;
import com.yhj.his.module.inventory.enums.InboundType;
import com.yhj.his.module.inventory.repository.MaterialInboundItemRepository;
import com.yhj.his.module.inventory.repository.MaterialInboundRepository;
import com.yhj.his.module.inventory.service.MaterialInboundItemService;
import com.yhj.his.module.inventory.service.MaterialInboundService;
import com.yhj.his.module.inventory.service.MaterialInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 入库记录Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialInboundServiceImpl implements MaterialInboundService {

    private final MaterialInboundRepository materialInboundRepository;
    private final MaterialInboundItemRepository materialInboundItemRepository;
    private final MaterialInboundItemService materialInboundItemService;
    private final MaterialInventoryService materialInventoryService;

    @Override
    public Optional<MaterialInbound> findById(String id) {
        return materialInboundRepository.findById(id);
    }

    @Override
    public Optional<MaterialInbound> findByInboundNo(String inboundNo) {
        return materialInboundRepository.findByInboundNo(inboundNo);
    }

    @Override
    public List<MaterialInbound> findAll() {
        return materialInboundRepository.findByDeletedFalse(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<MaterialInbound> findAll(Pageable pageable) {
        return materialInboundRepository.findByDeletedFalse(pageable);
    }

    @Override
    public List<MaterialInbound> findByStatus(InboundStatus status) {
        return materialInboundRepository.findByStatusAndDeletedFalse(status);
    }

    @Override
    public List<MaterialInbound> findByInboundType(InboundType inboundType) {
        return materialInboundRepository.findByInboundTypeAndDeletedFalse(inboundType);
    }

    @Override
    public List<MaterialInbound> findByWarehouseId(String warehouseId) {
        return materialInboundRepository.findByWarehouseIdAndDeletedFalse(warehouseId);
    }

    @Override
    public List<MaterialInbound> findByInboundDate(LocalDate inboundDate) {
        return materialInboundRepository.findByInboundDateAndDeletedFalse(inboundDate);
    }

    @Override
    public Page<MaterialInbound> findByStatus(InboundStatus status, Pageable pageable) {
        return materialInboundRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Page<MaterialInbound> search(String warehouseId, InboundStatus status, InboundType inboundType,
                                        LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return materialInboundRepository.search(warehouseId, status, inboundType, startDate, endDate, pageable);
    }

    @Override
    public List<MaterialInbound> findPendingInbounds() {
        return materialInboundRepository.findPendingInbounds();
    }

    @Override
    @Transactional
    public MaterialInbound create(MaterialInbound materialInbound) {
        materialInbound.setInboundNo(generateInboundNo());
        materialInbound.setStatus(InboundStatus.PENDING);
        materialInbound.setInboundDate(LocalDate.now());
        materialInbound.setApplyTime(LocalDateTime.now());
        return materialInboundRepository.save(materialInbound);
    }

    @Override
    @Transactional
    public MaterialInbound createWithItems(MaterialInbound materialInbound, List<MaterialInboundItem> items) {
        MaterialInbound saved = create(materialInbound);
        for (MaterialInboundItem item : items) {
            item.setInbound(saved);
            materialInboundItemRepository.save(item);
        }
        calculateTotal(saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public MaterialInbound update(String id, MaterialInbound materialInbound) {
        MaterialInbound existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("入库记录不存在: " + id));

        if (existing.getStatus() != InboundStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核状态的入库单可以修改");
        }

        existing.setWarehouseId(materialInbound.getWarehouseId());
        existing.setWarehouseName(materialInbound.getWarehouseName());
        existing.setSupplierId(materialInbound.getSupplierId());
        existing.setSupplierName(materialInbound.getSupplierName());
        existing.setInboundType(materialInbound.getInboundType());
        existing.setRemark(materialInbound.getRemark());

        return materialInboundRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        MaterialInbound inbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("入库记录不存在: " + id));

        if (inbound.getStatus() == InboundStatus.CONFIRMED) {
            throw new IllegalArgumentException("已入库的记录不能删除");
        }

        inbound.setDeleted(true);
        materialInboundRepository.save(inbound);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    @Transactional
    public MaterialInbound submit(String id) {
        MaterialInbound inbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("入库记录不存在: " + id));

        inbound.setStatus(InboundStatus.PENDING);
        inbound.setApplyTime(LocalDateTime.now());
        return materialInboundRepository.save(inbound);
    }

    @Override
    @Transactional
    public MaterialInbound audit(String id, String auditorId, String auditorName, boolean approved, String auditRemark) {
        MaterialInbound inbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("入库记录不存在: " + id));

        if (inbound.getStatus() != InboundStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核状态的入库单可以审核");
        }

        inbound.setAuditorId(auditorId);
        inbound.setAuditorName(auditorName);
        inbound.setAuditTime(LocalDateTime.now());
        inbound.setAuditRemark(auditRemark);

        if (approved) {
            inbound.setStatus(InboundStatus.AUDITED);
        } else {
            inbound.setStatus(InboundStatus.REJECTED);
        }

        return materialInboundRepository.save(inbound);
    }

    @Override
    @Transactional
    public MaterialInbound confirm(String id, String operatorId, String operatorName) {
        MaterialInbound inbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("入库记录不存在: " + id));

        if (inbound.getStatus() != InboundStatus.AUDITED) {
            throw new IllegalArgumentException("只有已审核状态的入库单可以确认入库");
        }

        // 入库操作：更新库存
        List<MaterialInboundItem> items = getItems(id);
        for (MaterialInboundItem item : items) {
            materialInventoryService.inboundStock(
                    item.getMaterialId(),
                    inbound.getWarehouseId(),
                    item.getBatchNo(),
                    item.getQuantity(),
                    item.getPurchasePrice(),
                    item.getRetailPrice(),
                    item.getExpiryDate(),
                    inbound.getSupplierId(),
                    inbound.getSupplierName()
            );
        }

        inbound.setOperatorId(operatorId);
        inbound.setOperatorName(operatorName);
        inbound.setInboundTime(LocalDateTime.now());
        inbound.setStatus(InboundStatus.CONFIRMED);

        return materialInboundRepository.save(inbound);
    }

    @Override
    @Transactional
    public MaterialInbound cancel(String id) {
        MaterialInbound inbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("入库记录不存在: " + id));

        if (inbound.getStatus() == InboundStatus.CONFIRMED) {
            throw new IllegalArgumentException("已入库的记录不能取消");
        }

        inbound.setStatus(InboundStatus.CANCELLED);
        return materialInboundRepository.save(inbound);
    }

    @Override
    public String generateInboundNo() {
        String prefix = "IN";
        String dateStr = LocalDate.now().toString().replace("-", "");
        long count = materialInboundRepository.findByDeletedFalse(Pageable.unpaged()).getTotalElements() + 1;
        return prefix + dateStr + String.format("%04d", count);
    }

    @Override
    @Transactional
    public void calculateTotal(String id) {
        MaterialInbound inbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("入库记录不存在: " + id));

        List<MaterialInboundItem> items = getItems(id);
        BigDecimal totalQuantity = items.stream()
                .map(MaterialInboundItem::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = items.stream()
                .map(MaterialInboundItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        inbound.setTotalQuantity(totalQuantity);
        inbound.setTotalAmount(totalAmount);
        materialInboundRepository.save(inbound);
    }

    @Override
    @Transactional
    public MaterialInboundItem addItem(String inboundId, MaterialInboundItem item) {
        MaterialInbound inbound = findById(inboundId)
                .orElseThrow(() -> new IllegalArgumentException("入库记录不存在: " + inboundId));

        if (inbound.getStatus() != InboundStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核状态的入库单可以添加明细");
        }

        item.setInbound(inbound);
        item.setAmount(materialInboundItemService.calculateAmount(item));
        MaterialInboundItem saved = materialInboundItemRepository.save(item);
        calculateTotal(inboundId);
        return saved;
    }

    @Override
    @Transactional
    public void deleteItem(String itemId) {
        MaterialInboundItem item = materialInboundItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("入库明细不存在: " + itemId));

        if (item.getInbound().getStatus() != InboundStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核状态的入库单可以删除明细");
        }

        materialInboundItemRepository.delete(item);
        calculateTotal(item.getInbound().getId());
    }

    @Override
    public List<MaterialInboundItem> getItems(String inboundId) {
        return materialInboundItemRepository.findByInboundId(inboundId);
    }
}