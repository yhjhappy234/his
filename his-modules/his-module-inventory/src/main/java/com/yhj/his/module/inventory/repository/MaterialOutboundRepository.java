package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialOutbound;
import com.yhj.his.module.inventory.enums.OutboundStatus;
import com.yhj.his.module.inventory.enums.OutboundType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 出库记录Repository
 */
@Repository
public interface MaterialOutboundRepository extends JpaRepository<MaterialOutbound, String> {

    /**
     * 根据出库单号查询
     */
    Optional<MaterialOutbound> findByOutboundNo(String outboundNo);

    /**
     * 根据状态查询
     */
    List<MaterialOutbound> findByStatusAndDeletedFalse(OutboundStatus status);

    /**
     * 根据出库类型查询
     */
    List<MaterialOutbound> findByOutboundTypeAndDeletedFalse(OutboundType outboundType);

    /**
     * 根据库房ID查询
     */
    List<MaterialOutbound> findByWarehouseIdAndDeletedFalse(String warehouseId);

    /**
     * 根据目标科室ID查询
     */
    List<MaterialOutbound> findByTargetDeptIdAndDeletedFalse(String targetDeptId);

    /**
     * 根据出库日期查询
     */
    List<MaterialOutbound> findByOutboundDateAndDeletedFalse(LocalDate outboundDate);

    /**
     * 分页查询
     */
    Page<MaterialOutbound> findByDeletedFalse(Pageable pageable);

    /**
     * 根据状态分页查询
     */
    Page<MaterialOutbound> findByStatusAndDeletedFalse(OutboundStatus status, Pageable pageable);

    /**
     * 条件查询
     */
    @Query("SELECT mo FROM MaterialOutbound mo WHERE mo.deleted = false " +
           "AND (:warehouseId IS NULL OR mo.warehouseId = :warehouseId) " +
           "AND (:status IS NULL OR mo.status = :status) " +
           "AND (:outboundType IS NULL OR mo.outboundType = :outboundType) " +
           "AND (:targetDeptId IS NULL OR mo.targetDeptId = :targetDeptId) " +
           "AND (:startDate IS NULL OR mo.outboundDate >= :startDate) " +
           "AND (:endDate IS NULL OR mo.outboundDate <= :endDate)")
    Page<MaterialOutbound> search(@Param("warehouseId") String warehouseId,
                                   @Param("status") OutboundStatus status,
                                   @Param("outboundType") OutboundType outboundType,
                                   @Param("targetDeptId") String targetDeptId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   Pageable pageable);

    /**
     * 查询待审核出库单
     */
    @Query("SELECT mo FROM MaterialOutbound mo WHERE mo.deleted = false AND mo.status = 'PENDING'")
    List<MaterialOutbound> findPendingOutbounds();
}