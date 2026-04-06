package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.dto.RequisitionDTO;
import com.yhj.his.module.inventory.dto.RequisitionItemDTO;
import com.yhj.his.module.inventory.entity.Material;
import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.entity.MaterialRequisition;
import com.yhj.his.module.inventory.entity.MaterialRequisitionItem;
import com.yhj.his.module.inventory.entity.Warehouse;
import com.yhj.his.module.inventory.enums.RequisitionStatus;
import com.yhj.his.module.inventory.repository.MaterialInventoryRepository;
import com.yhj.his.module.inventory.repository.MaterialRepository;
import com.yhj.his.module.inventory.repository.MaterialRequisitionItemRepository;
import com.yhj.his.module.inventory.repository.MaterialRequisitionRepository;
import com.yhj.his.module.inventory.repository.WarehouseRepository;
import com.yhj.his.module.inventory.vo.MaterialRequisitionItemVO;
import com.yhj.his.module.inventory.vo.MaterialRequisitionVO;
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
 * 物资申领服务实现
 */
@Service
@RequiredArgsConstructor
public class RequisitionServiceImpl implements RequisitionService {

    private final MaterialRequisitionRepository requisitionRepository;
    private final MaterialRequisitionItemRepository itemRepository;
    private final MaterialRepository materialRepository;
    private final WarehouseRepository warehouseRepository;
    private final MaterialInventoryRepository inventoryRepository;

    @Override
    @Transactional
    public MaterialRequisitionVO apply(RequisitionDTO dto) {
        // 检查库房是否存在
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new BusinessException("库房不存在"));

        // 创建申领记录
        MaterialRequisition requisition = new MaterialRequisition();
        requisition.setRequisitionNo(SequenceGenerator.generate("SL"));
        requisition.setWarehouseId(dto.getWarehouseId());
        requisition.setWarehouseName(warehouse.getWarehouseName());
        requisition.setDeptId(dto.getDeptId());
        requisition.setDeptName(dto.getDeptName());
        requisition.setRequisitionDate(LocalDate.now());
        requisition.setApplicantId(dto.getApplicantId());
        requisition.setApplicantName(dto.getApplicantName());
        requisition.setApplyTime(LocalDateTime.now());
        requisition.setRemark(dto.getRemark());
        requisition.setStatus(RequisitionStatus.PENDING);

        // 创建申领明细
        BigDecimal totalQuantity = BigDecimal.ZERO;
        List<MaterialRequisitionItem> items = new ArrayList<>();

        for (RequisitionItemDTO itemDto : dto.getItems()) {
            Material material = materialRepository.findById(itemDto.getMaterialId())
                    .orElseThrow(() -> new BusinessException("物资不存在: " + itemDto.getMaterialId()));

            MaterialRequisitionItem item = new MaterialRequisitionItem();
            item.setRequisition(requisition);
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

        requisition.setTotalQuantity(totalQuantity);
        requisition.setItems(items);

        requisition = requisitionRepository.save(requisition);
        return toVO(requisition);
    }

    @Override
    @Transactional
    public MaterialRequisitionVO approve(AuditDTO dto) {
        MaterialRequisition requisition = requisitionRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("申领记录不存在"));

        if (requisition.getStatus() != RequisitionStatus.PENDING) {
            throw new BusinessException("申领单状态不正确，无法审批");
        }

        if ("PASS".equals(dto.getResult())) {
            // 检查库存是否足够
            for (MaterialRequisitionItem item : requisition.getItems()) {
                BigDecimal availableStock = inventoryRepository.sumQuantityByMaterialIdAndWarehouseId(
                        item.getMaterialId(), requisition.getWarehouseId());
                if (availableStock == null || availableStock.compareTo(item.getApplyQuantity()) < 0) {
                    throw new BusinessException("物资库存不足: " + item.getMaterialName());
                }
            }
            requisition.setStatus(RequisitionStatus.APPROVED);
        } else if ("REJECT".equals(dto.getResult())) {
            requisition.setStatus(RequisitionStatus.REJECTED);
        }

        requisition.setApproverId(dto.getAuditorId());
        requisition.setApproverName(dto.getAuditorName());
        requisition.setApproveTime(LocalDateTime.now());
        requisition.setApproveRemark(dto.getRemark());

        requisition = requisitionRepository.save(requisition);
        return toVO(requisition);
    }

    @Override
    @Transactional
    public MaterialRequisitionVO issue(ConfirmDTO dto) {
        MaterialRequisition requisition = requisitionRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("申领记录不存在"));

        if (requisition.getStatus() != RequisitionStatus.APPROVED) {
            throw new BusinessException("申领单状态不正确，无法发放");
        }

        // 按先进先出原则扣减库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (MaterialRequisitionItem item : requisition.getItems()) {
            BigDecimal remaining = item.getApplyQuantity();
            BigDecimal itemAmount = BigDecimal.ZERO;
            BigDecimal issueQuantity = BigDecimal.ZERO;

            // 查询可用库存（按效期排序）
            List<MaterialInventory> inventories = inventoryRepository.findAvailableInventoryOrderByExpiry(
                    item.getMaterialId(), requisition.getWarehouseId(), LocalDate.now());

            for (MaterialInventory inventory : inventories) {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }

                BigDecimal deduct = inventory.getAvailableQuantity().min(remaining);
                inventory.setQuantity(inventory.getQuantity().subtract(deduct));
                inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(deduct));
                inventoryRepository.save(inventory);

                remaining = remaining.subtract(deduct);
                issueQuantity = issueQuantity.add(deduct);
                itemAmount = itemAmount.add(deduct.multiply(inventory.getPurchasePrice() != null ?
                        inventory.getPurchasePrice() : BigDecimal.ZERO));
            }

            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                throw new BusinessException("物资库存不足: " + item.getMaterialName());
            }

            item.setIssueQuantity(issueQuantity);
            item.setAmount(itemAmount);
            totalAmount = totalAmount.add(itemAmount);
        }

        requisition.setStatus(RequisitionStatus.ISSUED);
        requisition.setIssuerId(dto.getOperatorId());
        requisition.setIssuerName(dto.getOperatorName());
        requisition.setIssueTime(LocalDateTime.now());
        requisition.setTotalAmount(totalAmount);

        requisition = requisitionRepository.save(requisition);
        return toVO(requisition);
    }

    @Override
    @Transactional
    public MaterialRequisitionVO receive(ConfirmDTO dto) {
        MaterialRequisition requisition = requisitionRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("申领记录不存在"));

        if (requisition.getStatus() != RequisitionStatus.ISSUED) {
            throw new BusinessException("申领单状态不正确，无法接收");
        }

        requisition.setStatus(RequisitionStatus.RECEIVED);
        requisition.setReceiverId(dto.getOperatorId());
        requisition.setReceiverName(dto.getOperatorName());
        requisition.setReceiveTime(LocalDateTime.now());

        requisition = requisitionRepository.save(requisition);
        return toVO(requisition);
    }

    @Override
    @Transactional
    public void cancel(String id) {
        MaterialRequisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("申领记录不存在"));

        if (requisition.getStatus() != RequisitionStatus.PENDING) {
            throw new BusinessException("只有待审批状态的申领单可以取消");
        }

        requisition.setStatus(RequisitionStatus.CANCELLED);
        requisitionRepository.save(requisition);
    }

    @Override
    public MaterialRequisitionVO getById(String id) {
        MaterialRequisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("申领记录不存在"));
        return toVO(requisition);
    }

    @Override
    public MaterialRequisitionVO getByNo(String requisitionNo) {
        MaterialRequisition requisition = requisitionRepository.findByRequisitionNo(requisitionNo)
                .orElseThrow(() -> new BusinessException("申领记录不存在"));
        return toVO(requisition);
    }

    @Override
    public PageResult<MaterialRequisitionVO> list(Integer pageNum, Integer pageSize) {
        Page<MaterialRequisition> page = requisitionRepository.findByDeletedFalse(PageRequest.of(pageNum - 1, pageSize));
        List<MaterialRequisitionVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public PageResult<MaterialRequisitionVO> query(QueryDTO query) {
        Page<MaterialRequisition> page = requisitionRepository.search(
                query.getWarehouseId(),
                query.getDeptId(),
                query.getStatus() != null ? RequisitionStatus.valueOf(query.getStatus()) : null,
                query.getStartDate(),
                query.getEndDate(),
                PageRequest.of(query.getPageNum() - 1, query.getPageSize())
        );
        List<MaterialRequisitionVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public List<MaterialRequisitionVO> listPending() {
        List<MaterialRequisition> list = requisitionRepository.findPendingRequisitions();
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<MaterialRequisitionVO> listByDept(String deptId) {
        List<MaterialRequisition> list = requisitionRepository.findByDeptIdOrderByDateDesc(deptId);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    private MaterialRequisitionVO toVO(MaterialRequisition entity) {
        MaterialRequisitionVO vo = new MaterialRequisitionVO();
        vo.setId(entity.getId());
        vo.setRequisitionNo(entity.getRequisitionNo());
        vo.setWarehouseId(entity.getWarehouseId());
        vo.setWarehouseName(entity.getWarehouseName());
        vo.setDeptId(entity.getDeptId());
        vo.setDeptName(entity.getDeptName());
        vo.setRequisitionDate(entity.getRequisitionDate());
        vo.setTotalQuantity(entity.getTotalQuantity());
        vo.setTotalAmount(entity.getTotalAmount());
        vo.setApplicantId(entity.getApplicantId());
        vo.setApplicantName(entity.getApplicantName());
        vo.setApplyTime(entity.getApplyTime());
        vo.setApproverId(entity.getApproverId());
        vo.setApproverName(entity.getApproverName());
        vo.setApproveTime(entity.getApproveTime());
        vo.setApproveRemark(entity.getApproveRemark());
        vo.setIssuerId(entity.getIssuerId());
        vo.setIssuerName(entity.getIssuerName());
        vo.setIssueTime(entity.getIssueTime());
        vo.setReceiverId(entity.getReceiverId());
        vo.setReceiverName(entity.getReceiverName());
        vo.setReceiveTime(entity.getReceiveTime());
        vo.setRemark(entity.getRemark());
        vo.setStatus(entity.getStatus().name());
        vo.setStatusName(entity.getStatus().getName());
        vo.setCreateTime(entity.getCreateTime());

        // 转换明细
        List<MaterialRequisitionItemVO> items = entity.getItems().stream()
                .map(this::toItemVO).collect(Collectors.toList());
        vo.setItems(items);

        return vo;
    }

    private MaterialRequisitionItemVO toItemVO(MaterialRequisitionItem entity) {
        MaterialRequisitionItemVO vo = new MaterialRequisitionItemVO();
        vo.setId(entity.getId());
        vo.setMaterialId(entity.getMaterialId());
        vo.setMaterialCode(entity.getMaterialCode());
        vo.setMaterialName(entity.getMaterialName());
        vo.setMaterialSpec(entity.getMaterialSpec());
        vo.setMaterialUnit(entity.getMaterialUnit());
        vo.setApplyQuantity(entity.getApplyQuantity());
        vo.setIssueQuantity(entity.getIssueQuantity());
        vo.setPurchasePrice(entity.getPurchasePrice());
        vo.setRetailPrice(entity.getRetailPrice());
        vo.setAmount(entity.getAmount());
        vo.setRemark(entity.getRemark());
        return vo;
    }
}