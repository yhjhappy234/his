package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.Department;
import com.yhj.his.module.hr.enums.DepartmentType;
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
 * 科室Repository
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String>, JpaSpecificationExecutor<Department> {

    /**
     * 根据编码查找科室
     */
    Optional<Department> findByDeptCode(String deptCode);

    /**
     * 根据名称查找科室
     */
    Optional<Department> findByDeptName(String deptName);

    /**
     * 根据父科室ID查找子科室列表
     */
    List<Department> findByParentIdAndDeletedFalse(String parentId);

    /**
     * 查找顶级科室（无父科室）
     */
    List<Department> findByParentIdIsNullAndDeletedFalse();

    /**
     * 根据科室类型查找科室列表
     */
    List<Department> findByDeptTypeAndDeletedFalse(DepartmentType deptType);

    /**
     * 根据状态查找科室列表
     */
    List<Department> findByStatusAndDeletedFalse(String status);

    /**
     * 分页查询科室
     */
    @Query("SELECT d FROM Department d WHERE d.deleted = false " +
           "AND (:deptType IS NULL OR d.deptType = :deptType) " +
           "AND (:status IS NULL OR d.status = :status) " +
           "AND (:keyword IS NULL OR d.deptName LIKE %:keyword% OR d.deptCode LIKE %:keyword%)")
    Page<Department> findByConditions(@Param("deptType") DepartmentType deptType,
                                       @Param("status") String status,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);

    /**
     * 检查编码是否存在
     */
    boolean existsByDeptCode(String deptCode);

    /**
     * 检查名称是否存在
     */
    boolean existsByDeptName(String deptName);

    /**
     * 查找所有科室（按排序号排序）
     */
    List<Department> findByDeletedFalseOrderBySortOrderAsc();

    /**
     * 查找科室树形结构
     */
    @Query("SELECT d FROM Department d WHERE d.deleted = false ORDER BY d.deptLevel ASC, d.sortOrder ASC")
    List<Department> findAllOrderByLevelAndSort();

    /**
     * 根据ID查找科室(排除已删除)
     */
    Optional<Department> findByIdAndDeletedFalse(String id);
}