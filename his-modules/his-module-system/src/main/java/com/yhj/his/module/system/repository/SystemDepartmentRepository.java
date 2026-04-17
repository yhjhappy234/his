package com.yhj.his.module.system.repository;

import com.yhj.his.module.system.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 科室数据访问 (System模块)
 */
@Repository
public interface SystemDepartmentRepository extends JpaRepository<Department, String> {

    /**
     * 根据科室编码查询
     */
    Optional<Department> findByDeptCode(String deptCode);

    /**
     * 根据科室编码查询(未删除)
     */
    Optional<Department> findByDeptCodeAndDeletedFalse(String deptCode);

    /**
     * 查询所有未删除科室
     */
    List<Department> findByDeletedFalseOrderBySortOrderAsc();

    /**
     * 根据父级ID查询子科室
     */
    List<Department> findByParentIdAndDeletedFalseOrderBySortOrderAsc(String parentId);

    /**
     * 查询顶级科室(无父级)
     */
    List<Department> findByParentIdIsNullAndDeletedFalseOrderBySortOrderAsc();

    /**
     * 根据科室类型查询
     */
    List<Department> findByDeptTypeAndDeletedFalseOrderBySortOrderAsc(String deptType);

    /**
     * 根据状态查询
     */
    List<Department> findByStatusAndDeletedFalseOrderBySortOrderAsc(String status);

    /**
     * 分页查询科室
     */
    Page<Department> findByDeletedFalse(Pageable pageable);

    /**
     * 根据条件分页查询科室
     */
    @Query("SELECT d FROM Department d WHERE d.deleted = false " +
           "AND (:deptName IS NULL OR d.deptName LIKE %:deptName%) " +
           "AND (:deptCode IS NULL OR d.deptCode LIKE %:deptCode%) " +
           "AND (:deptType IS NULL OR d.deptType = :deptType) " +
           "AND (:status IS NULL OR d.status = :status) " +
           "AND (:parentId IS NULL OR d.parentId = :parentId)")
    Page<Department> findByCondition(
            @Param("deptName") String deptName,
            @Param("deptCode") String deptCode,
            @Param("deptType") String deptType,
            @Param("status") String status,
            @Param("parentId") String parentId,
            Pageable pageable);

    /**
     * 检查科室编码是否存在
     */
    boolean existsByDeptCodeAndDeletedFalse(String deptCode);

    /**
     * 统计子科室数量
     */
    long countByParentIdAndDeletedFalse(String parentId);
}