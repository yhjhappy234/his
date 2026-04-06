package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.*;
import com.yhj.his.module.system.vo.LoginVO;
import com.yhj.his.module.system.vo.UserPermissionVO;
import com.yhj.his.module.system.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param dto 登录请求
     * @param clientIp 客户端IP
     * @return 登录结果
     */
    Result<LoginVO> login(LoginDTO dto, String clientIp);

    /**
     * 用户退出
     *
     * @param userId 用户ID
     * @return 退出结果
     */
    Result<Void> logout(String userId);

    /**
     * 创建用户
     *
     * @param dto 用户信息
     * @return 用户信息
     */
    Result<UserVO> create(UserDTO dto);

    /**
     * 更新用户
     *
     * @param dto 用户信息
     * @return 用户信息
     */
    Result<UserVO> update(UserDTO dto);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    Result<Void> delete(String userId);

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    Result<UserVO> getById(String userId);

    /**
     * 分页查询用户
     *
     * @param userName 用户名
     * @param loginName 登录账号
     * @param deptId 科室ID
     * @param status 状态
     * @param phone 电话
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用户分页列表
     */
    Result<PageResult<UserVO>> page(String userName, String loginName, String deptId,
                                    String status, String phone, Integer pageNum, Integer pageSize);

    /**
     * 获取用户权限
     *
     * @param userId 用户ID
     * @return 用户权限信息
     */
    Result<UserPermissionVO> getUserPermission(String userId);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto 密码修改请求
     * @return 修改结果
     */
    Result<Void> changePassword(String userId, PasswordChangeDTO dto);

    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 重置结果
     */
    Result<Void> resetPassword(String userId, String newPassword);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 更新结果
     */
    Result<Void> updateStatus(String userId, String status);

    /**
     * 授权用户角色
     *
     * @param dto 授权请求
     * @return 授权结果
     */
    Result<Void> authorizeRoles(UserAuthorizationDTO dto);

    /**
     * 获取科室用户列表
     *
     * @param deptId 科室ID
     * @return 用户列表
     */
    Result<List<UserVO>> listByDeptId(String deptId);
}