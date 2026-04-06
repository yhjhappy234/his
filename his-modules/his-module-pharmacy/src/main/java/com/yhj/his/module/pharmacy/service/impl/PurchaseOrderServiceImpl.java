package com.yhj.his.module.pharmacy.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.PurchaseApplyDTO;
import com.yhj.his.module.pharmacy.dto.PurchaseAuditDTO;
import com.yhj.his.module.pharmacy.dto.PurchaseQueryDTO;
import com.yhj.his.module.pharmacy.dto.ReceiveConfirmDTO;
import com.yhj.his.module.pharmacy.entity.PurchaseOrder;
import com.yhj.his.module.pharmacy.entity.PurchaseOrderItem;
import com.yhj.his.module.pharmacy.enums.PurchaseOrderStatus;
import com.yhj.his.module.pharmacy.repository.PurchaseOrderItemRepository;
import com.yhj.his.module.pharmacy.repository.PurchaseOrderRepository;
import com.yhj.his.module.pharmacy.service.DrugInventoryService;
import com.yhj.his.module.pharmacy.service.PurchaseOrderService;
import com.yhj.his.module.pharmacy.vo.PurchaseOrderVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 采购订单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final DrugInventoryService drugInventoryService;

    @Override
    @Transactional
    public Result<PurchaseOrderVO> createPurchaseOrder(PurchaseApplyDTO dto) {
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo(generateOrderNo());
        order.setSupplierId(dto.getSupplierId());
        order.setSupplierName(dto.getSupplierName());
        order.setOrderDate(LocalDate.now());
        order.setExpectedDate(dto.getExpectedDate());
        order.setStatus(PurchaseOrderStatus.DRAFT);
        order.setApplicantId(dto.getApplicantId());
        order.setApplicantName(dto.getApplicantName());

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderItem> items = new ArrayList<>();

        if (dto.getItems() != null) {
            for (PurchaseApplyDTO.PurchaseItemDTO itemDto : dto.getItems()) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setOrderId(order.getId());
                item.setDrugId(itemDto.getDrugId());
                item.setDrugCode(itemDto.getDrugCode());
                item.setDrugName(itemDto.getDrugName());
                item.setDrugSpec(itemDto.getDrugSpec());
                item.setDrugUnit(itemDto.getDrugUnit());
                item.setQuantity(itemDto.getQuantity());
                item.setReceivedQuantity(BigDecimal.ZERO);
                item.setPurchasePrice(itemDto.getPurchasePrice());
                BigDecimal amount = itemDto.getQuantity().multiply(itemDto.getPurchasePrice() != null ? itemDto.getPurchasePrice() : BigDecimal.ZERO);
                item.setAmount(amount);
                item.setRemark(itemDto.getRemark());
                items.add(item);
                totalQuantity = totalQuantity.add(itemDto.getQuantity());
                totalAmount = totalAmount.add(amount);
            }
        }

        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount);
        PurchaseOrder saved = purchaseOrderRepository.save(order);

        for (PurchaseOrderItem item : items) {
            item.setOrderId(saved.getId());
        }
        purchaseOrderItemRepository.saveAll(items);

        return Result.success(entityToVO(saved, items));
    }

    @Override
    @Transactional
    public Result<PurchaseOrderVO> updatePurchaseOrder(String orderId, PurchaseApplyDTO dto) {
        Optional<PurchaseOrder> optional = purchaseOrderRepository.findById(orderId);
        if (!optional.isPresent()) {
            return Result.error("采购订单不存在: " + orderId);
        }
        PurchaseOrder order = optional.get();
        if (order.getStatus() != PurchaseOrderStatus.DRAFT) {
            return Result.error("只有草稿状态的订单可以修改");
        }

        order.setSupplierId(dto.getSupplierId());
        order.setSupplierName(dto.getSupplierName());
        order.setExpectedDate(dto.getExpectedDate());

        purchaseOrderItemRepository.deleteByOrderId(orderId);

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderItem> items = new ArrayList<>();

        if (dto.getItems() != null) {
            for (PurchaseApplyDTO.PurchaseItemDTO itemDto : dto.getItems()) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setOrderId(orderId);
                item.setDrugId(itemDto.getDrugId());
                item.setDrugCode(itemDto.getDrugCode());
                item.setDrugName(itemDto.getDrugName());
                item.setDrugSpec(itemDto.getDrugSpec());
                item.setDrugUnit(itemDto.getDrugUnit());
                item.setQuantity(itemDto.getQuantity());
                item.setReceivedQuantity(BigDecimal.ZERO);
                item.setPurchasePrice(itemDto.getPurchasePrice());
                BigDecimal amount = itemDto.getQuantity().multiply(itemDto.getPurchasePrice() != null ? itemDto.getPurchasePrice() : BigDecimal.ZERO);
                item.setAmount(amount);
                item.setRemark(itemDto.getRemark());
                items.add(item);
                totalQuantity = totalQuantity.add(itemDto.getQuantity());
                totalAmount = totalAmount.add(amount);
            }
        }

        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount);
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        purchaseOrderItemRepository.saveAll(items);

        return Result.success(entityToVO(saved, items));
    }

    @Override
    @Transactional
    public Result<Void> deletePurchaseOrder(String orderId) {
        Optional<PurchaseOrder> optional = purchaseOrderRepository.findById(orderId);
        if (!optional.isPresent()) {
            return Result.error("采购订单不存在: " + orderId);
        }
        PurchaseOrder order = optional.get();
        if (order.getStatus() != PurchaseOrderStatus.DRAFT && order.getStatus() != PurchaseOrderStatus.CANCELLED) {
            return Result.error("只有草稿或已取消的订单可以删除");
        }
        purchaseOrderItemRepository.deleteByOrderId(orderId);
        purchaseOrderRepository.delete(order);
        return Result.successVoid();
    }

    @Override
    public Result<PurchaseOrderVO> getPurchaseOrderById(String orderId) {
        Optional<PurchaseOrder> optional = purchaseOrderRepository.findById(orderId);
        if (!optional.isPresent()) {
            return Result.error("采购订单不存在: " + orderId);
        }
        PurchaseOrder order = optional.get();
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(orderId);
        return Result.success(entityToVO(order, items));
    }

    @Override
    public Result<PurchaseOrderVO> getPurchaseOrderByNo(String orderNo) {
        Optional<PurchaseOrder> optional = purchaseOrderRepository.findByOrderNo(orderNo);
        if (!optional.isPresent()) {
            return Result.error("采购订单不存在: " + orderNo);
        }
        PurchaseOrder order = optional.get();
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(order.getId());
        return Result.success(entityToVO(order, items));
    }

    @Override
    public Result<PageResult<PurchaseOrderVO>> queryPurchaseOrders(PurchaseQueryDTO query) {
        Pageable pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
        PurchaseOrderStatus status = query.getStatus() != null ? PurchaseOrderStatus.valueOf(query.getStatus()) : null;
        Page<PurchaseOrder> page = purchaseOrderRepository.queryOrders(
                query.getSupplierId(), query.getOrderNo(), status,
                query.getStartDate(), query.getEndDate(), pageable);
        List<PurchaseOrderVO> list = page.getContent().stream()
                .map(order -> {
                    List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(order.getId());
                    return entityToVO(order, items);
                })
                .collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize()));
    }

    @Override
    @Transactional
    public Result<PurchaseOrderVO> submitPurchaseOrder(String orderId, String applicantId, String applicantName) {
        Optional<PurchaseOrder> optional = purchaseOrderRepository.findById(orderId);
        if (!optional.isPresent()) {
            return Result.error("采购订单不存在: " + orderId);
        }
        PurchaseOrder order = optional.get();
        if (order.getStatus() != PurchaseOrderStatus.DRAFT) {
            return Result.error("只有草稿状态的订单可以提交");
        }
        order.setStatus(PurchaseOrderStatus.PENDING);
        order.setApplicantId(applicantId);
        order.setApplicantName(applicantName);
        order.setApplyTime(LocalDateTime.now());
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(orderId);
        return Result.success(entityToVO(saved, items));
    }

    @Override
    @Transactional
    public Result<PurchaseOrderVO> auditPurchaseOrder(String orderId, PurchaseAuditDTO dto) {
        Optional<PurchaseOrder> optional = purchaseOrderRepository.findById(orderId);
        if (!optional.isPresent()) {
            return Result.error("采购订单不存在: " + orderId);
        }
        PurchaseOrder order = optional.get();
        if (order.getStatus() != PurchaseOrderStatus.PENDING) {
            return Result.error("只有待审核状态的订单可以审核");
        }
        order.setAuditorId(dto.getAuditorId());
        order.setAuditorName(dto.getAuditorName());
        order.setAuditTime(LocalDateTime.now());
        order.setAuditRemark(dto.getAuditRemark());
        if (dto.getApproved()) {
            order.setStatus(PurchaseOrderStatus.APPROVED);
        } else {
            order.setStatus(PurchaseOrderStatus.REJECTED);
        }
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(orderId);
        return Result.success(entityToVO(saved, items));
    }

    @Override
    @Transactional
    public Result<Void> cancelPurchaseOrder(String orderId, String reason) {
        Optional<PurchaseOrder> optional = purchaseOrderRepository.findById(orderId);
        if (!optional.isPresent()) {
            return Result.error("采购订单不存在: " + orderId);
        }
        PurchaseOrder order = optional.get();
        if (order.getStatus() == PurchaseOrderStatus.COMPLETED || order.getStatus() == PurchaseOrderStatus.PARTIAL_RECEIVED) {
            return Result.error("已完成或部分入库的订单不能取消");
        }
        order.setStatus(PurchaseOrderStatus.CANCELLED);
        order.setAuditRemark(reason);
        purchaseOrderRepository.save(order);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<PurchaseOrderVO> confirmReceive(String orderId, ReceiveConfirmDTO dto) {
        Optional<PurchaseOrder> optional = purchaseOrderRepository.findById(orderId);
        if (!optional.isPresent()) {
            return Result.error("采购订单不存在: " + orderId);
        }
        PurchaseOrder order = optional.get();
        if (order.getStatus() != PurchaseOrderStatus.APPROVED && order.getStatus() != PurchaseOrderStatus.PARTIAL_RECEIVED) {
            return Result.error("只有已审核或部分入库的订单可以确认收货");
        }

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(orderId);
        BigDecimal totalReceived = BigDecimal.ZERO;

        if (dto.getItems() != null) {
            for (ReceiveConfirmDTO.ReceiveItemDTO receiveItem : dto.getItems()) {
                Optional<PurchaseOrderItem> itemOptional = items.stream()
                        .filter(i -> i.getId().equals(receiveItem.getItemId()))
                        .findFirst();
                if (itemOptional.isPresent()) {
                    PurchaseOrderItem item = itemOptional.get();
                    item.setReceivedQuantity(item.getReceivedQuantity().add(receiveItem.getQuantity()));
                    purchaseOrderItemRepository.save(item);
                    totalReceived = totalReceived.add(receiveItem.getQuantity());
                }
            }
        }

        boolean allReceived = items.stream()
                .allMatch(i -> i.getReceivedQuantity().compareTo(i.getQuantity()) >= 0);

        if (allReceived) {
            order.setStatus(PurchaseOrderStatus.COMPLETED);
        } else {
            order.setStatus(PurchaseOrderStatus.PARTIAL_RECEIVED);
        }

        PurchaseOrder saved = purchaseOrderRepository.save(order);
        List<PurchaseOrderItem> updatedItems = purchaseOrderItemRepository.findByOrderId(orderId);
        return Result.success(entityToVO(saved, updatedItems));
    }

    @Override
    public Result<List<PurchaseOrderVO>> getPendingAuditOrders() {
        List<PurchaseOrder> list = purchaseOrderRepository.findByStatus(PurchaseOrderStatus.PENDING);
        List<PurchaseOrderVO> vos = list.stream()
                .map(order -> {
                    List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(order.getId());
                    return entityToVO(order, items);
                })
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<List<PurchaseOrderVO>> getOrdersBySupplier(String supplierId) {
        List<PurchaseOrder> list = purchaseOrderRepository.findBySupplierId(supplierId);
        List<PurchaseOrderVO> vos = list.stream()
                .map(order -> {
                    List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByOrderId(order.getId());
                    return entityToVO(order, items);
                })
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    @Transactional
    public Result<Void> updateOrderStatus(String orderId, PurchaseOrderStatus status) {
        Optional<PurchaseOrder> optional = purchaseOrderRepository.findById(orderId);
        if (!optional.isPresent()) {
            return Result.error("采购订单不存在: " + orderId);
        }
        PurchaseOrder order = optional.get();
        order.setStatus(status);
        purchaseOrderRepository.save(order);
        return Result.successVoid();
    }

    private String generateOrderNo() {
        return "PO" + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + UUID.randomUUID().toString().substring(0, 6);
    }

    private PurchaseOrderVO entityToVO(PurchaseOrder entity, List<PurchaseOrderItem> items) {
        PurchaseOrderVO vo = new PurchaseOrderVO();
        vo.setOrderId(entity.getId());
        vo.setOrderNo(entity.getOrderNo());
        vo.setSupplierId(entity.getSupplierId());
        vo.setSupplierName(entity.getSupplierName());
        vo.setOrderDate(entity.getOrderDate());
        vo.setExpectedDate(entity.getExpectedDate());
        vo.setTotalQuantity(entity.getTotalQuantity());
        vo.setTotalAmount(entity.getTotalAmount());
        vo.setStatus(entity.getStatus());
        vo.setApplicantName(entity.getApplicantName());
        vo.setApplyTime(entity.getApplyTime());
        vo.setAuditorName(entity.getAuditorName());
        vo.setAuditTime(entity.getAuditTime());
        vo.setAuditRemark(entity.getAuditRemark());
        vo.setCreateTime(entity.getCreateTime());

        if (items != null) {
            List<PurchaseOrderVO.PurchaseItemVO> itemVOs = items.stream().map(this::itemToVO).collect(Collectors.toList());
            vo.setItems(itemVOs);
        }
        return vo;
    }

    private PurchaseOrderVO.PurchaseItemVO itemToVO(PurchaseOrderItem item) {
        PurchaseOrderVO.PurchaseItemVO vo = new PurchaseOrderVO.PurchaseItemVO();
        vo.setItemId(item.getId());
        vo.setDrugId(item.getDrugId());
        vo.setDrugCode(item.getDrugCode());
        vo.setDrugName(item.getDrugName());
        vo.setDrugSpec(item.getDrugSpec());
        vo.setDrugUnit(item.getDrugUnit());
        vo.setQuantity(item.getQuantity());
        vo.setReceivedQuantity(item.getReceivedQuantity());
        vo.setPurchasePrice(item.getPurchasePrice());
        vo.setAmount(item.getAmount());
        vo.setRemark(item.getRemark());
        return vo;
    }
}