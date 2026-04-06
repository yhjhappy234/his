package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.inventory.dto.CheckAdjustDTO;
import com.yhj.his.module.inventory.dto.CheckInputDTO;
import com.yhj.his.module.inventory.dto.CheckItemDTO;
import com.yhj.his.module.inventory.dto.CheckPlanDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.entity.MaterialCheck;
import com.yhj.his.module.inventory.entity.MaterialCheckItem;
import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.entity.Warehouse;
import com.yhj.his.module.inventory.enums.CheckStatus;
import com.yhj.his.module.inventory.enums.CheckType;
import com.yhj.his.module.inventory.enums.InboundType;
import com.yhj.his.module.inventory.enums.OutboundType;
import com.yhj.his.module.inventory.repository.MaterialCheckItemRepository;
import com.yhj.his.module.inventory.repository.MaterialCheckRepository;
import com.yhj.his.module.inventory.repository.MaterialInventoryRepository;
import com.yhj.his.module.inventory.repository.WarehouseRepository;
import com.yhj.his.module.inventory.vo.MaterialCheckItemVO;
import com.yhj.his.module.inventory.vo.MaterialCheckVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存盘点服务实现
 */
@Service
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {

    private final MaterialCheckRepository checkRepository;
    private final MaterialCheckItemRepository checkItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialInventoryRepository inventoryRepository;
    private final InboundService inboundService;
    private final OutboundService outboundService;
    private final SequenceGenerator sequenceGenerator;

    @Override
    @Transactional
    public MaterialCheckVO createPlan(CheckPlanDTO dto) {
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new BusinessException("库房不存在"));

        MaterialCheck check = new MaterialCheck();
        check.setCheckNo(sequenceGenerator.generate("PD"));
        check.setCheckType(CheckType.valueOf(dto.getCheckType()));
        check.setWarehouseId(dto.getWarehouseId());
        check.setWarehouseName(warehouse.getWarehouseName());
        check.setCheckDate(dto.getCheckDate() != null ? dto.getCheckDate() : java.time.LocalDate.now());
        check.setCheckerId(dto.getCheckerId());
        check.setCheckerName(dto.getCheckerName());
        check.setRemark(dto.getRemark());
        check.setStatus(CheckStatus.PENDING);

        // 生成盘点明细（从库存中获取）
        List<MaterialInventory> inventories = inventoryRepository.findByWarehouseIdAndDeletedFalse(dto.getWarehouseId());
        List<MaterialCheckItem> items = new java.util.ArrayList<>();

        for (MaterialInventory inventory : inventories) {
            if (inventory.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                MaterialCheckItem item = new MaterialCheckItem();
                item.setCheck(check);
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
        }

        check.setTotalCount(items.size());
        check.setItems(items);

        check = checkRepository.save(check);
        return toVO(check);
    }

    @Override
    @Transactional
    public MaterialCheckVO start(String checkId) {
        MaterialCheck check = checkRepository.findById(checkId)
                .orElseThrow(() -> new BusinessException("盘点记录不存在"));

        if (check.getStatus() != CheckStatus.PENDING) {
            throw new BusinessException("盘点单状态不正确，无法开始盘点");
        }

        check.setStatus(CheckStatus.IN_PROGRESS);
        check.setStartTime(LocalDateTime.now());

        check = checkRepository.save(check);
        return toVO(check);
    }

    @Override
    @Transactional
    public MaterialCheckVO input(CheckInputDTO dto) {
        MaterialCheck check = checkRepository.findById(dto.getCheckId())
                .orElseThrow(() -> new BusinessException("盘点记录不存在"));

        if (check.getStatus() != CheckStatus.IN_PROGRESS) {
            throw new BusinessException("盘点单状态不正确，无法录入");
        }

        // 更新盘点明细
        for (CheckItemDTO itemDto : dto.getItems()) {
            MaterialCheckItem item = checkItemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new BusinessException("盘点明细不存在"));

            item.setActualQuantity(itemDto.getActualQuantity());
            item.setRemark(itemDto.getRemark());

            // 计算差异
            BigDecimal diff = itemDto.getActualQuantity().subtract(item.getBookQuantity());
            item.setDiffQuantity(diff);
            item.setDiffAmount(diff.multiply(item.getPurchasePrice() != null ?
                    item.getPurchasePrice() : BigDecimal.ZERO));

            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                item.setDiffType("PROFIT");
            } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                item.setDiffType("LOSS");
            } else {
                item.setDiffType("NONE");
            }

            checkItemRepository.save(item);
        }

        return toVO(check);
    }

    @Override
    @Transactional
    public MaterialCheckVO complete(String checkId) {
        MaterialCheck check = checkRepository.findById(checkId)
                .orElseThrow(() -> new BusinessException("盘点记录不存在"));

        if (check.getStatus() != CheckStatus.IN_PROGRESS) {
            throw new BusinessException("盘点单状态不正确，无法完成盘点");
        }

        // 统计盘盈盘亏
        List<MaterialCheckItem> items = checkItemRepository.findByCheckId(checkId);
        int profitCount = 0;
        int lossCount = 0;
        BigDecimal profitAmount = BigDecimal.ZERO;
        BigDecimal lossAmount = BigDecimal.ZERO;

        for (MaterialCheckItem item : items) {
            if ("PROFIT".equals(item.getDiffType())) {
                profitCount++;
                profitAmount = profitAmount.add(item.getDiffAmount() != null ?
                        item.getDiffAmount().abs() : BigDecimal.ZERO);
            } else if ("LOSS".equals(item.getDiffType())) {
                lossCount++;
                lossAmount = lossAmount.add(item.getDiffAmount() != null ?
                        item.getDiffAmount().abs() : BigDecimal.ZERO);
            }
        }

        check.setProfitCount(profitCount);
        check.setLossCount(lossCount);
        check.setProfitAmount(profitAmount);
        check.setLossAmount(lossAmount);
        check.setStatus(CheckStatus.COMPLETED);
        check.setEndTime(LocalDateTime.now());

        check = checkRepository.save(check);
        return toVO(check);
    }

    @Override
    @Transactional
    public MaterialCheckVO adjust(CheckAdjustDTO dto) {
        MaterialCheck check = checkRepository.findById(dto.getCheckId())
                .orElseThrow(() -> new BusinessException("盘点记录不存在"));

        if (check.getStatus() != CheckStatus.COMPLETED) {
            throw new BusinessException("盘点单状态不正确，无法调整");
        }

        // 获取有差异且未调整的明细
        List<MaterialCheckItem> diffItems;
        if (dto.getItemIds() != null && !dto.getItemIds().isEmpty()) {
            diffItems = checkItemRepository.findAllById(dto.getItemIds());
        } else {
            diffItems = checkItemRepository.findUnadjustedDiffItems(dto.getCheckId());
        }

        // 处理差异
        for (MaterialCheckItem item : diffItems) {
            if (item.getAdjusted()) {
                continue;
            }

            MaterialInventory inventory = inventoryRepository.findById(item.getInventoryId()).orElse(null);

            if ("PROFIT".equals(item.getDiffType())) {
                // 盘盈 - 增加库存
                if (inventory != null) {
                    inventory.setQuantity(inventory.getQuantity().add(item.getDiffQuantity().abs()));
                    inventory.setAvailableQuantity(inventory.getAvailableQuantity().add(item.getDiffQuantity().abs()));
                    inventoryRepository.save(inventory);
                }
            } else if ("LOSS".equals(item.getDiffType())) {
                // 盘亏 - 减少库存
                if (inventory != null) {
                    inventory.setQuantity(inventory.getQuantity().subtract(item.getDiffQuantity().abs()));
                    inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(item.getDiffQuantity().abs()));
                    inventoryRepository.save(inventory);
                }
            }

            item.setAdjusted(true);
            checkItemRepository.save(item);
        }

        check.setStatus(CheckStatus.ADJUSTED);
        check.setAdjusterId(dto.getAdjusterId());
        check.setAdjusterName(dto.getAdjusterName());
        check.setAdjustTime(LocalDateTime.now());

        check = checkRepository.save(check);
        return toVO(check);
    }

    @Override
    @Transactional
    public void cancel(String checkId) {
        MaterialCheck check = checkRepository.findById(checkId)
                .orElseThrow(() -> new BusinessException("盘点记录不存在"));

        if (check.getStatus() != CheckStatus.PENDING) {
            throw new BusinessException("只有待盘点状态可以取消");
        }

        check.setStatus(CheckStatus.CANCELLED);
        checkRepository.save(check);
    }

    @Override
    public MaterialCheckVO getById(String id) {
        MaterialCheck check = checkRepository.findById(id)
                .orElseThrow(() -> new BusinessException("盘点记录不存在"));
        return toVO(check);
    }

    @Override
    public MaterialCheckVO getByNo(String checkNo) {
        MaterialCheck check = checkRepository.findByCheckNo(checkNo)
                .orElseThrow(() -> new BusinessException("盘点记录不存在"));
        return toVO(check);
    }

    @Override
    public PageResult<MaterialCheckVO> list(Integer pageNum, Integer pageSize) {
        Page<MaterialCheck> page = checkRepository.findByDeletedFalse(PageRequest.of(pageNum - 1, pageSize));
        List<MaterialCheckVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public PageResult<MaterialCheckVO> query(QueryDTO query) {
        Page<MaterialCheck> page = checkRepository.search(
                query.getWarehouseId(),
                query.getStatus() != null ? CheckStatus.valueOf(query.getStatus()) : null,
                query.getType() != null ? CheckType.valueOf(query.getType()) : null,
                query.getStartDate(),
                query.getEndDate(),
                PageRequest.of(query.getPageNum() - 1, query.getPageSize())
        );
        List<MaterialCheckVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public List<MaterialCheckVO> listInProgress() {
        List<MaterialCheck> list = checkRepository.findInProgressChecks();
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    private MaterialCheckVO toVO(MaterialCheck entity) {
        MaterialCheckVO vo = new MaterialCheckVO();
        vo.setId(entity.getId());
        vo.setCheckNo(entity.getCheckNo());
        vo.setCheckType(entity.getCheckType().name());
        vo.setCheckTypeName(entity.getCheckType().getName());
        vo.setWarehouseId(entity.getWarehouseId());
        vo.setWarehouseName(entity.getWarehouseName());
        vo.setCheckDate(entity.getCheckDate());
        vo.setStartTime(entity.getStartTime());
        vo.setEndTime(entity.getEndTime());
        vo.setCheckerId(entity.getCheckerId());
        vo.setCheckerName(entity.getCheckerName());
        vo.setTotalCount(entity.getTotalCount());
        vo.setProfitCount(entity.getProfitCount());
        vo.setLossCount(entity.getLossCount());
        vo.setProfitAmount(entity.getProfitAmount());
        vo.setLossAmount(entity.getLossAmount());
        vo.setAdjusterId(entity.getAdjusterId());
        vo.setAdjusterName(entity.getAdjusterName());
        vo.setAdjustTime(entity.getAdjustTime());
        vo.setRemark(entity.getRemark());
        vo.setStatus(entity.getStatus().name());
        vo.setStatusName(entity.getStatus().getName());
        vo.setCreateTime(entity.getCreateTime());

        // 转换明细
        List<MaterialCheckItemVO> items = checkItemRepository.findByCheckId(entity.getId())
                .stream().map(this::toItemVO).collect(Collectors.toList());
        vo.setItems(items);

        return vo;
    }

    private MaterialCheckItemVO toItemVO(MaterialCheckItem entity) {
        MaterialCheckItemVO vo = new MaterialCheckItemVO();
        vo.setId(entity.getId());
        vo.setMaterialId(entity.getMaterialId());
        vo.setMaterialCode(entity.getMaterialCode());
        vo.setMaterialName(entity.getMaterialName());
        vo.setMaterialSpec(entity.getMaterialSpec());
        vo.setMaterialUnit(entity.getMaterialUnit());
        vo.setBatchNo(entity.getBatchNo());
        vo.setBookQuantity(entity.getBookQuantity());
        vo.setActualQuantity(entity.getActualQuantity());
        vo.setDiffQuantity(entity.getDiffQuantity());
        vo.setPurchasePrice(entity.getPurchasePrice());
        vo.setRetailPrice(entity.getRetailPrice());
        vo.setDiffAmount(entity.getDiffAmount());
        vo.setDiffType(entity.getDiffType());
        vo.setAdjusted(entity.getAdjusted());
        vo.setRemark(entity.getRemark());
        return vo;
    }
}