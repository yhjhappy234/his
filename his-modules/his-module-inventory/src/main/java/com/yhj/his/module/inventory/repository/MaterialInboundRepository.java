package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialInbound;
import com.yhj.his.module.inventory.enums.InboundStatus;
import com.yhj.his.module.inventory.enums.InboundType;
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
 * 入库记录Repository
 */
@Repository
public interface MaterialInboundRepository extends JpaRepository<MaterialInbound, String> {

    /**
     * 根据入库单号查询
     */
    Optional<MaterialInbound> findByInboundNo(String inboundNo);

    /**
     * 根据状态查询
     */
    List<MaterialInbound> findByStatusAndDeletedFalse(InboundStatus status);

    /**
     * 根据入库类型查询
     */
    List<MaterialInbound> findByInboundTypeAndDeletedFalse(InboundType inboundType);

    /**
     * 根据库房ID查询
     */
    List<MaterialInbound> findByWarehouseIdAndDeletedFalse(String warehouseId);

    /**
     * 根据入库日期查询
     */
    List<MaterialInbound> findByInboundDateAndDeletedFalse(LocalDate inboundDate);

    /**
     * 分页查询
     */
    Page<MaterialInbound> findByDeletedFalse(Pageable pageable);

    /**
     * 根据状态分页查询
     */
    Page<MaterialInbound> findByStatusAndDeletedFalse(InboundStatus status, Pageable pageable);

    /**
     * 条件查询
     */
    @Query("SELECT mi FROM MaterialInbound mi WHERE mi.deleted = false " +
           "AND (:warehouseId IS NULL OR mi.warehouseId = :warehouseId) " +
           "AND (:status IS NULL OR mi.status = :status) " +
           "AND (:inboundType IS NULL OR mi.inboundType = :inboundType) " +
           "AND (:startDate IS NULL OR mi.inboundDate >= :startDate) " +
           "AND (:endDate IS NULL OR mi.inboundDate <= :endDate)")
    Page<MaterialInbound> search(@Param("warehouseId") String warehouseId,
                                  @Param("status") InboundStatus status,
                                  @Param("inboundType") InboundType inboundType,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  Pageable pageable);

    /**
     * 查询待审核入库单
     */
    @Query("SELECT mi FROM MaterialInbound mi WHERE mi.deleted = false AND mi.status = 'PENDING'")
    List<MaterialInbound> findPendingInbounds();
}