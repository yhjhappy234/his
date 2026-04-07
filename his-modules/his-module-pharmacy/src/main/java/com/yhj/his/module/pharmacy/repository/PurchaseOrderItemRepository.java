package com.yhj.his.module.pharmacy.repository;

import com.yhj.his.module.pharmacy.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 采购订单明细Repository
 */
@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, String>, JpaSpecificationExecutor<PurchaseOrderItem> {

    /**
     * 根据订单ID查询明细
     */
    List<PurchaseOrderItem> findByOrderId(String orderId);

    /**
     * 根据药品ID查询明细
     */
    List<PurchaseOrderItem> findByDrugId(String drugId);

    /**
     * 查询指定订单的明细
     */
    @Query("SELECT i FROM PurchaseOrderItem i WHERE i.orderId = :orderId AND i.deleted = false ORDER BY i.createTime ASC")
    List<PurchaseOrderItem> findByOrderIdOrderById(@Param("orderId") String orderId);

    /**
     * 删除指定订单的明细
     */
    void deleteByOrderId(String orderId);
}