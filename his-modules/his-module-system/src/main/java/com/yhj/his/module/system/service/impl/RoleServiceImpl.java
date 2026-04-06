package com.yhj.his.module.system.service.impl;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.system.dto.*;
import com.yhj.his.module.system.entity.*;
import com.yhj.his.module.system.enums.DataScopeLevel;
import com.yhj.his.module.system.enums.PermissionType;
import com.yhj.his.module.system.repository.*;
import com.yhj.his.module.system.service.*;
import com.yhj.his.module.system.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public Result<RoleVO> create(RoleDTO dto) {
        // 检查角色编码是否存在
        if (roleRepository.existsByRoleCodeAndDeletedFalse(dto.getRoleCode())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "角色编码已存在");
        }

        Role role = new Role();
        role.setRoleCode(dto.getRoleCode());
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        role.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        role.setIsSystem(dto.getIsSystem() != null ? dto.getIsSystem() : false);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : "NORMAL");
        role.setRemark(dto.getRemark());

        if (dto.getDataScope() != null) {
            role.setDataScope(DataScopeLevel.fromCode(dto.getDataScope()));
        }

        role = roleRepository.save(role);

        // 授权权限
        if (dto.getPermIds() != null && !dto.getPermIds().isEmpty()) {
            authorizeRolePermissions(role.getId(), dto.getPermIds());
        }

        return Result.success("创建成功", convertToVO(role));
    }

    @Override
    @Transactional
    public Result<RoleVO> update(RoleDTO dto) {
        Role role = roleRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "角色不存在"));

        if (role.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "角色已删除");
        }

        // 检查是否为系统角色
        if (role.getIsSystem()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "系统角色不可修改");
        }

        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        role.setSortOrder(dto.getSortOrder());
        role.setRemark(dto.getRemark());

        if (dto.getDataScope() != null) {
            role.setDataScope(DataScopeLevel.fromCode(dto.getDataScope()));
        }

        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }

        role = roleRepository.save(role);

        // 更新权限授权
        if (dto.getPermIds() != null) {
            rolePermissionRepository.deleteByRoleId(role.getId());
            if (!dto.getPermIds().isEmpty()) {
                authorizeRolePermissions(role.getId(), dto.getPermIds());
            }
        }

        return Result.success("更新成功", convertToVO(role));
    }

    @Override
    @Transactional
    public Result<Void> delete(String roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "角色不存在"));

        if (role.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "角色已删除");
        }

        // 检查是否为系统角色
        if (role.getIsSystem()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "系统角色不可删除");
        }

        // 检查是否有用户绑定该角色
        long userCount = userRoleRepository.findByRoleId(roleId).size();
        if (userCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "该角色已授权给" + userCount + "个用户，无法删除");
        }

        // 逻辑删除
        role.setDeleted(true);
        roleRepository.save(role);

        // 删除角色权限关联
        rolePermissionRepository.deleteByRoleId(roleId);

        return Result.successVoid();
    }

    @Override
    public Result<RoleVO> getById(String roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "角色不存在"));

        if (role.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "角色已删除");
        }

        RoleVO vo = convertToVO(role);
        vo.setPermissions(getRolePermissionList(roleId));
        return Result.success(vo);
    }

    @Override
    public Result<PageResult<RoleVO>> page(String roleName, String roleCode, String status,
                                           Integer pageNum, Integer pageSize) {
        Page<Role> page = roleRepository.findByCondition(roleName, roleCode, status,
                PageUtils.of(pageNum, pageSize));

        List<RoleVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<List<RoleVO>> listAll() {
        List<Role> roles = roleRepository.findByDeletedFalseOrderBySortOrderAsc();
        List<RoleVO> list = roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result<Void> authorizePermissions(RoleAuthorizationDTO dto) {
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "角色不存在"));

        // 检查是否为系统角色
        if (role.getIsSystem()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "系统角色权限不可修改");
        }

        // 删除原有权限
        rolePermissionRepository.deleteByRoleId(dto.getRoleId());

        // 授权新权限
        if (dto.getPermIds() != null && !dto.getPermIds().isEmpty()) {
            authorizeRolePermissions(dto.getRoleId(), dto.getPermIds());
        }

        return Result.successVoid();
    }

    @Override
    public Result<List<PermissionVO>> getRolePermissions(String roleId) {
        List<PermissionVO> permissions = getRolePermissionList(roleId);
        return Result.success(permissions);
    }

    @Override
    public Result<List<RoleVO>> getUserRoles(String userId) {
        List<String> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        List<Role> roles = roleRepository.findByIdInAndDeletedFalse(roleIds);
        List<RoleVO> list = roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    /**
     * 授权角色权限
     */
    private void authorizeRolePermissions(String roleId, List<String> permIds) {
        for (String permId : permIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermId(permId);
            rolePermissionRepository.save(rolePermission);
        }
    }

    /**
     * 获取角色权限列表
     */
    private List<PermissionVO> getRolePermissionList(String roleId) {
        List<String> permIds = rolePermissionRepository.findPermIdsByRoleId(roleId);
        if (permIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Permission> permissions = permissionRepository.findByIdInAndDeletedFalse(permIds);
        return permissions.stream()
                .map(this::convertPermissionToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换Role实体到VO
     */
    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleName(role.getRoleName());
        vo.setDescription(role.getDescription());
        vo.setDataScope(role.getDataScope() != null ? role.getDataScope().getCode() : null);
        vo.setSortOrder(role.getSortOrder());
        vo.setIsSystem(role.getIsSystem());
        vo.setStatus(role.getStatus());
        vo.setRemark(role.getRemark());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }

    /**
     * 转换Permission实体到VO
     */
    private PermissionVO convertPermissionToVO(Permission permission) {
        PermissionVO vo = new PermissionVO();
        vo.setId(permission.getId());
        vo.setPermCode(permission.getPermCode());
        vo.setPermName(permission.getPermName());
        vo.setPermType(permission.getPermType() != null ? permission.getPermType().getCode() : null);
        vo.setParentId(permission.getParentId());
        vo.setPath(permission.getPath());
        vo.setIcon(permission.getIcon());
        vo.setSortOrder(permission.getSortOrder());
        vo.setStatus(permission.getStatus());
        vo.setRemark(permission.getRemark());
        vo.setCreateTime(permission.getCreateTime());
        return vo;
    }
}