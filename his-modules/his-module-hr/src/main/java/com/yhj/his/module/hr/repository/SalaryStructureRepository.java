package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.SalaryStructure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 薪资结构Repository
 */
@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, String>, JpaSpecificationExecutor<SalaryStructure> {

    /**
     * 根据结构编码查找薪资结构
     */
    Optional<SalaryStructure> findByStructureCode(String structureCode);

    /**
     * 根据薪资类型查找薪资结构列表
     */
    List<SalaryStructure> findBySalaryTypeAndDeletedFalseOrderBySortOrderAsc(String salaryType);

    /**
     * 根据项目编码查找薪资结构
     */
    Optional<SalaryStructure> findByItemCodeAndDeletedFalse(String itemCode);

    /**
     * 根据状态查找薪资结构列表
     */
    List<SalaryStructure> findByStatusAndDeletedFalseOrderBySortOrderAsc(String status);

    /**
     * 分页查询薪资结构
     */
    @Query("SELECT s FROM SalaryStructure s WHERE s.deleted = false " +
           "AND (:salaryType IS NULL OR s.salaryType = :salaryType) " +
           "AND (:status IS NULL OR s.status = :status) " +
           "AND (:keyword IS NULL OR s.itemName LIKE %:keyword% OR s.structureName LIKE %:keyword%)")
    Page<SalaryStructure> findByConditions(@Param("salaryType") String salaryType,
                                            @Param("status") String status,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);

    /**
     * 查找所有启用的薪资结构（按排序号排序）
     */
    @Query("SELECT s FROM SalaryStructure s WHERE s.deleted = false AND s.status = '启用' ORDER BY s.sortOrder ASC")
    List<SalaryStructure> findAllEnabled();

    /**
     * 检查结构编码是否存在
     */
    boolean existsByStructureCode(String structureCode);

    /**
     * 检查项目编码是否存在
     */
    boolean existsByItemCode(String itemCode);
}