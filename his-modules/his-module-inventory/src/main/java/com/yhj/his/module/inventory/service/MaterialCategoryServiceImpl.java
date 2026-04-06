package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.inventory.dto.MaterialCategoryDTO;
import com.yhj.his.module.inventory.entity.MaterialCategory;
import com.yhj.his.module.inventory.repository.MaterialCategoryRepository;
import com.yhj.his.module.inventory.vo.MaterialCategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物资分类服务实现
 */
@Service
@RequiredArgsConstructor
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    private final MaterialCategoryRepository repository;

    @Override
    @Transactional
    public MaterialCategoryVO create(MaterialCategoryDTO dto) {
        // 检查编码是否存在
        if (repository.existsByCategoryCodeAndDeletedFalse(dto.getCategoryCode())) {
            throw new BusinessException("分类编码已存在: " + dto.getCategoryCode());
        }

        MaterialCategory entity = new MaterialCategory();
        entity.setCategoryCode(dto.getCategoryCode());
        entity.setCategoryName(dto.getCategoryName());
        entity.setParentId(dto.getParentId());
        entity.setLevel(dto.getLevel() != null ? dto.getLevel() : 1);
        entity.setSortOrder(dto.getSortOrder());
        entity.setRemark(dto.getRemark());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        // 设置层级
        if (dto.getParentId() != null) {
            MaterialCategory parent = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new BusinessException("父分类不存在"));
            entity.setLevel(parent.getLevel() + 1);
        }

        entity = repository.save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public MaterialCategoryVO update(String id, MaterialCategoryDTO dto) {
        MaterialCategory entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));

        // 检查编码是否重复
        if (!entity.getCategoryCode().equals(dto.getCategoryCode())) {
            if (repository.existsByCategoryCodeAndDeletedFalse(dto.getCategoryCode())) {
                throw new BusinessException("分类编码已存在: " + dto.getCategoryCode());
            }
        }

        entity.setCategoryCode(dto.getCategoryCode());
        entity.setCategoryName(dto.getCategoryName());
        entity.setParentId(dto.getParentId());
        entity.setSortOrder(dto.getSortOrder());
        entity.setRemark(dto.getRemark());
        entity.setStatus(dto.getStatus());

        entity = repository.save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        // 检查是否有子分类
        List<MaterialCategory> children = repository.findByParentIdAndDeletedFalse(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子分类，无法删除");
        }

        MaterialCategory entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        entity.setDeleted(true);
        repository.save(entity);
    }

    @Override
    public MaterialCategoryVO getById(String id) {
        MaterialCategory entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        return toVO(entity);
    }

    @Override
    public MaterialCategoryVO getByCode(String code) {
        MaterialCategory entity = repository.findByCategoryCode(code)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        return toVO(entity);
    }

    @Override
    public PageResult<MaterialCategoryVO> list(Integer pageNum, Integer pageSize) {
        Page<MaterialCategory> page = repository.findByDeletedFalse(PageRequest.of(pageNum - 1, pageSize));
        List<MaterialCategoryVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public List<MaterialCategoryVO> tree() {
        List<MaterialCategory> all = repository.findAllOrderByLevelAndSortOrder();
        return buildTree(all);
    }

    @Override
    public List<MaterialCategoryVO> getChildren(String parentId) {
        List<MaterialCategory> children = repository.findByParentIdAndDeletedFalse(parentId);
        return children.stream().map(this::toVO).collect(Collectors.toList());
    }

    private MaterialCategoryVO toVO(MaterialCategory entity) {
        MaterialCategoryVO vo = new MaterialCategoryVO();
        vo.setId(entity.getId());
        vo.setCategoryCode(entity.getCategoryCode());
        vo.setCategoryName(entity.getCategoryName());
        vo.setParentId(entity.getParentId());
        vo.setLevel(entity.getLevel());
        vo.setSortOrder(entity.getSortOrder());
        vo.setRemark(entity.getRemark());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    private List<MaterialCategoryVO> buildTree(List<MaterialCategory> all) {
        Map<String, List<MaterialCategoryVO>> parentMap = all.stream()
                .filter(c -> c.getParentId() != null)
                .map(this::toVO)
                .collect(Collectors.groupingBy(MaterialCategoryVO::getParentId));

        List<MaterialCategoryVO> roots = all.stream()
                .filter(c -> c.getParentId() == null)
                .map(this::toVO)
                .collect(Collectors.toList());

        for (MaterialCategoryVO root : roots) {
            root.setChildren(parentMap.getOrDefault(root.getId(), new ArrayList<>()));
            fillChildren(root, parentMap);
        }
        return roots;
    }

    private void fillChildren(MaterialCategoryVO parent, Map<String, List<MaterialCategoryVO>> parentMap) {
        List<MaterialCategoryVO> children = parent.getChildren();
        if (children != null) {
            for (MaterialCategoryVO child : children) {
                child.setChildren(parentMap.getOrDefault(child.getId(), new ArrayList<>()));
                fillChildren(child, parentMap);
            }
        }
    }
}