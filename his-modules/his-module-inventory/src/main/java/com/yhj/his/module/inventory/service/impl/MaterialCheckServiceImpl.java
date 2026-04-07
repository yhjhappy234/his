package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.module.inventory.entity.MaterialCheck;
import com.yhj.his.module.inventory.entity.MaterialCheckItem;
import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.enums.CheckStatus;
import com.yhj.his.module.inventory.enums.CheckType;
import com.yhj.his.module.inventory.repository.MaterialCheckItemRepository;
import com.yhj.his.module.inventory.repository.MaterialCheckRepository;
import com.yhj.his.module.inventory.service.MaterialCheckItemService;
import com.yhj.his.module.inventory.service.MaterialCheckService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 库存盘点Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialCheckServiceImpl implements MaterialCheckService {

    private final MaterialCheckRepository materialCheckRepository;
    private final MaterialCheckItemRepository materialCheckItemRepository;
    private final MaterialCheckItemService materialCheckItemService;
    private final MaterialInventoryService materialInventoryService;

    @Override
    public Optional<MaterialCheck> findById(String id) {
        return materialCheckRepository.findById(id);
    }

    @Override
    public Optional<MaterialCheck> findByCheckNo(String checkNo) {
        return materialCheckRepository.findByCheckNo(checkNo);
    }

    @Override
    public List<MaterialCheck> findAll() {
        return materialCheckRepository.findByDeletedFalse(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<MaterialCheck> findAll(Pageable pageable) {
        return materialCheckRepository.findByDeletedFalse(pageable);
    }

    @Override
    public List<MaterialCheck> findByStatus(CheckStatus status) {
        return materialCheckRepository.findByStatusAndDeletedFalse(status);
    }

    @Override
    public List<MaterialCheck> findByCheckType(CheckType checkType) {
        return materialCheckRepository.findByCheckTypeAndDeletedFalse(checkType);
    }

    @Override
    public List<MaterialCheck> findByWarehouseId(String warehouseId) {
        return materialCheckRepository.findByWarehouseIdAndDeletedFalse(warehouseId);
    }

    @Override
    public List<MaterialCheck> findByCheckDate(LocalDate checkDate) {
        return materialCheckRepository.findByCheckDateAndDeletedFalse(checkDate);
    }

    @Override
    public Page<MaterialCheck> findByStatus(CheckStatus status, Pageable pageable) {
        return materialCheckRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Page<MaterialCheck> search(String warehouseId, CheckStatus status, CheckType checkType,
                                      LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return materialCheckRepository.search(warehouseId, status, checkType, startDate, endDate, pageable);
    }

    @Override
    public List<MaterialCheck> findInProgressChecks() {
        return materialCheckRepository.findInProgressChecks();
    }

    @Override
    public List<MaterialCheck> findCompletedChecks() {
        return materialCheckRepository.findCompletedChecks();
    }

    @Override
    @Transactional
    public MaterialCheck create(MaterialCheck materialCheck) {
        materialCheck.setCheckNo(generateCheckNo());
        materialCheck.setStatus(CheckStatus.PENDING);
        materialCheck.setCheckDate(LocalDate.now());
        return materialCheckRepository.save(materialCheck);
    }

    @Override
    @Transactional
    public MaterialCheck createWithItems(MaterialCheck materialCheck, List<MaterialCheckItem> items) {
        MaterialCheck saved = create(materialCheck);
        for (MaterialCheckItem item : items) {
            item.setCheck(saved);
            materialCheckItemRepository.save(item);
        }
        saved.setTotalCount(items.size());
        return materialCheckRepository.save(saved);
    }

    @Override
    @Transactional
    public MaterialCheck update(String id, MaterialCheck materialCheck) {
        MaterialCheck existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点记录不存在: " + id));

        if (existing.getStatus() != CheckStatus.PENDING) {
            throw new IllegalArgumentException("只有待盘点状态的盘点单可以修改");
        }

        existing.setWarehouseId(materialCheck.getWarehouseId());
        existing.setWarehouseName(materialCheck.getWarehouseName());
        existing.setCheckType(materialCheck.getCheckType());
        existing.setRemark(materialCheck.getRemark());

        return materialCheckRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        MaterialCheck check = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点记录不存在: " + id));

        if (check.getStatus() == CheckStatus.ADJUSTED) {
            throw new IllegalArgumentException("已调整的盘点记录不能删除");
        }

        check.setDeleted(true);
        materialCheckRepository.save(check);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    @Transactional
    public MaterialCheck startCheck(String id, String checkerId, String checkerName) {
        MaterialCheck check = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点记录不存在: " + id));

        if (check.getStatus() != CheckStatus.PENDING) {
            throw new IllegalArgumentException("只有待盘点状态的盘点单可以开始");
        }

        // 根据库房初始化盘点明细（如果没有明细）
        if (getItems(id).isEmpty()) {
            List<MaterialCheckItem> items = initializeItemsByWarehouse(check.getWarehouseId());
            for (MaterialCheckItem item : items) {
                item.setCheck(check);
                materialCheckItemRepository.save(item);
            }
            check.setTotalCount(items.size());
        }

        check.setCheckerId(checkerId);
        check.setCheckerName(checkerName);
        check.setStartTime(LocalDateTime.now());
        check.setStatus(CheckStatus.IN_PROGRESS);

        return materialCheckRepository.save(check);
    }

    @Override
    @Transactional
    public MaterialCheckItem inputCheckData(String itemId, BigDecimal actualQuantity, String remark) {
        MaterialCheckItem item = materialCheckItemService.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("盘点明细不存在: " + itemId));

        if (item.getCheck().getStatus() != CheckStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("只有盘点中状态可以输入盘点数据");
        }

        item.setActualQuantity(actualQuantity);
        item.setRemark(remark);
        materialCheckItemService.calculateDiff(item);

        return materialCheckItemRepository.save(item);
    }

    @Override
    @Transactional
    public MaterialCheck completeCheck(String id) {
        MaterialCheck check = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点记录不存在: " + id));

        if (check.getStatus() != CheckStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("只有盘点中状态的盘点单可以完成");
        }

        check.setEndTime(LocalDateTime.now());
        check.setStatus(CheckStatus.COMPLETED);
        calculateDiff(id);

        return materialCheckRepository.save(check);
    }

    @Override
    @Transactional
    public MaterialCheck adjust(String id, String adjusterId, String adjusterName) {
        MaterialCheck check = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点记录不存在: " + id));

        if (check.getStatus() != CheckStatus.COMPLETED) {
            throw new IllegalArgumentException("只有已完成状态的盘点单可以调整");
        }

        // 调整库存
        List<MaterialCheckItem> diffItems = getUnadjustedDiffItems(id);
        for (MaterialCheckItem item : diffItems) {
            materialCheckItemService.adjust(item.getId());
            if (item.getInventoryId() != null) {
                materialInventoryService.adjustStock(item.getInventoryId(), item.getActualQuantity());
            }
        }

        check.setAdjusterId(adjusterId);
        check.setAdjusterName(adjusterName);
        check.setAdjustTime(LocalDateTime.now());
        check.setStatus(CheckStatus.ADJUSTED);

        return materialCheckRepository.save(check);
    }

    @Override
    @Transactional
    public MaterialCheck cancel(String id) {
        MaterialCheck check = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点记录不存在: " + id));

        if (check.getStatus() == CheckStatus.ADJUSTED) {
            throw new IllegalArgumentException("已调整的盘点记录不能取消");
        }

        check.setStatus(CheckStatus.CANCELLED);
        return materialCheckRepository.save(check);
    }

    @Override
    public String generateCheckNo() {
        String prefix = "CHK";
        String dateStr = LocalDate.now().toString().replace("-", "");
        long count = materialCheckRepository.findByDeletedFalse(Pageable.unpaged()).getTotalElements() + 1;
        return prefix + dateStr + String.format("%04d", count);
    }

    @Override
    @Transactional
    public void calculateDiff(String id) {
        MaterialCheck check = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点记录不存在: " + id));

        List<MaterialCheckItem> items = getItems(id);
        int profitCount = 0;
        int lossCount = 0;
        BigDecimal profitAmount = BigDecimal.ZERO;
        BigDecimal lossAmount = BigDecimal.ZERO;

        for (MaterialCheckItem item : items) {
            materialCheckItemService.calculateDiff(item);
            if ("PROFIT".equals(item.getDiffType())) {
                profitCount++;
                profitAmount = profitAmount.add(item.getDiffAmount() != null ? item.getDiffAmount() : BigDecimal.ZERO);
            } else if ("LOSS".equals(item.getDiffType())) {
                lossCount++;
                lossAmount = lossAmount.add(item.getDiffAmount() != null ? item.getDiffAmount() : BigDecimal.ZERO);
            }
            materialCheckItemRepository.save(item);
        }

        check.setProfitCount(profitCount);
        check.setLossCount(lossCount);
        check.setProfitAmount(profitAmount);
        check.setLossAmount(lossAmount);
        materialCheckRepository.save(check);
    }

    @Override
    @Transactional
    public MaterialCheckItem addItem(String checkId, MaterialCheckItem item) {
        MaterialCheck check = findById(checkId)
                .orElseThrow(() -> new IllegalArgumentException("盘点记录不存在: " + checkId));

        if (check.getStatus() != CheckStatus.PENDING && check.getStatus() != CheckStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("只有待盘点或盘点中状态的盘点单可以添加明细");
        }

        item.setCheck(check);
        materialCheckItemService.calculateDiff(item);
        MaterialCheckItem saved = materialCheckItemRepository.save(item);
        check.setTotalCount(check.getTotalCount() + 1);
        materialCheckRepository.save(check);
        return saved;
    }

    @Override
    @Transactional
    public void deleteItem(String itemId) {
        MaterialCheckItem item = materialCheckItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("盘点明细不存在: " + itemId));

        if (item.getCheck().getStatus() != CheckStatus.PENDING) {
            throw new IllegalArgumentException("只有待盘点状态的盘点单可以删除明细");
        }

        materialCheckItemRepository.delete(item);
        item.getCheck().setTotalCount(item.getCheck().getTotalCount() - 1);
        materialCheckRepository.save(item.getCheck());
    }

    @Override
    public List<MaterialCheckItem> getItems(String checkId) {
        return materialCheckItemRepository.findByCheckId(checkId);
    }

    @Override
    public List<MaterialCheckItem> getDiffItems(String checkId) {
        return materialCheckItemRepository.findDiffItems(checkId);
    }

    @Override
    public List<MaterialCheckItem> getUnadjustedDiffItems(String checkId) {
        return materialCheckItemRepository.findUnadjustedDiffItems(checkId);
    }

    @Override
    public List<MaterialCheckItem> initializeItemsByWarehouse(String warehouseId) {
        List<MaterialInventory> inventories = materialInventoryService.findByWarehouseId(warehouseId);
        List<MaterialCheckItem> items = new ArrayList<>();

        for (MaterialInventory inventory : inventories) {
            MaterialCheckItem item = new MaterialCheckItem();
            item.setMaterialId(inventory.getMaterialId());
            item.setMaterialCode(inventory.getMaterialCode());
            item.setMaterialName(inventory.getMaterialName());
            item.setMaterialSpec(inventory.getMaterialSpec());
            item.setMaterialUnit(inventory.getMaterialUnit());
            item.setBatchNo(inventory.getBatchNo());
            item.setInventoryId(inventory.getId());
            item.setBookQuantity(inventory.getQuantity());
            item.setPurchasePrice(inventory.getPurchasePrice());
            item.setRetailPrice(inventory.getRetailPrice());
            item.setAdjusted(false);
            items.add(item);
        }

        return items;
    }
}