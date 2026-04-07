package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialCheck;
import com.yhj.his.module.inventory.enums.CheckStatus;
import com.yhj.his.module.inventory.enums.CheckType;
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
 * 库存盘点Repository
 */
@Repository
public interface MaterialCheckRepository extends JpaRepository<MaterialCheck, String> {

    /**
     * 根据盘点单号查询
     */
    Optional<MaterialCheck> findByCheckNo(String checkNo);

    /**
     * 根据状态查询
     */
    List<MaterialCheck> findByStatusAndDeletedFalse(CheckStatus status);

    /**
     * 根据盘点类型查询
     */
    List<MaterialCheck> findByCheckTypeAndDeletedFalse(CheckType checkType);

    /**
     * 根据库房ID查询
     */
    List<MaterialCheck> findByWarehouseIdAndDeletedFalse(String warehouseId);

    /**
     * 根据盘点日期查询
     */
    List<MaterialCheck> findByCheckDateAndDeletedFalse(LocalDate checkDate);

    /**
     * 分页查询
     */
    Page<MaterialCheck> findByDeletedFalse(Pageable pageable);

    /**
     * 根据状态分页查询
     */
    Page<MaterialCheck> findByStatusAndDeletedFalse(CheckStatus status, Pageable pageable);

    /**
     * 条件查询
     */
    @Query("SELECT mc FROM MaterialCheck mc WHERE mc.deleted = false " +
           "AND (:warehouseId IS NULL OR mc.warehouseId = :warehouseId) " +
           "AND (:status IS NULL OR mc.status = :status) " +
           "AND (:checkType IS NULL OR mc.checkType = :checkType) " +
           "AND (:startDate IS NULL OR mc.checkDate >= :startDate) " +
           "AND (:endDate IS NULL OR mc.checkDate <= :endDate)")
    Page<MaterialCheck> search(@Param("warehouseId") String warehouseId,
                                @Param("status") CheckStatus status,
                                @Param("checkType") CheckType checkType,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                Pageable pageable);

    /**
     * 查询进行中的盘点
     */
    @Query("SELECT mc FROM MaterialCheck mc WHERE mc.deleted = false AND mc.status = 'IN_PROGRESS'")
    List<MaterialCheck> findInProgressChecks();

    /**
     * 查询待调整的盘点
     */
    @Query("SELECT mc FROM MaterialCheck mc WHERE mc.deleted = false AND mc.status = 'COMPLETED'")
    List<MaterialCheck> findCompletedChecks();
}