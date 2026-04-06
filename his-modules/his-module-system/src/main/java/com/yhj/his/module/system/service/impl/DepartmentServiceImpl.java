package com.yhj.his.module.system.service.impl;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.system.dto.DepartmentDTO;
import com.yhj.his.module.system.entity.Department;
import com.yhj.his.module.system.repository.DepartmentRepository;
import com.yhj.his.module.system.repository.UserRepository;
import com.yhj.his.module.system.service.DepartmentService;
import com.yhj.his.module.system.vo.DepartmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 科室服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Result<DepartmentVO> create(DepartmentDTO dto) {
        // 检查科室编码是否存在
        if (dto.getDeptCode() != null && departmentRepository.existsByDeptCodeAndDeletedFalse(dto.getDeptCode())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "科室编码已存在");
        }

        Department dept = new Department();
        dept.setDeptCode(dto.getDeptCode());
        dept.setDeptName(dto.getDeptName());
        dept.setShortName(dto.getShortName());
        dept.setParentId(dto.getParentId());
        dept.setDeptType(dto.getDeptType());
        dept.setLeaderId(dto.getLeaderId());
        dept.setLeaderName(dto.getLeaderName());
        dept.setPhone(dto.getPhone());
        dept.setAddress(dto.getAddress());
        dept.setRemark(dto.getRemark());

        // 计算层级
        if (dto.getParentId() != null) {
            Department parent = departmentRepository.findById(dto.getParentId()).orElse(null);
            if (parent != null) {
                dept.setDeptLevel(parent.getDeptLevel() + 1);
            } else {
                dept.setDeptLevel(1);
            }
        } else {
            dept.setDeptLevel(dto.getDeptLevel() != null ? dto.getDeptLevel() : 1);
        }

        dept.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        dept.setStatus(dto.getStatus() != null ? dto.getStatus() : "NORMAL");

        dept = departmentRepository.save(dept);
        return Result.success("创建成功", convertToVO(dept));
    }

    @Override
    @Transactional
    public Result<DepartmentVO> update(DepartmentDTO dto) {
        Department dept = departmentRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "科室不存在"));

        if (dept.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "科室已删除");
        }

        dept.setDeptName(dto.getDeptName());
        dept.setShortName(dto.getShortName());
        dept.setDeptType(dto.getDeptType());
        dept.setLeaderId(dto.getLeaderId());
        dept.setLeaderName(dto.getLeaderName());
        dept.setPhone(dto.getPhone());
        dept.setAddress(dto.getAddress());
        dept.setSortOrder(dto.getSortOrder());
        dept.setStatus(dto.getStatus());
        dept.setRemark(dto.getRemark());

        // 更新父级关系
        if (dto.getParentId() != null && !dto.getParentId().equals(dept.getParentId())) {
            // 检查是否将科室设为自己的子科室
            if (dto.getParentId().equals(dto.getId())) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "不能将科室设为自己的子科室");
            }

            Department parent = departmentRepository.findById(dto.getParentId()).orElse(null);
            if (parent != null) {
                dept.setParentId(dto.getParentId());
                dept.setDeptLevel(parent.getDeptLevel() + 1);
            }
        }

        dept = departmentRepository.save(dept);
        return Result.success("更新成功", convertToVO(dept));
    }

    @Override
    @Transactional
    public Result<Void> delete(String deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "科室不存在"));

        if (dept.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "科室已删除");
        }

        // 检查是否有子科室
        long childCount = departmentRepository.countByParentIdAndDeletedFalse(deptId);
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "该科室有" + childCount + "个子科室，无法删除");
        }

        // 检查是否有用户绑定该科室
        long userCount = userRepository.countByDeptIdAndDeletedFalse(deptId);
        if (userCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "该科室有" + userCount + "个用户，无法删除");
        }

        // 逻辑删除
        dept.setDeleted(true);
        departmentRepository.save(dept);

        return Result.successVoid();
    }

    @Override
    public Result<DepartmentVO> getById(String deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "科室不存在"));

        if (dept.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "科室已删除");
        }

        return Result.success(convertToVO(dept));
    }

    @Override
    public Result<PageResult<DepartmentVO>> page(String deptName, String deptCode, String deptType,
                                                  String status, String parentId, Integer pageNum, Integer pageSize) {
        Page<Department> page = departmentRepository.findByCondition(deptName, deptCode, deptType, status, parentId,
                PageUtils.of(pageNum, pageSize));

        List<DepartmentVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<List<DepartmentVO>> listAll() {
        List<Department> depts = departmentRepository.findByDeletedFalseOrderBySortOrderAsc();
        List<DepartmentVO> list = depts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<DepartmentVO>> getTree() {
        List<Department> depts = departmentRepository.findByDeletedFalseOrderBySortOrderAsc();
        List<DepartmentVO> tree = buildTree(depts, null);
        return Result.success(tree);
    }

    @Override
    public Result<List<DepartmentVO>> listByParentId(String parentId) {
        List<Department> depts = departmentRepository.findByParentIdAndDeletedFalseOrderBySortOrderAsc(parentId);
        List<DepartmentVO> list = depts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<DepartmentVO>> listByType(String deptType) {
        List<Department> depts = departmentRepository.findByDeptTypeAndDeletedFalseOrderBySortOrderAsc(deptType);
        List<DepartmentVO> list = depts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    /**
     * 构建科室树
     */
    private List<DepartmentVO> buildTree(List<Department> depts, String parentId) {
        List<DepartmentVO> tree = new ArrayList<>();

        for (Department dept : depts) {
            String deptParentId = dept.getParentId();
            boolean matchParent = (parentId == null && deptParentId == null) ||
                                  (parentId != null && parentId.equals(deptParentId));

            if (matchParent) {
                DepartmentVO vo = convertToVO(dept);
                // 递归构建子科室
                List<DepartmentVO> children = buildTree(depts, dept.getId());
                if (!children.isEmpty()) {
                    vo.setChildren(children);
                }
                tree.add(vo);
            }
        }

        return tree.stream()
                .sorted(Comparator.comparing(DepartmentVO::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * 转换Department实体到VO
     */
    private DepartmentVO convertToVO(Department dept) {
        DepartmentVO vo = new DepartmentVO();
        vo.setId(dept.getId());
        vo.setDeptCode(dept.getDeptCode());
        vo.setDeptName(dept.getDeptName());
        vo.setShortName(dept.getShortName());
        vo.setParentId(dept.getParentId());
        vo.setDeptLevel(dept.getDeptLevel());
        vo.setDeptType(dept.getDeptType());
        vo.setLeaderId(dept.getLeaderId());
        vo.setLeaderName(dept.getLeaderName());
        vo.setPhone(dept.getPhone());
        vo.setAddress(dept.getAddress());
        vo.setSortOrder(dept.getSortOrder());
        vo.setStatus(dept.getStatus());
        vo.setRemark(dept.getRemark());
        vo.setCreateTime(dept.getCreateTime());
        return vo;
    }
}