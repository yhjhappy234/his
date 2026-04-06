package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.RoleAuthorizationDTO;
import com.yhj.his.module.system.dto.RoleDTO;
import com.yhj.his.module.system.vo.PermissionVO;
import com.yhj.his.module.system.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 创建角色
     *
     * @param dto 角色信息
     * @return 角色信息
     */
    Result<RoleVO> create(RoleDTO dto);

    /**
     * 更新角色
     *
     * @param dto 角色信息
     * @return 角色信息
     */
    Result<RoleVO> update(RoleDTO dto);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 删除结果
     */
    Result<Void> delete(String roleId);

    /**
     * 获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    Result<RoleVO> getById(String roleId);

    /**
     * 分页查询角色
     *
     * @param roleName 角色名称
     * @param roleCode 角色编码
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 角色分页列表
     */
    Result<PageResult<RoleVO>> page(String roleName, String roleCode, String status,
                                    Integer pageNum, Integer pageSize);

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    Result<List<RoleVO>> listAll();

    /**
     * 授权角色权限
     *
     * @param dto 授权请求
     * @return 授权结果
     */
    Result<Void> authorizePermissions(RoleAuthorizationDTO dto);

    /**
     * 获取角色权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    Result<List<PermissionVO>> getRolePermissions(String roleId);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    Result<List<RoleVO>> getUserRoles(String userId);
}