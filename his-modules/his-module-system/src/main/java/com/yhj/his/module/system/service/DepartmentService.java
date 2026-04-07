package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.DepartmentDTO;
import com.yhj.his.module.system.vo.DepartmentVO;

import java.util.List;

/**
 * 科室服务接口
 */
public interface DepartmentService {

    /**
     * 创建科室
     *
     * @param dto 科室信息
     * @return 科室信息
     */
    Result<DepartmentVO> create(DepartmentDTO dto);

    /**
     * 更新科室
     *
     * @param dto 科室信息
     * @return 科室信息
     */
    Result<DepartmentVO> update(DepartmentDTO dto);

    /**
     * 删除科室
     *
     * @param deptId 科室ID
     * @return 删除结果
     */
    Result<Void> delete(String deptId);

    /**
     * 获取科室详情
     *
     * @param deptId 科室ID
     * @return 科室信息
     */
    Result<DepartmentVO> getById(String deptId);

    /**
     * 分页查询科室
     *
     * @param deptName 科室名称
     * @param deptCode 科室编码
     * @param deptType 科室类型
     * @param status 状态
     * @param parentId 父级ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 科室分页列表
     */
    Result<PageResult<DepartmentVO>> page(String deptName, String deptCode, String deptType,
                                          String status, String parentId, Integer pageNum, Integer pageSize);

    /**
     * 获取所有科室列表
     *
     * @return 科室列表
     */
    Result<List<DepartmentVO>> listAll();

    /**
     * 获取科室树
     *
     * @return 科室树
     */
    Result<List<DepartmentVO>> getTree();

    /**
     * 获取子科室列表
     *
     * @param parentId 父级ID
     * @return 子科室列表
     */
    Result<List<DepartmentVO>> listByParentId(String parentId);

    /**
     * 根据科室类型查询
     *
     * @param deptType 科室类型
     * @return 科室列表
     */
    Result<List<DepartmentVO>> listByType(String deptType);
}