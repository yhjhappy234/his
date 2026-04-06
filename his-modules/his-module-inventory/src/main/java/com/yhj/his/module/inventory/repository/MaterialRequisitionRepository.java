package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.MaterialRequisition;
import com.yhj.his.module.inventory.enums.RequisitionStatus;
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
 * 物资申领Repository
 */
@Repository
public interface MaterialRequisitionRepository extends JpaRepository<MaterialRequisition, String> {

    /**
     * 根据申领单号查询
     */
    Optional<MaterialRequisition> findByRequisitionNo(String requisitionNo);

    /**
     * 根据状态查询
     */
    List<MaterialRequisition> findByStatusAndDeletedFalse(RequisitionStatus status);

    /**
     * 根据库房ID查询
     */
    List<MaterialRequisition> findByWarehouseIdAndDeletedFalse(String warehouseId);

    /**
     * 根据科室ID查询
     */
    List<MaterialRequisition> findByDeptIdAndDeletedFalse(String deptId);

    /**
     * 根据申领日期查询
     */
    List<MaterialRequisition> findByRequisitionDateAndDeletedFalse(LocalDate requisitionDate);

    /**
     * 分页查询
     */
    Page<MaterialRequisition> findByDeletedFalse(Pageable pageable);

    /**
     * 根据状态分页查询
     */
    Page<MaterialRequisition> findByStatusAndDeletedFalse(RequisitionStatus status, Pageable pageable);

    /**
     * 条件查询
     */
    @Query("SELECT mr FROM MaterialRequisition mr WHERE mr.deleted = false " +
           "AND (:warehouseId IS NULL OR mr.warehouseId = :warehouseId) " +
           "AND (:deptId IS NULL OR mr.deptId = :deptId) " +
           "AND (:status IS NULL OR mr.status = :status) " +
           "AND (:startDate IS NULL OR mr.requisitionDate >= :startDate) " +
           "AND (:endDate IS NULL OR mr.requisitionDate <= :endDate)")
    Page<MaterialRequisition> search(@Param("warehouseId") String warehouseId,
                                      @Param("deptId") String deptId,
                                      @Param("status") RequisitionStatus status,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      Pageable pageable);

    /**
     * 查询待审批申领单
     */
    @Query("SELECT mr FROM MaterialRequisition mr WHERE mr.deleted = false AND mr.status = 'PENDING'")
    List<MaterialRequisition> findPendingRequisitions();

    /**
     * 查询某科室的申领记录
     */
    @Query("SELECT mr FROM MaterialRequisition mr WHERE mr.deleted = false AND mr.deptId = :deptId ORDER BY mr.requisitionDate DESC")
    List<MaterialRequisition> findByDeptIdOrderByDateDesc(@Param("deptId") String deptId);
}