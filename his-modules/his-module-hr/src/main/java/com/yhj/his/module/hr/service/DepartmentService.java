package com.yhj.his.module.hr.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Department;
import com.yhj.his.module.hr.enums.DepartmentType;

import java.util.List;

/**
 * 科室服务接口
 */
public interface DepartmentService {

    /**
     * 创建科室
     */
    DepartmentVO createDepartment(DepartmentCreateDTO dto);

    /**
     * 更新科室
     */
    DepartmentVO updateDepartment(String deptId, DepartmentCreateDTO dto);

    /**
     * 删除科室
     */
    void deleteDepartment(String deptId);

    /**
     * 获取科室详情
     */
    DepartmentVO getDepartmentById(String deptId);

    /**
     * 根据编码获取科室
     */
    DepartmentVO getDepartmentByCode(String deptCode);

    /**
     * 分页查询科室
     */
    PageResult<DepartmentVO> listDepartments(DepartmentType deptType, String status, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取科室树形结构
     */
    List<DepartmentVO> getDepartmentTree();

    /**
     * 获取子科室列表
     */
    List<DepartmentVO> listChildDepartments(String parentId);

    /**
     * 根据类型获取科室列表
     */
    List<DepartmentVO> listDepartmentsByType(DepartmentType deptType);
}