package com.yhj.his.module.hr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Department;
import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.enums.DepartmentType;
import com.yhj.his.module.hr.enums.EmployeeStatus;
import com.yhj.his.module.hr.repository.HrDepartmentRepository;
import com.yhj.his.module.hr.repository.EmployeeRepository;
import com.yhj.his.module.hr.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 科室服务实现
 */
@Service("hrDepartmentService")
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final HrDepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public DepartmentVO createDepartment(DepartmentCreateDTO dto) {
        // 检查编码是否已存在
        if (departmentRepository.existsByDeptCode(dto.getDeptCode())) {
            throw new BusinessException("科室编码已存在: " + dto.getDeptCode());
        }

        // 检查名称是否已存在
        if (departmentRepository.existsByDeptName(dto.getDeptName())) {
            throw new BusinessException("科室名称已存在: " + dto.getDeptName());
        }

        Department department = new Department();
        BeanUtil.copyProperties(dto, department);
        department.setId(IdUtil.fastSimpleUUID());

        // 设置科室类型
        if (dto.getDeptType() != null) {
            department.setDeptType(DepartmentType.valueOf(dto.getDeptType()));
        }

        // 设置层级
        if (dto.getParentId() != null) {
            Department parent = departmentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new BusinessException("上级科室不存在"));
            department.setDeptLevel(parent.getDeptLevel() + 1);
            department.setDeptPath(parent.getDeptPath() + "/" + dto.getDeptCode());
        } else {
            department.setDeptLevel(1);
            department.setDeptPath(dto.getDeptCode());
        }

        // 设置默认状态
        if (department.getStatus() == null) {
            department.setStatus("正常");
        }

        department = departmentRepository.save(department);
        return convertToVO(department);
    }

    @Override
    @Transactional
    public DepartmentVO updateDepartment(String deptId, DepartmentCreateDTO dto) {
        Department department = departmentRepository.findByIdAndDeletedFalse(deptId)
                .orElseThrow(() -> new BusinessException("科室不存在"));

        // 检查编码是否重复（排除自身）
        if (!department.getDeptCode().equals(dto.getDeptCode())
                && departmentRepository.existsByDeptCode(dto.getDeptCode())) {
            throw new BusinessException("科室编码已存在: " + dto.getDeptCode());
        }

        // 检查名称是否重复（排除自身）
        if (!department.getDeptName().equals(dto.getDeptName())
                && departmentRepository.existsByDeptName(dto.getDeptName())) {
            throw new BusinessException("科室名称已存在: " + dto.getDeptName());
        }

        BeanUtil.copyProperties(dto, department, "id", "createTime", "deleted");

        // 设置科室类型
        if (dto.getDeptType() != null) {
            department.setDeptType(DepartmentType.valueOf(dto.getDeptType()));
        }

        department = departmentRepository.save(department);
        return convertToVO(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(String deptId) {
        Department department = departmentRepository.findByIdAndDeletedFalse(deptId)
                .orElseThrow(() -> new BusinessException("科室不存在"));

        // 检查是否有子科室
        List<Department> children = departmentRepository.findByParentIdAndDeletedFalse(deptId);
        if (!children.isEmpty()) {
            throw new BusinessException("科室存在子科室，不能删除");
        }

        // 检查是否有员工
        long employeeCount = employeeRepository.countByDeptIdAndDeletedFalse(deptId);
        if (employeeCount > 0) {
            throw new BusinessException("科室存在员工，不能删除");
        }

        department.setDeleted(true);
        departmentRepository.save(department);
    }

    @Override
    public DepartmentVO getDepartmentById(String deptId) {
        Department department = departmentRepository.findByIdAndDeletedFalse(deptId)
                .orElseThrow(() -> new BusinessException("科室不存在"));
        return convertToVO(department);
    }

    @Override
    public DepartmentVO getDepartmentByCode(String deptCode) {
        Department department = departmentRepository.findByDeptCode(deptCode)
                .orElseThrow(() -> new BusinessException("科室不存在: " + deptCode));
        return convertToVO(department);
    }

    @Override
    public PageResult<DepartmentVO> listDepartments(DepartmentType deptType, String status, String keyword, Integer pageNum, Integer pageSize) {
        Page<Department> page = departmentRepository.findByConditions(deptType, status, keyword, PageUtils.of(pageNum, pageSize));
        List<DepartmentVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public List<DepartmentVO> getDepartmentTree() {
        List<Department> allDepts = departmentRepository.findAllOrderByLevelAndSort();

        // 按层级分组
        Map<Integer, List<Department>> levelMap = allDepts.stream()
                .collect(Collectors.groupingBy(Department::getDeptLevel));

        // 构建树形结构
        List<DepartmentVO> rootDepts = levelMap.getOrDefault(1, new ArrayList<>())
                .stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 递归设置子科室
        buildTree(rootDepts, levelMap);

        return rootDepts;
    }

    @Override
    public List<DepartmentVO> listChildDepartments(String parentId) {
        List<Department> children = departmentRepository.findByParentIdAndDeletedFalse(parentId);
        return children.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<DepartmentVO> listDepartmentsByType(DepartmentType deptType) {
        List<Department> departments = departmentRepository.findByDeptTypeAndDeletedFalse(deptType);
        return departments.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private void buildTree(List<DepartmentVO> parents, Map<Integer, List<Department>> levelMap) {
        if (parents == null || parents.isEmpty()) {
            return;
        }

        for (DepartmentVO parent : parents) {
            List<Department> children = levelMap.getOrDefault(parent.getDeptLevel() + 1, new ArrayList<>())
                    .stream()
                    .filter(d -> parent.getId().equals(d.getParentId()))
                    .collect(Collectors.toList());

            List<DepartmentVO> childVOs = children.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());

            buildTree(childVOs, levelMap);
        }
    }

    private DepartmentVO convertToVO(Department department) {
        DepartmentVO vo = new DepartmentVO();
        BeanUtil.copyProperties(department, vo);

        // 设置科室类型名称
        if (department.getDeptType() != null) {
            vo.setDeptType(department.getDeptType().name());
        }

        // 统计员工数量
        long employeeCount = employeeRepository.countByDeptIdAndDeletedFalse(department.getId());
        vo.setEmployeeCount(employeeCount);

        // 设置上级科室名称
        if (department.getParentId() != null) {
            departmentRepository.findById(department.getParentId())
                    .ifPresent(parent -> vo.setParentName(parent.getDeptName()));
        }

        return vo;
    }
}