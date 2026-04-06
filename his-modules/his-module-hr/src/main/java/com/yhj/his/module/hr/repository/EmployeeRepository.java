package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.enums.EmployeeStatus;
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
 * 员工Repository
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String>, JpaSpecificationExecutor<Employee> {

    /**
     * 根据工号查找员工
     */
    Optional<Employee> findByEmployeeNo(String employeeNo);

    /**
     * 根据科室ID查找员工列表
     */
    List<Employee> findByDeptIdAndDeletedFalse(String deptId);

    /**
     * 根据状态查找员工列表
     */
    List<Employee> findByStatusAndDeletedFalse(EmployeeStatus status);

    /**
     * 根据科室ID和状态查找员工列表
     */
    List<Employee> findByDeptIdAndStatusAndDeletedFalse(String deptId, EmployeeStatus status);

    /**
     * 分页查询员工
     */
    @Query("SELECT e FROM Employee e WHERE e.deleted = false " +
           "AND (:deptId IS NULL OR e.deptId = :deptId) " +
           "AND (:status IS NULL OR e.status = :status) " +
           "AND (:keyword IS NULL OR e.employeeName LIKE %:keyword% OR e.employeeNo LIKE %:keyword%)")
    Page<Employee> findByConditions(@Param("deptId") String deptId,
                                     @Param("status") EmployeeStatus status,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);

    /**
     * 检查工号是否存在
     */
    boolean existsByEmployeeNo(String employeeNo);

    /**
     * 统计科室员工数量
     */
    long countByDeptIdAndDeletedFalse(String deptId);

    /**
     * 统计在职员工数量
     */
    long countByStatusAndDeletedFalse(EmployeeStatus status);

    /**
     * 根据ID查找员工(排除已删除)
     */
    Optional<Employee> findByIdAndDeletedFalse(String id);
}