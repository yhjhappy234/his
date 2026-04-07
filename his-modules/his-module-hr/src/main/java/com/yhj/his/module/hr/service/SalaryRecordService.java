package com.yhj.his.module.hr.service;

import com.yhj.his.module.hr.entity.SalaryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 薪资记录服务接口
 */
public interface SalaryRecordService {

    /**
     * 创建薪资记录
     */
    SalaryRecord createSalaryRecord(SalaryRecord salaryRecord);

    /**
     * 更新薪资记录
     */
    SalaryRecord updateSalaryRecord(SalaryRecord salaryRecord);

    /**
     * 根据ID删除薪资记录（逻辑删除）
     */
    void deleteSalaryRecord(String id);

    /**
     * 根据ID获取薪资记录
     */
    Optional<SalaryRecord> getSalaryRecordById(String id);

    /**
     * 根据薪资单号获取薪资记录
     */
    Optional<SalaryRecord> getSalaryRecordByNo(String salaryNo);

    /**
     * 根据员工ID和薪资月份获取薪资记录
     */
    Optional<SalaryRecord> getSalaryRecordByEmployeeAndMonth(String employeeId, String salaryMonth);

    /**
     * 根据员工ID获取薪资记录列表
     */
    List<SalaryRecord> getSalaryRecordsByEmployeeId(String employeeId);

    /**
     * 根据薪资月份获取薪资记录列表
     */
    List<SalaryRecord> getSalaryRecordsByMonth(String salaryMonth);

    /**
     * 根据状态获取薪资记录列表
     */
    List<SalaryRecord> getSalaryRecordsByStatus(String status);

    /**
     * 分页查询薪资记录
     */
    Page<SalaryRecord> searchSalaryRecords(String employeeId, String deptId, String salaryMonth,
                                             String status, Pageable pageable);

    /**
     * 统计某月份的薪资总额
     */
    BigDecimal sumNetSalaryByMonth(String salaryMonth);

    /**
     * 统计员工某时间段内的薪资总额
     */
    BigDecimal sumNetSalaryByEmployeeAndMonthRange(String employeeId, String startMonth, String endMonth);

    /**
     * 审核薪资记录
     */
    SalaryRecord approveSalaryRecord(String salaryRecordId, String approverId, String approverName);

    /**
     * 发放薪资
     */
    SalaryRecord paySalary(String salaryRecordId);

    /**
     * 计算薪资
     */
    SalaryRecord calculateSalary(String employeeId, String salaryMonth);

    /**
     * 批量生成薪资记录
     */
    List<SalaryRecord> batchGenerateSalaryRecords(String salaryMonth, String deptId);

    /**
     * 检查薪资单号是否存在
     */
    boolean existsBySalaryNo(String salaryNo);

    /**
     * 检查员工某月份薪资是否已生成
     */
    boolean existsByEmployeeAndMonth(String employeeId, String salaryMonth);

    /**
     * 生成薪资单号
     */
    String generateSalaryNo();

    /**
     * 计算应发工资
     */
    BigDecimal calculateGrossSalary(SalaryRecord salaryRecord);

    /**
     * 计算扣款合计
     */
    BigDecimal calculateTotalDeduction(SalaryRecord salaryRecord);

    /**
     * 计算实发工资
     */
    BigDecimal calculateNetSalary(SalaryRecord salaryRecord);
}