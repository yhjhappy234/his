package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.InboundDTO;
import com.yhj.his.module.inventory.dto.InboundItemDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.entity.Material;
import com.yhj.his.module.inventory.entity.MaterialInbound;
import com.yhj.his.module.inventory.entity.MaterialInboundItem;
import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.entity.Warehouse;
import com.yhj.his.module.inventory.enums.InboundStatus;
import com.yhj.his.module.inventory.enums.InboundType;
import com.yhj.his.module.inventory.repository.MaterialInboundItemRepository;
import com.yhj.his.module.inventory.repository.MaterialInboundRepository;
import com.yhj.his.module.inventory.repository.MaterialInventoryRepository;
import com.yhj.his.module.inventory.repository.MaterialRepository;
import com.yhj.his.module.inventory.repository.WarehouseRepository;
import com.yhj.his.module.inventory.vo.MaterialInboundItemVO;
import com.yhj.his.module.inventory.vo.MaterialInboundVO;
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
 * 入库管理服务实现
 */
@Service
@RequiredArgsConstructor
public class InboundServiceImpl implements InboundService {

    private final MaterialInboundRepository inboundRepository;
    private final MaterialInboundItemRepository itemRepository;
    private final MaterialRepository materialRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialInventoryRepository inventoryRepository;

    @Override
    @Transactional
    public MaterialInboundVO register(InboundDTO dto) {
        // 检查库房是否存在
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new BusinessException("库房不存在"));

        // 创建入库记录
        MaterialInbound inbound = new MaterialInbound();
        inbound.setInboundNo(SequenceGenerator.generate("RK"));
        inbound.setInboundType(InboundType.valueOf(dto.getInboundType()));
        inbound.setWarehouseId(dto.getWarehouseId());
        inbound.setWarehouseName(warehouse.getWarehouseName());
        inbound.setSupplierId(dto.getSupplierId());
        inbound.setSupplierName(dto.getSupplierName());
        inbound.setInboundDate(dto.getInboundDate());
        inbound.setApplicantId(dto.getApplicantId());
        inbound.setApplicantName(dto.getApplicantName());
        inbound.setApplyTime(LocalDateTime.now());
        inbound.setRemark(dto.getRemark());
        inbound.setStatus(InboundStatus.PENDING);

        // 创建入库明细
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<MaterialInboundItem> items = new ArrayList<>();

        for (InboundItemDTO itemDto : dto.getItems()) {
            Material material = materialRepository.findById(itemDto.getMaterialId())
                    .orElseThrow(() -> new BusinessException("物资不存在: " + itemDto.getMaterialId()));

            MaterialInboundItem item = new MaterialInboundItem();
            item.setInbound(inbound);
            item.setMaterialId(itemDto.getMaterialId());
            item.setMaterialCode(material.getMaterialCode());
            item.setMaterialName(material.getMaterialName());
            item.setMaterialSpec(material.getMaterialSpec());
            item.setMaterialUnit(material.getMaterialUnit());
            item.setBatchNo(itemDto.getBatchNo());
            item.setProductionDate(itemDto.getProductionDate());
            item.setExpiryDate(itemDto.getExpiryDate());
            item.setQuantity(itemDto.getQuantity());
            item.setPurchasePrice(itemDto.getPurchasePrice() != null ? itemDto.getPurchasePrice() : material.getPurchasePrice());
            item.setRetailPrice(itemDto.getRetailPrice() != null ? itemDto.getRetailPrice() : material.getRetailPrice());
            item.setLocation(itemDto.getLocation());
            item.setRemark(itemDto.getRemark());

            // 计算金额
            BigDecimal amount = item.getQuantity().multiply(item.getPurchasePrice() != null ? item.getPurchasePrice() : BigDecimal.ZERO);
            item.setAmount(amount);

            totalQuantity = totalQuantity.add(itemDto.getQuantity());
            totalAmount = totalAmount.add(amount);
            items.add(item);
        }

        inbound.setTotalQuantity(totalQuantity);
        inbound.setTotalAmount(totalAmount);
        inbound.setItems(items);

        inbound = inboundRepository.save(inbound);
        return toVO(inbound);
    }

    @Override
    @Transactional
    public MaterialInboundVO audit(AuditDTO dto) {
        MaterialInbound inbound = inboundRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("入库记录不存在"));

        if (inbound.getStatus() != InboundStatus.PENDING) {
            throw new BusinessException("入库单状态不正确，无法审核");
        }

        if ("PASS".equals(dto.getResult())) {
            inbound.setStatus(InboundStatus.AUDITED);
        } else if ("REJECT".equals(dto.getResult())) {
            inbound.setStatus(InboundStatus.REJECTED);
        }

        inbound.setAuditorId(dto.getAuditorId());
        inbound.setAuditorName(dto.getAuditorName());
        inbound.setAuditTime(LocalDateTime.now());
        inbound.setAuditRemark(dto.getRemark());

        inbound = inboundRepository.save(inbound);
        return toVO(inbound);
    }

    @Override
    @Transactional
    public MaterialInboundVO confirm(ConfirmDTO dto) {
        MaterialInbound inbound = inboundRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("入库记录不存在"));

        if (inbound.getStatus() != InboundStatus.AUDITED) {
            throw new BusinessException("入库单状态不正确，无法确认入库");
        }

        // 更新库存
        for (MaterialInboundItem item : inbound.getItems()) {
            MaterialInventory inventory = new MaterialInventory();
            inventory.setMaterialId(item.getMaterialId());
            inventory.setMaterialCode(item.getMaterialCode());
            inventory.setMaterialName(item.getMaterialName());
            inventory.setMaterialSpec(item.getMaterialSpec());
            inventory.setMaterialUnit(item.getMaterialUnit());
            inventory.setWarehouseId(inbound.getWarehouseId());
            inventory.setWarehouseName(inbound.getWarehouseName());
            inventory.setBatchNo(item.getBatchNo());
            inventory.setExpiryDate(item.getExpiryDate());
            inventory.setQuantity(item.getQuantity());
            inventory.setLockedQuantity(BigDecimal.ZERO);
            inventory.setAvailableQuantity(item.getQuantity());
            inventory.setLocation(item.getLocation());
            inventory.setPurchasePrice(item.getPurchasePrice());
            inventory.setRetailPrice(item.getRetailPrice());
            inventory.setSupplierId(inbound.getSupplierId());
            inventory.setSupplierName(inbound.getSupplierName());
            inventory.setInboundTime(LocalDateTime.now());
            inventory.setStatus(1);
            inventoryRepository.save(inventory);
        }

        inbound.setStatus(InboundStatus.CONFIRMED);
        inbound.setOperatorId(dto.getOperatorId());
        inbound.setOperatorName(dto.getOperatorName());
        inbound.setInboundTime(LocalDateTime.now());

        inbound = inboundRepository.save(inbound);
        return toVO(inbound);
    }

    @Override
    @Transactional
    public void cancel(String id) {
        MaterialInbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new BusinessException("入库记录不存在"));

        if (inbound.getStatus() != InboundStatus.PENDING) {
            throw new BusinessException("只有待审核状态的入库单可以取消");
        }

        inbound.setStatus(InboundStatus.CANCELLED);
        inboundRepository.save(inbound);
    }

    @Override
    public MaterialInboundVO getById(String id) {
        MaterialInbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new BusinessException("入库记录不存在"));
        return toVO(inbound);
    }

    @Override
    public MaterialInboundVO getByNo(String inboundNo) {
        MaterialInbound inbound = inboundRepository.findByInboundNo(inboundNo)
                .orElseThrow(() -> new BusinessException("入库记录不存在"));
        return toVO(inbound);
    }

    @Override
    public PageResult<MaterialInboundVO> list(Integer pageNum, Integer pageSize) {
        Page<MaterialInbound> page = inboundRepository.findByDeletedFalse(PageRequest.of(pageNum - 1, pageSize));
        List<MaterialInboundVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public PageResult<MaterialInboundVO> query(QueryDTO query) {
        Page<MaterialInbound> page = inboundRepository.search(
                query.getWarehouseId(),
                query.getStatus() != null ? InboundStatus.valueOf(query.getStatus()) : null,
                query.getType() != null ? InboundType.valueOf(query.getType()) : null,
                query.getStartDate(),
                query.getEndDate(),
                PageRequest.of(query.getPageNum() - 1, query.getPageSize())
        );
        List<MaterialInboundVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public List<MaterialInboundVO> listPending() {
        List<MaterialInbound> list = inboundRepository.findPendingInbounds();
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    private MaterialInboundVO toVO(MaterialInbound entity) {
        MaterialInboundVO vo = new MaterialInboundVO();
        vo.setId(entity.getId());
        vo.setInboundNo(entity.getInboundNo());
        vo.setInboundType(entity.getInboundType().name());
        vo.setInboundTypeName(entity.getInboundType().getName());
        vo.setWarehouseId(entity.getWarehouseId());
        vo.setWarehouseName(entity.getWarehouseName());
        vo.setSupplierId(entity.getSupplierId());
        vo.setSupplierName(entity.getSupplierName());
        vo.setInboundDate(entity.getInboundDate());
        vo.setInboundTime(entity.getInboundTime());
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
        vo.setRemark(entity.getRemark());
        vo.setStatus(entity.getStatus().name());
        vo.setStatusName(entity.getStatus().getName());
        vo.setCreateTime(entity.getCreateTime());

        // 转换明细
        List<MaterialInboundItemVO> items = entity.getItems().stream()
                .map(this::toItemVO).collect(Collectors.toList());
        vo.setItems(items);

        return vo;
    }

    private MaterialInboundItemVO toItemVO(MaterialInboundItem entity) {
        MaterialInboundItemVO vo = new MaterialInboundItemVO();
        vo.setId(entity.getId());
        vo.setMaterialId(entity.getMaterialId());
        vo.setMaterialCode(entity.getMaterialCode());
        vo.setMaterialName(entity.getMaterialName());
        vo.setMaterialSpec(entity.getMaterialSpec());
        vo.setMaterialUnit(entity.getMaterialUnit());
        vo.setBatchNo(entity.getBatchNo());
        vo.setProductionDate(entity.getProductionDate());
        vo.setExpiryDate(entity.getExpiryDate());
        vo.setQuantity(entity.getQuantity());
        vo.setPurchasePrice(entity.getPurchasePrice());
        vo.setRetailPrice(entity.getRetailPrice());
        vo.setAmount(entity.getAmount());
        vo.setLocation(entity.getLocation());
        vo.setRemark(entity.getRemark());
        return vo;
    }
}