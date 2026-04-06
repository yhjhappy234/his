package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.PermissionDTO;
import com.yhj.his.module.system.vo.PermissionVO;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 创建权限
     *
     * @param dto 权限信息
     * @return 权限信息
     */
    Result<PermissionVO> create(PermissionDTO dto);

    /**
     * 更新权限
     *
     * @param dto 权限信息
     * @return 权限信息
     */
    Result<PermissionVO> update(PermissionDTO dto);

    /**
     * 删除权限
     *
     * @param permId 权限ID
     * @return 删除结果
     */
    Result<Void> delete(String permId);

    /**
     * 获取权限详情
     *
     * @param permId 权限ID
     * @return 权限信息
     */
    Result<PermissionVO> getById(String permId);

    /**
     * 分页查询权限
     *
     * @param permName 权限名称
     * @param permCode 权限编码
     * @param permType 权限类型
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 权限分页列表
     */
    Result<PageResult<PermissionVO>> page(String permName, String permCode, String permType,
                                          String status, Integer pageNum, Integer pageSize);

    /**
     * 获取所有权限列表
     *
     * @return 权限列表
     */
    Result<List<PermissionVO>> listAll();

    /**
     * 获取菜单权限树
     *
     * @return 权限树
     */
    Result<List<PermissionVO>> getMenuTree();

    /**
     * 获取用户菜单权限
     *
     * @param userId 用户ID
     * @return 菜单权限树
     */
    Result<List<PermissionVO>> getUserMenus(String userId);

    /**
     * 获取子权限列表
     *
     * @param parentId 父级ID
     * @return 子权限列表
     */
    Result<List<PermissionVO>> listByParentId(String parentId);
}