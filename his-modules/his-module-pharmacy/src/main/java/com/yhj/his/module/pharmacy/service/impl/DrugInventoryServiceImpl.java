package com.yhj.his.module.pharmacy.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.InventoryInDTO;
import com.yhj.his.module.pharmacy.dto.InventoryQueryDTO;
import com.yhj.his.module.pharmacy.entity.Drug;
import com.yhj.his.module.pharmacy.entity.DrugInventory;
import com.yhj.his.module.pharmacy.entity.InventoryTransaction;
import com.yhj.his.module.pharmacy.enums.InventoryOperationType;
import com.yhj.his.module.pharmacy.enums.InventoryStatus;
import com.yhj.his.module.pharmacy.repository.DrugInventoryRepository;
import com.yhj.his.module.pharmacy.repository.DrugRepository;
import com.yhj.his.module.pharmacy.repository.InventoryTransactionRepository;
import com.yhj.his.module.pharmacy.service.DrugInventoryService;
import com.yhj.his.module.pharmacy.vo.ExpiryAlertVO;
import com.yhj.his.module.pharmacy.vo.InventoryVO;
import com.yhj.his.module.pharmacy.vo.StockAlertVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 药品库存服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DrugInventoryServiceImpl implements DrugInventoryService {

    private final DrugInventoryRepository inventoryRepository;
    private final DrugRepository drugRepository;
    private final InventoryTransactionRepository transactionRepository;

    @Override
    @Transactional
    public Result<InventoryVO> inbound(InventoryInDTO dto) {
        Optional<Drug> drugOptional = drugRepository.findById(dto.getDrugId());
        if (!drugOptional.isPresent()) {
            return Result.error("药品不存在: " + dto.getDrugId());
        }
        Drug drug = drugOptional.get();

        DrugInventory inventory = new DrugInventory();
        inventory.setDrugId(dto.getDrugId());
        inventory.setDrugCode(drug.getDrugCode());
        inventory.setDrugName(drug.getDrugName());
        inventory.setDrugSpec(drug.getDrugSpec());
        inventory.setDrugUnit(drug.getDrugUnit());
        inventory.setPharmacyId(dto.getPharmacyId());
        inventory.setBatchNo(dto.getBatchNo());
        inventory.setProductionDate(dto.getProductionDate());
        inventory.setExpiryDate(dto.getExpiryDate());
        inventory.setQuantity(dto.getQuantity());
        inventory.setLockedQuantity(BigDecimal.ZERO);
        inventory.setAvailableQuantity(dto.getQuantity());
        inventory.setLocation(dto.getLocation());
        inventory.setPurchasePrice(dto.getPurchasePrice() != null ? dto.getPurchasePrice() : drug.getPurchasePrice());
        inventory.setRetailPrice(dto.getRetailPrice() != null ? dto.getRetailPrice() : drug.getRetailPrice());
        inventory.setSupplierId(dto.getSupplierId());
        inventory.setSupplierName(dto.getSupplierName());
        inventory.setStatus(InventoryStatus.NORMAL);

        DrugInventory saved = inventoryRepository.save(inventory);

        InventoryTransaction transaction = createTransaction(InventoryOperationType.INBOUND, saved, dto.getQuantity(), dto.getRelatedId(), dto.getRelatedNo(), dto.getRemark());
        transactionRepository.save(transaction);

        return Result.success(entityToVO(saved));
    }

    @Override
    @Transactional
    public Result<InventoryVO> outbound(String inventoryId, BigDecimal quantity, String reason, String operatorId) {
        Optional<DrugInventory> optional = inventoryRepository.findById(inventoryId);
        if (!optional.isPresent()) {
            return Result.error("库存不存在: " + inventoryId);
        }
        DrugInventory inventory = optional.get();
        if (inventory.getAvailableQuantity().compareTo(quantity) < 0) {
            return Result.error("可用库存不足");
        }

        BigDecimal beforeQuantity = inventory.getQuantity();
        inventory.setQuantity(inventory.getQuantity().subtract(quantity));
        inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(quantity));
        DrugInventory saved = inventoryRepository.save(inventory);

        InventoryTransaction transaction = createTransaction(InventoryOperationType.OUTBOUND, saved, quantity.negate(), null, null, reason);
        transaction.setOperatorId(operatorId);
        transaction.setQuantityBefore(beforeQuantity);
        transaction.setQuantityAfter(saved.getQuantity());
        transactionRepository.save(transaction);

        return Result.success(entityToVO(saved));
    }

    @Override
    public Result<InventoryVO> getInventoryById(String inventoryId) {
        Optional<DrugInventory> optional = inventoryRepository.findById(inventoryId);
        if (!optional.isPresent()) {
            return Result.error("库存不存在: " + inventoryId);
        }
        return Result.success(entityToVO(optional.get()));
    }

    @Override
    public Result<PageResult<InventoryVO>> queryInventory(InventoryQueryDTO query) {
        Pageable pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
        InventoryStatus status = query.getStatus() != null ? InventoryStatus.valueOf(query.getStatus()) : null;
        Page<DrugInventory> page = inventoryRepository.queryInventory(
                query.getPharmacyId(), query.getDrugId(), query.getKeyword(),
                query.getBatchNo(), status, pageable);
        List<InventoryVO> list = page.getContent().stream().map(this::entityToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize()));
    }

    @Override
    public Result<List<InventoryVO>> getDrugInventory(String drugId) {
        List<DrugInventory> list = inventoryRepository.findByDrugId(drugId);
        List<InventoryVO> vos = list.stream().map(this::entityToVO).collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<PageResult<InventoryVO>> getPharmacyInventory(String pharmacyId, String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<DrugInventory> page = inventoryRepository.findByPharmacyIdAndKeyword(pharmacyId, keyword, pageable);
        List<InventoryVO> list = page.getContent().stream().map(this::entityToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    @Transactional
    public Result<Void> lockInventory(String inventoryId, BigDecimal quantity) {
        Optional<DrugInventory> optional = inventoryRepository.findById(inventoryId);
        if (!optional.isPresent()) {
            return Result.error("库存不存在: " + inventoryId);
        }
        DrugInventory inventory = optional.get();
        if (inventory.getAvailableQuantity().compareTo(quantity) < 0) {
            return Result.error("可用库存不足");
        }
        inventory.setLockedQuantity(inventory.getLockedQuantity().add(quantity));
        inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(quantity));
        inventoryRepository.save(inventory);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> unlockInventory(String inventoryId, BigDecimal quantity) {
        Optional<DrugInventory> optional = inventoryRepository.findById(inventoryId);
        if (!optional.isPresent()) {
            return Result.error("库存不存在: " + inventoryId);
        }
        DrugInventory inventory = optional.get();
        if (inventory.getLockedQuantity().compareTo(quantity) < 0) {
            return Result.error("锁定数量不足");
        }
        inventory.setLockedQuantity(inventory.getLockedQuantity().subtract(quantity));
        inventory.setAvailableQuantity(inventory.getAvailableQuantity().add(quantity));
        inventoryRepository.save(inventory);
        return Result.successVoid();
    }

    @Override
    public Result<List<ExpiryAlertVO>> getExpiryAlerts(Integer alertDays) {
        LocalDate alertDate = LocalDate.now().plusDays(alertDays);
        List<DrugInventory> list = inventoryRepository.findByExpiryDateBefore(alertDate);
        List<ExpiryAlertVO> vos = list.stream().map(this::inventoryToExpiryAlert).collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<List<StockAlertVO>> getLowStockAlerts() {
        List<DrugInventory> list = inventoryRepository.findLowStockInventory();
        List<StockAlertVO> vos = list.stream()
                .filter(i -> i.getDrug() != null && i.getQuantity().compareTo(i.getDrug().getMinStock()) < 0)
                .map(this::inventoryToStockAlert)
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<List<StockAlertVO>> getOverStockAlerts() {
        List<DrugInventory> list = inventoryRepository.findOverStockInventory();
        List<StockAlertVO> vos = list.stream()
                .filter(i -> i.getDrug() != null && i.getQuantity().compareTo(i.getDrug().getMaxStock()) > 0)
                .map(this::inventoryToStockAlert)
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    @Transactional
    public Result<Void> updateInventoryStatus(String inventoryId, InventoryStatus status) {
        Optional<DrugInventory> optional = inventoryRepository.findById(inventoryId);
        if (!optional.isPresent()) {
            return Result.error("库存不存在: " + inventoryId);
        }
        DrugInventory inventory = optional.get();
        inventory.setStatus(status);
        inventoryRepository.save(inventory);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<InventoryVO> adjustInventory(String inventoryId, BigDecimal actualQuantity, String reason, String operatorId) {
        Optional<DrugInventory> optional = inventoryRepository.findById(inventoryId);
        if (!optional.isPresent()) {
            return Result.error("库存不存在: " + inventoryId);
        }
        DrugInventory inventory = optional.get();
        BigDecimal beforeQuantity = inventory.getQuantity();
        BigDecimal change = actualQuantity.subtract(beforeQuantity);

        inventory.setQuantity(actualQuantity);
        inventory.setAvailableQuantity(actualQuantity.subtract(inventory.getLockedQuantity()));
        DrugInventory saved = inventoryRepository.save(inventory);

        InventoryTransaction transaction = createTransaction(InventoryOperationType.ADJUSTMENT, saved, change, null, null, reason);
        transaction.setOperatorId(operatorId);
        transaction.setQuantityBefore(beforeQuantity);
        transaction.setQuantityAfter(actualQuantity);
        transactionRepository.save(transaction);

        return Result.success(entityToVO(saved));
    }

    private InventoryTransaction createTransaction(InventoryOperationType type, DrugInventory inventory, BigDecimal quantityChange, String relatedId, String relatedNo, String reason) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setTransactionNo(generateTransactionNo());
        transaction.setTransactionType(type);
        transaction.setDrugId(inventory.getDrugId());
        transaction.setDrugCode(inventory.getDrugCode());
        transaction.setDrugName(inventory.getDrugName());
        transaction.setDrugSpec(inventory.getDrugSpec());
        transaction.setDrugUnit(inventory.getDrugUnit());
        transaction.setPharmacyId(inventory.getPharmacyId());
        transaction.setBatchNo(inventory.getBatchNo());
        transaction.setExpiryDate(inventory.getExpiryDate());
        transaction.setQuantityChange(quantityChange);
        transaction.setQuantityBefore(inventory.getQuantity());
        transaction.setQuantityAfter(inventory.getQuantity().add(quantityChange));
        transaction.setRetailPrice(inventory.getRetailPrice());
        transaction.setPurchasePrice(inventory.getPurchasePrice());
        transaction.setAmount(quantityChange.multiply(inventory.getRetailPrice()));
        transaction.setRelatedId(relatedId);
        transaction.setRelatedNo(relatedNo);
        transaction.setReason(reason);
        transaction.setOperateTime(LocalDateTime.now());
        return transaction;
    }

    private String generateTransactionNo() {
        return "TR" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 4);
    }

    private InventoryVO entityToVO(DrugInventory entity) {
        InventoryVO vo = new InventoryVO();
        vo.setInventoryId(entity.getId());
        vo.setDrugId(entity.getDrugId());
        vo.setDrugCode(entity.getDrugCode());
        vo.setDrugName(entity.getDrugName());
        vo.setDrugSpec(entity.getDrugSpec());
        vo.setDrugUnit(entity.getDrugUnit());
        vo.setPharmacyId(entity.getPharmacyId());
        vo.setPharmacyName(entity.getPharmacyName());
        vo.setBatchNo(entity.getBatchNo());
        vo.setProductionDate(entity.getProductionDate());
        vo.setExpiryDate(entity.getExpiryDate());
        vo.setQuantity(entity.getQuantity());
        vo.setLockedQuantity(entity.getLockedQuantity());
        vo.setAvailableQuantity(entity.getAvailableQuantity());
        vo.setLocation(entity.getLocation());
        vo.setPurchasePrice(entity.getPurchasePrice());
        vo.setRetailPrice(entity.getRetailPrice());
        vo.setSupplierId(entity.getSupplierId());
        vo.setSupplierName(entity.getSupplierName());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());

        if (entity.getExpiryDate() != null) {
            vo.setDaysRemaining((int) ChronoUnit.DAYS.between(LocalDate.now(), entity.getExpiryDate()));
        }
        return vo;
    }

    private ExpiryAlertVO inventoryToExpiryAlert(DrugInventory inventory) {
        ExpiryAlertVO vo = new ExpiryAlertVO();
        vo.setInventoryId(inventory.getId());
        vo.setDrugId(inventory.getDrugId());
        vo.setDrugCode(inventory.getDrugCode());
        vo.setDrugName(inventory.getDrugName());
        vo.setDrugSpec(inventory.getDrugSpec());
        vo.setBatchNo(inventory.getBatchNo());
        vo.setExpiryDate(inventory.getExpiryDate());
        vo.setQuantity(inventory.getQuantity());
        vo.setPharmacyId(inventory.getPharmacyId());
        vo.setPharmacyName(inventory.getPharmacyName());
        if (inventory.getExpiryDate() != null) {
            int daysRemaining = (int) ChronoUnit.DAYS.between(LocalDate.now(), inventory.getExpiryDate());
            vo.setDaysRemaining(daysRemaining);
            if (daysRemaining <= 30) {
                vo.setAlertLevel("紧急");
            } else if (daysRemaining <= 90) {
                vo.setAlertLevel("预警");
            } else {
                vo.setAlertLevel("关注");
            }
        }
        return vo;
    }

    private StockAlertVO inventoryToStockAlert(DrugInventory inventory) {
        StockAlertVO vo = new StockAlertVO();
        vo.setDrugId(inventory.getDrugId());
        vo.setDrugCode(inventory.getDrugCode());
        vo.setDrugName(inventory.getDrugName());
        vo.setDrugSpec(inventory.getDrugSpec());
        vo.setDrugUnit(inventory.getDrugUnit());
        vo.setQuantity(inventory.getQuantity());
        vo.setPharmacyId(inventory.getPharmacyId());
        vo.setPharmacyName(inventory.getPharmacyName());
        return vo;
    }
}