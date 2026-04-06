package com.yhj.his.module.pharmacy.repository;

import com.yhj.his.module.pharmacy.entity.PurchaseOrder;
import com.yhj.his.module.pharmacy.enums.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 采购订单Repository
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String>, JpaSpecificationExecutor<PurchaseOrder> {

    /**
     * 根据订单号查询
     */
    Optional<PurchaseOrder> findByOrderNo(String orderNo);

    /**
     * 根据供应商ID查询
     */
    List<PurchaseOrder> findBySupplierId(String supplierId);

    /**
     * 根据状态查询
     */
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    /**
     * 查询待审核订单
     */
    @Query("SELECT p FROM PurchaseOrder p WHERE p.status = 'PENDING' AND p.deleted = false ORDER BY p.createTime ASC")
    List<PurchaseOrder> findPendingOrders();

    /**
     * 查询待入库订单
     */
    @Query("SELECT p FROM PurchaseOrder p WHERE p.status IN ('APPROVED', 'SHIPPED', 'PARTIAL_RECEIVED') AND p.deleted = false ORDER BY p.createTime ASC")
    List<PurchaseOrder> findReceivingOrders();

    /**
     * 综合查询采购订单
     */
    @Query("SELECT p FROM PurchaseOrder p WHERE p.deleted = false " +
           "AND (:orderNo IS NULL OR p.orderNo = :orderNo) " +
           "AND (:supplierId IS NULL OR p.supplierId = :supplierId) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:startDate IS NULL OR p.orderDate >= :startDate) " +
           "AND (:endDate IS NULL OR p.orderDate <= :endDate) " +
           "ORDER BY p.createTime DESC")
    Page<PurchaseOrder> queryPurchaseOrders(@Param("orderNo") String orderNo,
                                            @Param("supplierId") String supplierId,
                                            @Param("status") PurchaseOrderStatus status,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            Pageable pageable);

    /**
     * 查询最新订单号
     */
    @Query("SELECT MAX(p.orderNo) FROM PurchaseOrder p WHERE p.orderNo LIKE :prefix")
    String findMaxOrderNo(@Param("prefix") String prefix);

    /**
     * 综合查询采购订单(简化版)
     */
    @Query("SELECT p FROM PurchaseOrder p WHERE p.deleted = false " +
           "AND (:supplierId IS NULL OR p.supplierId = :supplierId) " +
           "AND (:orderNo IS NULL OR p.orderNo = :orderNo) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:startDate IS NULL OR p.orderDate >= :startDate) " +
           "AND (:endDate IS NULL OR p.orderDate <= :endDate) " +
           "ORDER BY p.createTime DESC")
    Page<PurchaseOrder> queryOrders(@Param("supplierId") String supplierId,
                                    @Param("orderNo") String orderNo,
                                    @Param("status") PurchaseOrderStatus status,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    Pageable pageable);
}