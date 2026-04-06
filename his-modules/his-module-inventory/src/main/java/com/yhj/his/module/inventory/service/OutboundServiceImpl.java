package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.OutboundDTO;
import com.yhj.his.module.inventory.dto.OutboundItemDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.entity.Material;
import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.entity.MaterialOutbound;
import com.yhj.his.module.inventory.entity.MaterialOutboundItem;
import com.yhj.his.module.inventory.entity.Warehouse;
import com.yhj.his.module.inventory.enums.OutboundStatus;
import com.yhj.his.module.inventory.enums.OutboundType;
import com.yhj.his.module.inventory.repository.MaterialInventoryRepository;
import com.yhj.his.module.inventory.repository.MaterialOutboundItemRepository;
import com.yhj.his.module.inventory.repository.MaterialOutboundRepository;
import com.yhj.his.module.inventory.repository.MaterialRepository;
import com.yhj.his.module.inventory.repository.WarehouseRepository;
import com.yhj.his.module.inventory.vo.MaterialOutboundItemVO;
import com.yhj.his.module.inventory.vo.MaterialOutboundVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 出库管理服务实现
 */
@Service
@RequiredArgsConstructor
public class OutboundServiceImpl implements OutboundService {

    private final MaterialOutboundRepository outboundRepository;
    private final MaterialOutboundItemRepository itemRepository;
    private final MaterialRepository materialRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialInventoryRepository inventoryRepository;
    private final SequenceGenerator sequenceGenerator;

    @Override
    @Transactional
    public MaterialOutboundVO apply(OutboundDTO dto) {
        // 检查库房是否存在
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new BusinessException("库房不存在"));

        // 检查库存是否足够
        for (OutboundItemDTO itemDto : dto.getItems()) {
            BigDecimal availableStock = inventoryRepository.sumQuantityByMaterialIdAndWarehouseId(
                    itemDto.getMaterialId(), dto.getWarehouseId());
            if (availableStock == null || availableStock.compareTo(itemDto.getApplyQuantity()) < 0) {
                throw new BusinessException("物资库存不足: " + itemDto.getMaterialId());
            }
        }

        // 创建出库记录
        MaterialOutbound outbound = new MaterialOutbound();
        outbound.setOutboundNo(sequenceGenerator.generate("CK"));
        outbound.setOutboundType(OutboundType.valueOf(dto.getOutboundType()));
        outbound.setWarehouseId(dto.getWarehouseId());
        outbound.setWarehouseName(warehouse.getWarehouseName());
        outbound.setTargetWarehouseId(dto.getTargetWarehouseId());
        outbound.setTargetDeptId(dto.getTargetDeptId());
        outbound.setTargetDeptName(dto.getTargetDeptName());
        outbound.setOutboundDate(dto.getOutboundDate());
        outbound.setApplicantId(dto.getApplicantId());
        outbound.setApplicantName(dto.getApplicantName());
        outbound.setApplyTime(LocalDateTime.now());
        outbound.setRemark(dto.getRemark());
        outbound.setStatus(OutboundStatus.PENDING);

        // 设置目标库房名称
        if (dto.getTargetWarehouseId() != null) {
            Warehouse targetWarehouse = warehouseRepository.findById(dto.getTargetWarehouseId()).orElse(null);
            if (targetWarehouse != null) {
                outbound.setTargetWarehouseName(targetWarehouse.getWarehouseName());
            }
        }

        // 创建出库明细
        BigDecimal totalQuantity = BigDecimal.ZERO;
        List<MaterialOutboundItem> items = new ArrayList<>();

        for (OutboundItemDTO itemDto : dto.getItems()) {
            Material material = materialRepository.findById(itemDto.getMaterialId())
                    .orElseThrow(() -> new BusinessException("物资不存在: " + itemDto.getMaterialId()));

            MaterialOutboundItem item = new MaterialOutboundItem();
            item.setOutbound(outbound);
            item.setMaterialId(itemDto.getMaterialId());
            item.setMaterialCode(material.getMaterialCode());
            item.setMaterialName(material.getMaterialName());
            item.setMaterialSpec(material.getMaterialSpec());
            item.setMaterialUnit(material.getMaterialUnit());
            item.setApplyQuantity(itemDto.getApplyQuantity());
            item.setPurchasePrice(material.getPurchasePrice());
            item.setRetailPrice(material.getRetailPrice());
            item.setRemark(itemDto.getRemark());

            totalQuantity = totalQuantity.add(itemDto.getApplyQuantity());
            items.add(item);
        }

        outbound.setTotalQuantity(totalQuantity);
        outbound.setItems(items);

        outbound = outboundRepository.save(outbound);
        return toVO(outbound);
    }

    @Override
    @Transactional
    public MaterialOutboundVO audit(AuditDTO dto) {
        MaterialOutbound outbound = outboundRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("出库记录不存在"));

        if (outbound.getStatus() != OutboundStatus.PENDING) {
            throw new BusinessException("出库单状态不正确，无法审核");
        }

        if ("PASS".equals(dto.getResult())) {
            outbound.setStatus(OutboundStatus.AUDITED);
        } else if ("REJECT".equals(dto.getResult())) {
            outbound.setStatus(OutboundStatus.REJECTED);
        }

        outbound.setAuditorId(dto.getAuditorId());
        outbound.setAuditorName(dto.getAuditorName());
        outbound.setAuditTime(LocalDateTime.now());
        outbound.setAuditRemark(dto.getRemark());

        outbound = outboundRepository.save(outbound);
        return toVO(outbound);
    }

    @Override
    @Transactional
    public MaterialOutboundVO confirm(ConfirmDTO dto) {
        MaterialOutbound outbound = outboundRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("出库记录不存在"));

        if (outbound.getStatus() != OutboundStatus.AUDITED) {
            throw new BusinessException("出库单状态不正确，无法确认出库");
        }

        // 按先进先出原则扣减库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (MaterialOutboundItem item : outbound.getItems()) {
            BigDecimal remaining = item.getApplyQuantity();
            BigDecimal itemAmount = BigDecimal.ZERO;

            // 查询可用库存（按效期排序）
            List<MaterialInventory> inventories = inventoryRepository.findAvailableInventoryOrderByExpiry(
                    item.getMaterialId(), outbound.getWarehouseId(), LocalDate.now());

            for (MaterialInventory inventory : inventories) {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }

                BigDecimal deduct = inventory.getAvailableQuantity().min(remaining);
                inventory.setQuantity(inventory.getQuantity().subtract(deduct));
                inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(deduct));
                inventoryRepository.save(inventory);

                remaining = remaining.subtract(deduct);
                itemAmount = itemAmount.add(deduct.multiply(inventory.getPurchasePrice() != null ?
                        inventory.getPurchasePrice() : BigDecimal.ZERO));

                if (item.getInventoryId() == null) {
                    item.setInventoryId(inventory.getId());
                    item.setBatchNo(inventory.getBatchNo());
                    item.setPurchasePrice(inventory.getPurchasePrice());
                    item.setRetailPrice(inventory.getRetailPrice());
                }
            }

            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                throw new BusinessException("物资库存不足: " + item.getMaterialName());
            }

            item.setQuantity(item.getApplyQuantity());
            item.setAmount(itemAmount);
            totalAmount = totalAmount.add(itemAmount);
        }

        outbound.setStatus(OutboundStatus.CONFIRMED);
        outbound.setOperatorId(dto.getOperatorId());
        outbound.setOperatorName(dto.getOperatorName());
        outbound.setOutboundTime(LocalDateTime.now());
        outbound.setTotalAmount(totalAmount);

        outbound = outboundRepository.save(outbound);
        return toVO(outbound);
    }

    @Override
    @Transactional
    public void cancel(String id) {
        MaterialOutbound outbound = outboundRepository.findById(id)
                .orElseThrow(() -> new BusinessException("出库记录不存在"));

        if (outbound.getStatus() != OutboundStatus.PENDING) {
            throw new BusinessException("只有待审核状态的出库单可以取消");
        }

        outbound.setStatus(OutboundStatus.CANCELLED);
        outboundRepository.save(outbound);
    }

    @Override
    public MaterialOutboundVO getById(String id) {
        MaterialOutbound outbound = outboundRepository.findById(id)
                .orElseThrow(() -> new BusinessException("出库记录不存在"));
        return toVO(outbound);
    }

    @Override
    public MaterialOutboundVO getByNo(String outboundNo) {
        MaterialOutbound outbound = outboundRepository.findByOutboundNo(outboundNo)
                .orElseThrow(() -> new BusinessException("出库记录不存在"));
        return toVO(outbound);
    }

    @Override
    public PageResult<MaterialOutboundVO> list(Integer pageNum, Integer pageSize) {
        Page<MaterialOutbound> page = outboundRepository.findByDeletedFalse(PageRequest.of(pageNum - 1, pageSize));
        List<MaterialOutboundVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public PageResult<MaterialOutboundVO> query(QueryDTO query) {
        Page<MaterialOutbound> page = outboundRepository.search(
                query.getWarehouseId(),
                query.getStatus() != null ? OutboundStatus.valueOf(query.getStatus()) : null,
                query.getType() != null ? OutboundType.valueOf(query.getType()) : null,
                query.getDeptId(),
                query.getStartDate(),
                query.getEndDate(),
                PageRequest.of(query.getPageNum() - 1, query.getPageSize())
        );
        List<MaterialOutboundVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public List<MaterialOutboundVO> listPending() {
        List<MaterialOutbound> list = outboundRepository.findPendingOutbounds();
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    private MaterialOutboundVO toVO(MaterialOutbound entity) {
        MaterialOutboundVO vo = new MaterialOutboundVO();
        vo.setId(entity.getId());
        vo.setOutboundNo(entity.getOutboundNo());
        vo.setOutboundType(entity.getOutboundType().name());
        vo.setOutboundTypeName(entity.getOutboundType().getName());
        vo.setWarehouseId(entity.getWarehouseId());
        vo.setWarehouseName(entity.getWarehouseName());
        vo.setTargetWarehouseId(entity.getTargetWarehouseId());
        vo.setTargetWarehouseName(entity.getTargetWarehouseName());
        vo.setTargetDeptId(entity.getTargetDeptId());
        vo.setTargetDeptName(entity.getTargetDeptName());
        vo.setOutboundDate(entity.getOutboundDate());
        vo.setOutboundTime(entity.getOutboundTime());
        vo.setTotalQuantity(entity.getTotalQuantity());
        vo.setTotalAmount(entity.getTotalAmount());
        vo.setApplicantId(entity.getApplicantId());
        vo.setApplicantName(entity.getApplicantName());
        vo.setApplyTime(entity.getApplyTime());
        vo.setAuditorId(entity.getAuditorId());
        vo.setAuditorName(entity.getAuditorName());
        vo.setAuditTime(entity.getAuditTime());
        vo.setAuditRemark(entity.getAuditRemark());
        vo.setOperatorId(entity.getOperatorId());
        vo.setOperatorName(entity.getOperatorName());
        vo.setReceiverId(entity.getReceiverId());
        vo.setReceiverName(entity.getReceiverName());
        vo.setReceiveTime(entity.getReceiveTime());
        vo.setRemark(entity.getRemark());
        vo.setStatus(entity.getStatus().name());
        vo.setStatusName(entity.getStatus().getName());
        vo.setCreateTime(entity.getCreateTime());

        // 转换明细
        List<MaterialOutboundItemVO> items = entity.getItems().stream()
                .map(this::toItemVO).collect(Collectors.toList());
        vo.setItems(items);

        return vo;
    }

    private MaterialOutboundItemVO toItemVO(MaterialOutboundItem entity) {
        MaterialOutboundItemVO vo = new MaterialOutboundItemVO();
        vo.setId(entity.getId());
        vo.setMaterialId(entity.getMaterialId());
        vo.setMaterialCode(entity.getMaterialCode());
        vo.setMaterialName(entity.getMaterialName());
        vo.setMaterialSpec(entity.getMaterialSpec());
        vo.setMaterialUnit(entity.getMaterialUnit());
        vo.setBatchNo(entity.getBatchNo());
        vo.setApplyQuantity(entity.getApplyQuantity());
        vo.setQuantity(entity.getQuantity());
        vo.setPurchasePrice(entity.getPurchasePrice());
        vo.setRetailPrice(entity.getRetailPrice());
        vo.setAmount(entity.getAmount());
        vo.setRemark(entity.getRemark());
        return vo;
    }
}