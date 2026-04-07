package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.SalaryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 薪资记录Repository
 */
@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, String>, JpaSpecificationExecutor<SalaryRecord> {

    /**
     * 根据薪资单号查找薪资记录
     */
    Optional<SalaryRecord> findBySalaryNo(String salaryNo);

    /**
     * 根据员工ID和薪资月份查找薪资记录
     */
    Optional<SalaryRecord> findByEmployeeIdAndSalaryMonthAndDeletedFalse(String employeeId, String salaryMonth);

    /**
     * 根据员工ID查找薪资记录列表
     */
    List<SalaryRecord> findByEmployeeIdAndDeletedFalseOrderBySalaryMonthDesc(String employeeId);

    /**
     * 根据薪资月份查找薪资记录列表
     */
    List<SalaryRecord> findBySalaryMonthAndDeletedFalse(String salaryMonth);

    /**
     * 根据状态查找薪资记录列表
     */
    List<SalaryRecord> findByStatusAndDeletedFalseOrderBySalaryMonthDesc(String status);

    /**
     * 分页查询薪资记录
     */
    @Query("SELECT s FROM SalaryRecord s WHERE s.deleted = false " +
           "AND (:employeeId IS NULL OR s.employeeId = :employeeId) " +
           "AND (:deptId IS NULL OR s.deptId = :deptId) " +
           "AND (:salaryMonth IS NULL OR s.salaryMonth = :salaryMonth) " +
           "AND (:status IS NULL OR s.status = :status)")
    Page<SalaryRecord> findByConditions(@Param("employeeId") String employeeId,
                                         @Param("deptId") String deptId,
                                         @Param("salaryMonth") String salaryMonth,
                                         @Param("status") String status,
                                         Pageable pageable);

    /**
     * 统计某月份的薪资总额
     */
    @Query("SELECT SUM(s.netSalary) FROM SalaryRecord s WHERE s.deleted = false " +
           "AND s.salaryMonth = :salaryMonth " +
           "AND s.status IN ('已审核', '已发放')")
    BigDecimal sumNetSalaryByMonth(@Param("salaryMonth") String salaryMonth);

    /**
     * 统计员工某时间段内的薪资总额
     */
    @Query("SELECT SUM(s.netSalary) FROM SalaryRecord s WHERE s.deleted = false " +
           "AND s.employeeId = :employeeId " +
           "AND s.salaryMonth >= :startMonth " +
           "AND s.salaryMonth <= :endMonth " +
           "AND s.status IN ('已审核', '已发放')")
    BigDecimal sumNetSalaryByEmployeeAndMonthRange(@Param("employeeId") String employeeId,
                                                    @Param("startMonth") String startMonth,
                                                    @Param("endMonth") String endMonth);

    /**
     * 检查薪资单号是否存在
     */
    boolean existsBySalaryNo(String salaryNo);

    /**
     * 检查员工某月份薪资是否已生成
     */
    boolean existsByEmployeeIdAndSalaryMonth(String employeeId, String salaryMonth);

    /**
     * 根据ID查找未删除的薪资记录
     */
    Optional<SalaryRecord> findByIdAndDeletedFalse(String id);
}