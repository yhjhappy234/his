package com.yhj.his.module.system.service.impl;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.system.dto.PermissionDTO;
import com.yhj.his.module.system.entity.Permission;
import com.yhj.his.module.system.enums.PermissionType;
import com.yhj.his.module.system.repository.PermissionRepository;
import com.yhj.his.module.system.repository.RolePermissionRepository;
import com.yhj.his.module.system.service.PermissionService;
import com.yhj.his.module.system.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public Result<PermissionVO> create(PermissionDTO dto) {
        // 检查权限编码是否存在
        if (permissionRepository.existsByPermCodeAndDeletedFalse(dto.getPermCode())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "权限编码已存在");
        }

        Permission permission = new Permission();
        permission.setPermCode(dto.getPermCode());
        permission.setPermName(dto.getPermName());
        permission.setPermType(PermissionType.fromCode(dto.getPermType()));
        permission.setParentId(dto.getParentId());
        permission.setPath(dto.getPath());
        permission.setIcon(dto.getIcon());
        permission.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        permission.setStatus(dto.getStatus() != null ? dto.getStatus() : "NORMAL");
        permission.setRemark(dto.getRemark());

        permission = permissionRepository.save(permission);
        return Result.success("创建成功", convertToVO(permission));
    }

    @Override
    @Transactional
    public Result<PermissionVO> update(PermissionDTO dto) {
        Permission permission = permissionRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "权限不存在"));

        if (permission.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "权限已删除");
        }

        permission.setPermName(dto.getPermName());
        permission.setPermType(PermissionType.fromCode(dto.getPermType()));
        permission.setParentId(dto.getParentId());
        permission.setPath(dto.getPath());
        permission.setIcon(dto.getIcon());
        permission.setSortOrder(dto.getSortOrder());
        permission.setStatus(dto.getStatus());
        permission.setRemark(dto.getRemark());

        permission = permissionRepository.save(permission);
        return Result.success("更新成功", convertToVO(permission));
    }

    @Override
    @Transactional
    public Result<Void> delete(String permId) {
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "权限不存在"));

        if (permission.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "权限已删除");
        }

        // 检查是否有子权限
        List<Permission> children = permissionRepository.findByParentIdAndDeletedFalseOrderBySortOrderAsc(permId);
        if (!children.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "该权限有" + children.size() + "个子权限，无法删除");
        }

        // 检查是否有角色绑定该权限
        long roleCount = rolePermissionRepository.findByPermId(permId).size();
        if (roleCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "该权限已授权给" + roleCount + "个角色，无法删除");
        }

        // 逻辑删除
        permission.setDeleted(true);
        permissionRepository.save(permission);

        return Result.successVoid();
    }

    @Override
    public Result<PermissionVO> getById(String permId) {
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "权限不存在"));

        if (permission.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "权限已删除");
        }

        return Result.success(convertToVO(permission));
    }

    @Override
    public Result<PageResult<PermissionVO>> page(String permName, String permCode, String permType,
                                                 String status, Integer pageNum, Integer pageSize) {
        PermissionType type = permType != null ? PermissionType.fromCode(permType) : null;
        Page<Permission> page = permissionRepository.findByCondition(permName, permCode, type, status,
                PageUtils.of(pageNum, pageSize));

        List<PermissionVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<List<PermissionVO>> listAll() {
        List<Permission> permissions = permissionRepository.findByDeletedFalseOrderBySortOrderAsc();
        List<PermissionVO> list = permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<PermissionVO>> getMenuTree() {
        List<Permission> menus = permissionRepository.findByPermTypeAndDeletedFalseOrderBySortOrderAsc(PermissionType.MENU);
        List<PermissionVO> tree = buildTree(menus, null);
        return Result.success(tree);
    }

    @Override
    public Result<List<PermissionVO>> getUserMenus(String userId) {
        // TODO: 需要根据用户角色获取对应的菜单权限
        // 这里简化实现，返回所有菜单
        return getMenuTree();
    }

    @Override
    public Result<List<PermissionVO>> listByParentId(String parentId) {
        List<Permission> permissions = permissionRepository.findByParentIdAndDeletedFalseOrderBySortOrderAsc(parentId);
        List<PermissionVO> list = permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    /**
     * 构建权限树
     */
    private List<PermissionVO> buildTree(List<Permission> permissions, String parentId) {
        List<PermissionVO> tree = new ArrayList<>();

        for (Permission permission : permissions) {
            String permParentId = permission.getParentId();
            boolean matchParent = (parentId == null && permParentId == null) ||
                                  (parentId != null && parentId.equals(permParentId));

            if (matchParent) {
                PermissionVO vo = convertToVO(permission);
                // 递归构建子权限
                List<PermissionVO> children = buildTree(permissions, permission.getId());
                if (!children.isEmpty()) {
                    vo.setChildren(children);
                }
                tree.add(vo);
            }
        }

        return tree.stream()
                .sorted(Comparator.comparing(PermissionVO::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * 转换Permission实体到VO
     */
    private PermissionVO convertToVO(Permission permission) {
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