package com.yhj.his.module.hr.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.enums.EmployeeStatus;

import java.util.List;

/**
 * 员工服务接口
 */
public interface EmployeeService {

    /**
     * 创建员工
     */
    EmployeeVO createEmployee(EmployeeCreateDTO dto);

    /**
     * 更新员工信息
     */
    EmployeeVO updateEmployee(String employeeId, EmployeeUpdateDTO dto);

    /**
     * 删除员工
     */
    void deleteEmployee(String employeeId);

    /**
     * 获取员工详情
     */
    EmployeeVO getEmployeeById(String employeeId);

    /**
     * 根据工号获取员工
     */
    EmployeeVO getEmployeeByNo(String employeeNo);

    /**
     * 分页查询员工
     */
    PageResult<EmployeeVO> listEmployees(String deptId, EmployeeStatus status, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 根据科室查询员工列表
     */
    List<EmployeeVO> listEmployeesByDept(String deptId);

    /**
     * 员工离职
     */
    EmployeeVO resignEmployee(String employeeId, EmployeeResignDTO dto);

    /**
     * 检查工号是否存在
     */
    boolean existsByEmployeeNo(String employeeNo);
}