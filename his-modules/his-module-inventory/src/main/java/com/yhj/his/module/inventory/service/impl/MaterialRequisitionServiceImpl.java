package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.entity.MaterialRequisition;
import com.yhj.his.module.inventory.entity.MaterialRequisitionItem;
import com.yhj.his.module.inventory.enums.RequisitionStatus;
import com.yhj.his.module.inventory.repository.MaterialRequisitionItemRepository;
import com.yhj.his.module.inventory.repository.MaterialRequisitionRepository;
import com.yhj.his.module.inventory.service.MaterialInventoryService;
import com.yhj.his.module.inventory.service.MaterialRequisitionItemService;
import com.yhj.his.module.inventory.service.MaterialRequisitionService;
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
 * 物资申领Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialRequisitionServiceImpl implements MaterialRequisitionService {

    private final MaterialRequisitionRepository materialRequisitionRepository;
    private final MaterialRequisitionItemRepository materialRequisitionItemRepository;
    private final MaterialRequisitionItemService materialRequisitionItemService;
    private final MaterialInventoryService materialInventoryService;

    @Override
    public Optional<MaterialRequisition> findById(String id) {
        return materialRequisitionRepository.findById(id);
    }

    @Override
    public Optional<MaterialRequisition> findByRequisitionNo(String requisitionNo) {
        return materialRequisitionRepository.findByRequisitionNo(requisitionNo);
    }

    @Override
    public List<MaterialRequisition> findAll() {
        return materialRequisitionRepository.findByDeletedFalse(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<MaterialRequisition> findAll(Pageable pageable) {
        return materialRequisitionRepository.findByDeletedFalse(pageable);
    }

    @Override
    public List<MaterialRequisition> findByStatus(RequisitionStatus status) {
        return materialRequisitionRepository.findByStatusAndDeletedFalse(status);
    }

    @Override
    public List<MaterialRequisition> findByWarehouseId(String warehouseId) {
        return materialRequisitionRepository.findByWarehouseIdAndDeletedFalse(warehouseId);
    }

    @Override
    public List<MaterialRequisition> findByDeptId(String deptId) {
        return materialRequisitionRepository.findByDeptIdAndDeletedFalse(deptId);
    }

    @Override
    public List<MaterialRequisition> findByRequisitionDate(LocalDate requisitionDate) {
        return materialRequisitionRepository.findByRequisitionDateAndDeletedFalse(requisitionDate);
    }

    @Override
    public Page<MaterialRequisition> findByStatus(RequisitionStatus status, Pageable pageable) {
        return materialRequisitionRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Page<MaterialRequisition> search(String warehouseId, String deptId, RequisitionStatus status,
                                            LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return materialRequisitionRepository.search(warehouseId, deptId, status, startDate, endDate, pageable);
    }

    @Override
    public List<MaterialRequisition> findPendingRequisitions() {
        return materialRequisitionRepository.findPendingRequisitions();
    }

    @Override
    public List<MaterialRequisition> findByDeptIdOrderByDateDesc(String deptId) {
        return materialRequisitionRepository.findByDeptIdOrderByDateDesc(deptId);
    }

    @Override
    @Transactional
    public MaterialRequisition create(MaterialRequisition materialRequisition) {
        materialRequisition.setRequisitionNo(generateRequisitionNo());
        materialRequisition.setStatus(RequisitionStatus.PENDING);
        materialRequisition.setRequisitionDate(LocalDate.now());
        materialRequisition.setApplyTime(LocalDateTime.now());
        return materialRequisitionRepository.save(materialRequisition);
    }

    @Override
    @Transactional
    public MaterialRequisition createWithItems(MaterialRequisition materialRequisition, List<MaterialRequisitionItem> items) {
        MaterialRequisition saved = create(materialRequisition);
        for (MaterialRequisitionItem item : items) {
            item.setRequisition(saved);
            materialRequisitionItemRepository.save(item);
        }
        calculateTotal(saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public MaterialRequisition update(String id, MaterialRequisition materialRequisition) {
        MaterialRequisition existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申领记录不存在: " + id));

        if (existing.getStatus() != RequisitionStatus.PENDING) {
            throw new IllegalArgumentException("只有待审批状态的申领单可以修改");
        }

        existing.setWarehouseId(materialRequisition.getWarehouseId());
        existing.setWarehouseName(materialRequisition.getWarehouseName());
        existing.setDeptId(materialRequisition.getDeptId());
        existing.setDeptName(materialRequisition.getDeptName());
        existing.setRemark(materialRequisition.getRemark());

        return materialRequisitionRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        MaterialRequisition requisition = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申领记录不存在: " + id));

        if (requisition.getStatus() == RequisitionStatus.RECEIVED) {
            throw new IllegalArgumentException("已接收的申领单不能删除");
        }

        requisition.setDeleted(true);
        materialRequisitionRepository.save(requisition);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    @Transactional
    public MaterialRequisition submit(String id) {
        MaterialRequisition requisition = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申领记录不存在: " + id));

        requisition.setStatus(RequisitionStatus.PENDING);
        requisition.setApplyTime(LocalDateTime.now());
        return materialRequisitionRepository.save(requisition);
    }

    @Override
    @Transactional
    public MaterialRequisition approve(String id, String approverId, String approverName, boolean approved, String approveRemark) {
        MaterialRequisition requisition = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申领记录不存在: " + id));

        if (requisition.getStatus() != RequisitionStatus.PENDING) {
            throw new IllegalArgumentException("只有待审批状态的申领单可以审批");
        }

        requisition.setApproverId(approverId);
        requisition.setApproverName(approverName);
        requisition.setApproveTime(LocalDateTime.now());
        requisition.setApproveRemark(approveRemark);

        if (approved) {
            requisition.setStatus(RequisitionStatus.APPROVED);
        } else {
            requisition.setStatus(RequisitionStatus.REJECTED);
        }

        return materialRequisitionRepository.save(requisition);
    }

    @Override
    @Transactional
    public MaterialRequisition issue(String id, String issuerId, String issuerName) {
        MaterialRequisition requisition = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申领记录不存在: " + id));

        if (requisition.getStatus() != RequisitionStatus.APPROVED) {
            throw new IllegalArgumentException("只有已审批状态的申领单可以发放");
        }

        // 发放操作：扣减库存
        List<MaterialRequisitionItem> items = getItems(id);
        for (MaterialRequisitionItem item : items) {
            if (item.getIssueQuantity() != null && item.getIssueQuantity().compareTo(BigDecimal.ZERO) > 0) {
                List<MaterialInventory> inventories = materialInventoryService
                        .findAvailableInventoryOrderByExpiry(item.getMaterialId(), requisition.getWarehouseId());
                BigDecimal remaining = item.getIssueQuantity();
                for (MaterialInventory inventory : inventories) {
                    if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
                    BigDecimal toIssue = inventory.getAvailableQuantity().min(remaining);
                    materialInventoryService.outboundStock(inventory.getId(), toIssue);
                    remaining = remaining.subtract(toIssue);
                }
            }
        }

        requisition.setIssuerId(issuerId);
        requisition.setIssuerName(issuerName);
        requisition.setIssueTime(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.ISSUED);

        return materialRequisitionRepository.save(requisition);
    }

    @Override
    @Transactional
    public MaterialRequisition receive(String id, String receiverId, String receiverName) {
        MaterialRequisition requisition = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申领记录不存在: " + id));

        if (requisition.getStatus() != RequisitionStatus.ISSUED) {
            throw new IllegalArgumentException("只有已发放状态的申领单可以接收");
        }

        requisition.setReceiverId(receiverId);
        requisition.setReceiverName(receiverName);
        requisition.setReceiveTime(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.RECEIVED);

        return materialRequisitionRepository.save(requisition);
    }

    @Override
    @Transactional
    public MaterialRequisition cancel(String id) {
        MaterialRequisition requisition = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申领记录不存在: " + id));

        if (requisition.getStatus() == RequisitionStatus.RECEIVED) {
            throw new IllegalArgumentException("已接收的申领单不能取消");
        }

        requisition.setStatus(RequisitionStatus.CANCELLED);
        return materialRequisitionRepository.save(requisition);
    }

    @Override
    public String generateRequisitionNo() {
        String prefix = "REQ";
        String dateStr = LocalDate.now().toString().replace("-", "");
        long count = materialRequisitionRepository.findByDeletedFalse(Pageable.unpaged()).getTotalElements() + 1;
        return prefix + dateStr + String.format("%04d", count);
    }

    @Override
    @Transactional
    public void calculateTotal(String id) {
        MaterialRequisition requisition = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申领记录不存在: " + id));

        List<MaterialRequisitionItem> items = getItems(id);
        BigDecimal totalQuantity = items.stream()
                .map(MaterialRequisitionItem::getApplyQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = items.stream()
                .map(MaterialRequisitionItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        requisition.setTotalQuantity(totalQuantity);
        requisition.setTotalAmount(totalAmount);
        materialRequisitionRepository.save(requisition);
    }

    @Override
    @Transactional
    public MaterialRequisitionItem addItem(String requisitionId, MaterialRequisitionItem item) {
        MaterialRequisition requisition = findById(requisitionId)
                .orElseThrow(() -> new IllegalArgumentException("申领记录不存在: " + requisitionId));

        if (requisition.getStatus() != RequisitionStatus.PENDING) {
            throw new IllegalArgumentException("只有待审批状态的申领单可以添加明细");
        }

        item.setRequisition(requisition);
        item.setAmount(materialRequisitionItemService.calculateAmount(item));
        MaterialRequisitionItem saved = materialRequisitionItemRepository.save(item);
        calculateTotal(requisitionId);
        return saved;
    }

    @Override
    @Transactional
    public void deleteItem(String itemId) {
        MaterialRequisitionItem item = materialRequisitionItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("申领明细不存在: " + itemId));

        if (item.getRequisition().getStatus() != RequisitionStatus.PENDING) {
            throw new IllegalArgumentException("只有待审批状态的申领单可以删除明细");
        }

        materialRequisitionItemRepository.delete(item);
        calculateTotal(item.getRequisition().getId());
    }

    @Override
    public List<MaterialRequisitionItem> getItems(String requisitionId) {
        return materialRequisitionItemRepository.findByRequisitionId(requisitionId);
    }

    @Override
    public boolean checkStockAvailable(String requisitionId) {
        MaterialRequisition requisition = findById(requisitionId)
                .orElseThrow(() -> new IllegalArgumentException("申领记录不存在: " + requisitionId));

        List<MaterialRequisitionItem> items = getItems(requisitionId);
        for (MaterialRequisitionItem item : items) {
            if (!materialInventoryService.checkStockAvailable(item.getMaterialId(), requisition.getWarehouseId(), item.getApplyQuantity())) {
                return false;
            }
        }
        return true;
    }
}