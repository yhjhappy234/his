package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.module.inventory.entity.MaterialOutbound;
import com.yhj.his.module.inventory.entity.MaterialOutboundItem;
import com.yhj.his.module.inventory.enums.OutboundStatus;
import com.yhj.his.module.inventory.enums.OutboundType;
import com.yhj.his.module.inventory.repository.MaterialOutboundItemRepository;
import com.yhj.his.module.inventory.repository.MaterialOutboundRepository;
import com.yhj.his.module.inventory.service.MaterialInventoryService;
import com.yhj.his.module.inventory.service.MaterialOutboundItemService;
import com.yhj.his.module.inventory.service.MaterialOutboundService;
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
 * 出库记录Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialOutboundServiceImpl implements MaterialOutboundService {

    private final MaterialOutboundRepository materialOutboundRepository;
    private final MaterialOutboundItemRepository materialOutboundItemRepository;
    private final MaterialOutboundItemService materialOutboundItemService;
    private final MaterialInventoryService materialInventoryService;

    @Override
    public Optional<MaterialOutbound> findById(String id) {
        return materialOutboundRepository.findById(id);
    }

    @Override
    public Optional<MaterialOutbound> findByOutboundNo(String outboundNo) {
        return materialOutboundRepository.findByOutboundNo(outboundNo);
    }

    @Override
    public List<MaterialOutbound> findAll() {
        return materialOutboundRepository.findByDeletedFalse(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<MaterialOutbound> findAll(Pageable pageable) {
        return materialOutboundRepository.findByDeletedFalse(pageable);
    }

    @Override
    public List<MaterialOutbound> findByStatus(OutboundStatus status) {
        return materialOutboundRepository.findByStatusAndDeletedFalse(status);
    }

    @Override
    public List<MaterialOutbound> findByOutboundType(OutboundType outboundType) {
        return materialOutboundRepository.findByOutboundTypeAndDeletedFalse(outboundType);
    }

    @Override
    public List<MaterialOutbound> findByWarehouseId(String warehouseId) {
        return materialOutboundRepository.findByWarehouseIdAndDeletedFalse(warehouseId);
    }

    @Override
    public List<MaterialOutbound> findByTargetDeptId(String targetDeptId) {
        return materialOutboundRepository.findByTargetDeptIdAndDeletedFalse(targetDeptId);
    }

    @Override
    public List<MaterialOutbound> findByOutboundDate(LocalDate outboundDate) {
        return materialOutboundRepository.findByOutboundDateAndDeletedFalse(outboundDate);
    }

    @Override
    public Page<MaterialOutbound> findByStatus(OutboundStatus status, Pageable pageable) {
        return materialOutboundRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Page<MaterialOutbound> search(String warehouseId, OutboundStatus status, OutboundType outboundType,
                                         String targetDeptId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return materialOutboundRepository.search(warehouseId, status, outboundType, targetDeptId, startDate, endDate, pageable);
    }

    @Override
    public List<MaterialOutbound> findPendingOutbounds() {
        return materialOutboundRepository.findPendingOutbounds();
    }

    @Override
    @Transactional
    public MaterialOutbound create(MaterialOutbound materialOutbound) {
        materialOutbound.setOutboundNo(generateOutboundNo());
        materialOutbound.setStatus(OutboundStatus.PENDING);
        materialOutbound.setOutboundDate(LocalDate.now());
        materialOutbound.setApplyTime(LocalDateTime.now());
        return materialOutboundRepository.save(materialOutbound);
    }

    @Override
    @Transactional
    public MaterialOutbound createWithItems(MaterialOutbound materialOutbound, List<MaterialOutboundItem> items) {
        MaterialOutbound saved = create(materialOutbound);
        for (MaterialOutboundItem item : items) {
            item.setOutbound(saved);
            materialOutboundItemRepository.save(item);
        }
        calculateTotal(saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public MaterialOutbound update(String id, MaterialOutbound materialOutbound) {
        MaterialOutbound existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("出库记录不存在: " + id));

        if (existing.getStatus() != OutboundStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核状态的出库单可以修改");
        }

        existing.setWarehouseId(materialOutbound.getWarehouseId());
        existing.setWarehouseName(materialOutbound.getWarehouseName());
        existing.setTargetWarehouseId(materialOutbound.getTargetWarehouseId());
        existing.setTargetWarehouseName(materialOutbound.getTargetWarehouseName());
        existing.setTargetDeptId(materialOutbound.getTargetDeptId());
        existing.setTargetDeptName(materialOutbound.getTargetDeptName());
        existing.setOutboundType(materialOutbound.getOutboundType());
        existing.setRemark(materialOutbound.getRemark());

        return materialOutboundRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        MaterialOutbound outbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("出库记录不存在: " + id));

        if (outbound.getStatus() == OutboundStatus.CONFIRMED) {
            throw new IllegalArgumentException("已确认出库的记录不能删除");
        }

        outbound.setDeleted(true);
        materialOutboundRepository.save(outbound);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    @Transactional
    public MaterialOutbound submit(String id) {
        MaterialOutbound outbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("出库记录不存在: " + id));

        outbound.setStatus(OutboundStatus.PENDING);
        outbound.setApplyTime(LocalDateTime.now());
        return materialOutboundRepository.save(outbound);
    }

    @Override
    @Transactional
    public MaterialOutbound audit(String id, String auditorId, String auditorName, boolean approved, String auditRemark) {
        MaterialOutbound outbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("出库记录不存在: " + id));

        if (outbound.getStatus() != OutboundStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核状态的出库单可以审核");
        }

        outbound.setAuditorId(auditorId);
        outbound.setAuditorName(auditorName);
        outbound.setAuditTime(LocalDateTime.now());
        outbound.setAuditRemark(auditRemark);

        if (approved) {
            outbound.setStatus(OutboundStatus.AUDITED);
        } else {
            outbound.setStatus(OutboundStatus.REJECTED);
        }

        return materialOutboundRepository.save(outbound);
    }

    @Override
    @Transactional
    public MaterialOutbound issue(String id, String operatorId, String operatorName) {
        MaterialOutbound outbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("出库记录不存在: " + id));

        if (outbound.getStatus() != OutboundStatus.AUDITED) {
            throw new IllegalArgumentException("只有已审核状态的出库单可以发放");
        }

        // 出库操作：扣减库存
        List<MaterialOutboundItem> items = getItems(id);
        for (MaterialOutboundItem item : items) {
            if (item.getInventoryId() != null) {
                materialInventoryService.outboundStock(item.getInventoryId(), item.getQuantity());
            }
        }

        outbound.setOperatorId(operatorId);
        outbound.setOperatorName(operatorName);
        outbound.setOutboundTime(LocalDateTime.now());
        outbound.setStatus(OutboundStatus.ISSUED);

        return materialOutboundRepository.save(outbound);
    }

    @Override
    @Transactional
    public MaterialOutbound confirm(String id, String receiverId, String receiverName) {
        MaterialOutbound outbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("出库记录不存在: " + id));

        if (outbound.getStatus() != OutboundStatus.ISSUED) {
            throw new IllegalArgumentException("只有已发放状态的出库单可以确认");
        }

        outbound.setReceiverId(receiverId);
        outbound.setReceiverName(receiverName);
        outbound.setReceiveTime(LocalDateTime.now());
        outbound.setStatus(OutboundStatus.CONFIRMED);

        return materialOutboundRepository.save(outbound);
    }

    @Override
    @Transactional
    public MaterialOutbound cancel(String id) {
        MaterialOutbound outbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("出库记录不存在: " + id));

        if (outbound.getStatus() == OutboundStatus.CONFIRMED) {
            throw new IllegalArgumentException("已确认出库的记录不能取消");
        }

        // 如果已发放，需要回退库存
        if (outbound.getStatus() == OutboundStatus.ISSUED) {
            List<MaterialOutboundItem> items = getItems(id);
            for (MaterialOutboundItem item : items) {
                if (item.getInventoryId() != null) {
                    materialInventoryService.inboundStock(
                            item.getMaterialId(),
                            outbound.getWarehouseId(),
                            item.getBatchNo(),
                            item.getQuantity(),
                            item.getPurchasePrice(),
                            item.getRetailPrice(),
                            null,
                            null,
                            null
                    );
                }
            }
        }

        outbound.setStatus(OutboundStatus.CANCELLED);
        return materialOutboundRepository.save(outbound);
    }

    @Override
    public String generateOutboundNo() {
        String prefix = "OUT";
        String dateStr = LocalDate.now().toString().replace("-", "");
        long count = materialOutboundRepository.findByDeletedFalse(Pageable.unpaged()).getTotalElements() + 1;
        return prefix + dateStr + String.format("%04d", count);
    }

    @Override
    @Transactional
    public void calculateTotal(String id) {
        MaterialOutbound outbound = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("出库记录不存在: " + id));

        List<MaterialOutboundItem> items = getItems(id);
        BigDecimal totalQuantity = items.stream()
                .map(MaterialOutboundItem::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = items.stream()
                .map(MaterialOutboundItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        outbound.setTotalQuantity(totalQuantity);
        outbound.setTotalAmount(totalAmount);
        materialOutboundRepository.save(outbound);
    }

    @Override
    @Transactional
    public MaterialOutboundItem addItem(String outboundId, MaterialOutboundItem item) {
        MaterialOutbound outbound = findById(outboundId)
                .orElseThrow(() -> new IllegalArgumentException("出库记录不存在: " + outboundId));

        if (outbound.getStatus() != OutboundStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核状态的出库单可以添加明细");
        }

        item.setOutbound(outbound);
        item.setAmount(materialOutboundItemService.calculateAmount(item));
        MaterialOutboundItem saved = materialOutboundItemRepository.save(item);
        calculateTotal(outboundId);
        return saved;
    }

    @Override
    @Transactional
    public void deleteItem(String itemId) {
        MaterialOutboundItem item = materialOutboundItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("出库明细不存在: " + itemId));

        if (item.getOutbound().getStatus() != OutboundStatus.PENDING) {
            throw new IllegalArgumentException("只有待审核状态的出库单可以删除明细");
        }

        materialOutboundItemRepository.delete(item);
        calculateTotal(item.getOutbound().getId());
    }

    @Override
    public List<MaterialOutboundItem> getItems(String outboundId) {
        return materialOutboundItemRepository.findByOutboundId(outboundId);
    }

    @Override
    public boolean checkStockAvailable(String outboundId) {
        MaterialOutbound outbound = findById(outboundId)
                .orElseThrow(() -> new IllegalArgumentException("出库记录不存在: " + outboundId));

        List<MaterialOutboundItem> items = getItems(outboundId);
        for (MaterialOutboundItem item : items) {
            if (!materialInventoryService.checkStockAvailable(item.getMaterialId(), outbound.getWarehouseId(), item.getApplyQuantity())) {
                return false;
            }
        }
        return true;
    }
}