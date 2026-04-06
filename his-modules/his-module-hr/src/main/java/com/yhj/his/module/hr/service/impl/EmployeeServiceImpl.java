package com.yhj.his.module.hr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Department;
import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.enums.EmployeeStatus;
import com.yhj.his.module.hr.enums.EmploymentType;
import com.yhj.his.module.hr.repository.DepartmentRepository;
import com.yhj.his.module.hr.repository.EmployeeRepository;
import com.yhj.his.module.hr.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工服务实现
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public EmployeeVO createEmployee(EmployeeCreateDTO dto) {
        // 检查工号是否已存在
        if (employeeRepository.existsByEmployeeNo(dto.getEmployeeNo())) {
            throw new BusinessException("员工工号已存在: " + dto.getEmployeeNo());
        }

        // 检查科室是否存在
        Department dept = departmentRepository.findById(dto.getDeptId())
                .orElseThrow(() -> new BusinessException("科室不存在"));

        Employee employee = new Employee();
        BeanUtil.copyProperties(dto, employee);
        employee.setId(IdUtil.fastSimpleUUID());
        employee.setDeptName(dept.getDeptName());

        // 设置默认状态
        if (employee.getStatus() == null) {
            employee.setStatus(EmployeeStatus.ON_JOB);
        }

        // 设置用工类型
        if (dto.getEmploymentType() != null) {
            employee.setEmploymentType(EmploymentType.valueOf(dto.getEmploymentType()));
        }

        employee = employeeRepository.save(employee);
        return convertToVO(employee);
    }

    @Override
    @Transactional
    public EmployeeVO updateEmployee(String employeeId, EmployeeUpdateDTO dto) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));

        BeanUtil.copyProperties(dto, employee, "id", "employeeNo", "createTime", "deleted");

        // 更新科室名称
        if (dto.getDeptId() != null) {
            Department dept = departmentRepository.findById(dto.getDeptId())
                    .orElseThrow(() -> new BusinessException("科室不存在"));
            employee.setDeptName(dept.getDeptName());
        }

        // 更新用工类型
        if (dto.getEmploymentType() != null) {
            employee.setEmploymentType(EmploymentType.valueOf(dto.getEmploymentType()));
        }

        // 更新状态
        if (dto.getStatus() != null) {
            employee.setStatus(EmployeeStatus.valueOf(dto.getStatus()));
        }

        // 计算工龄
        if (employee.getEmploymentDate() != null) {
            int workYears = Period.between(employee.getEmploymentDate(), LocalDate.now()).getYears();
            employee.setWorkYears(workYears);
        }

        employee = employeeRepository.save(employee);
        return convertToVO(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(String employeeId) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));

        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    @Override
    public EmployeeVO getEmployeeById(String employeeId) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));
        return convertToVO(employee);
    }

    @Override
    public EmployeeVO getEmployeeByNo(String employeeNo) {
        Employee employee = employeeRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new BusinessException("员工不存在: " + employeeNo));
        return convertToVO(employee);
    }

    @Override
    public PageResult<EmployeeVO> listEmployees(String deptId, EmployeeStatus status, String keyword, Integer pageNum, Integer pageSize) {
        Page<Employee> page = employeeRepository.findByConditions(deptId, status, keyword, PageUtils.of(pageNum, pageSize));
        List<EmployeeVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public List<EmployeeVO> listEmployeesByDept(String deptId) {
        List<Employee> employees = employeeRepository.findByDeptIdAndDeletedFalse(deptId);
        return employees.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeVO resignEmployee(String employeeId, EmployeeResignDTO dto) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));

        employee.setStatus(EmployeeStatus.RESIGNED);
        employee.setLeaveDate(dto.getLeaveDate() != null ? dto.getLeaveDate() : LocalDate.now());
        employee.setLeaveReason(dto.getLeaveReason());

        employee = employeeRepository.save(employee);
        return convertToVO(employee);
    }

    @Override
    public boolean existsByEmployeeNo(String employeeNo) {
        return employeeRepository.existsByEmployeeNo(employeeNo);
    }

    private EmployeeVO convertToVO(Employee employee) {
        EmployeeVO vo = new EmployeeVO();
        BeanUtil.copyProperties(employee, vo);

        // 设置状态名称
        if (employee.getStatus() != null) {
            vo.setStatus(employee.getStatus().name());
        }

        // 设置用工类型名称
        if (employee.getEmploymentType() != null) {
            vo.setEmploymentType(employee.getEmploymentType().name());
        }

        return vo;
    }
}