package com.yhj.his.module.hr.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.SalaryRecord;

import java.math.BigDecimal;
import java.util.List;

/**
 * 薪资服务接口
 */
public interface SalaryService {

    /**
     * 计算薪资
     */
    List<SalaryRecordVO> calculateSalary(SalaryCalculateDTO dto, String calculatorId, String calculatorName);

    /**
     * 审核薪资
     */
    SalaryRecordVO approveSalary(SalaryApprovalDTO dto, String approverId, String approverName);

    /**
     * 发放薪资
     */
    SalaryRecordVO paySalary(String salaryId);

    /**
     * 获取薪资记录详情
     */
    SalaryRecordVO getSalaryRecordById(String salaryId);

    /**
     * 根据员工和月份获取薪资记录
     */
    SalaryRecordVO getSalaryRecordByEmployeeAndMonth(String employeeId, String salaryMonth);

    /**
     * 根据员工获取薪资记录列表
     */
    List<SalaryRecordVO> listSalaryRecordsByEmployee(String employeeId);

    /**
     * 分页查询薪资记录
     */
    PageResult<SalaryRecordVO> listSalaryRecords(String employeeId, String deptId, String salaryMonth, String status, Integer pageNum, Integer pageSize);

    /**
     * 计算员工某月份薪资
     */
    SalaryRecordVO calculateEmployeeSalary(String employeeId, String salaryMonth);

    /**
     * 统计某月份薪资总额
     */
    BigDecimal sumSalaryByMonth(String salaryMonth);
}