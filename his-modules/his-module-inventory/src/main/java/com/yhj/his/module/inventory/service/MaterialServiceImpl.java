package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inventory.dto.MaterialDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.entity.Material;
import com.yhj.his.module.inventory.entity.MaterialCategory;
import com.yhj.his.module.inventory.enums.MaterialStatus;
import com.yhj.his.module.inventory.repository.MaterialCategoryRepository;
import com.yhj.his.module.inventory.repository.MaterialInventoryRepository;
import com.yhj.his.module.inventory.repository.MaterialRepository;
import com.yhj.his.module.inventory.vo.MaterialVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物资信息服务实现
 */
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository repository;
    private final MaterialCategoryRepository categoryRepository;
    private final MaterialInventoryRepository inventoryRepository;

    @Override
    @Transactional
    public MaterialVO create(MaterialDTO dto) {
        // 检查编码是否存在
        if (repository.existsByMaterialCodeAndDeletedFalse(dto.getMaterialCode())) {
            throw new BusinessException("物资编码已存在: " + dto.getMaterialCode());
        }

        Material entity = new Material();
        entity.setMaterialCode(dto.getMaterialCode());
        entity.setMaterialName(dto.getMaterialName());
        entity.setCategoryId(dto.getCategoryId());
        entity.setMaterialSpec(dto.getMaterialSpec());
        entity.setMaterialUnit(dto.getMaterialUnit());
        entity.setManufacturer(dto.getManufacturer());
        entity.setBrand(dto.getBrand());
        entity.setOrigin(dto.getOrigin());
        entity.setPurchasePrice(dto.getPurchasePrice());
        entity.setRetailPrice(dto.getRetailPrice());
        entity.setMinStock(dto.getMinStock());
        entity.setMaxStock(dto.getMaxStock());
        entity.setSafetyStock(dto.getSafetyStock());
        entity.setShelfLife(dto.getShelfLife());
        entity.setStorageCondition(dto.getStorageCondition());
        entity.setIsMedical(dto.getIsMedical() != null ? dto.getIsMedical() : false);
        entity.setIsSterile(dto.getIsSterile() != null ? dto.getIsSterile() : false);
        entity.setIsReusable(dto.getIsReusable() != null ? dto.getIsReusable() : false);
        entity.setRemark(dto.getRemark());

        // 设置分类名称
        if (dto.getCategoryId() != null) {
            MaterialCategory category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            if (category != null) {
                entity.setCategoryName(category.getCategoryName());
            }
        }

        // 设置状态
        if (dto.getStatus() != null) {
            entity.setStatus(MaterialStatus.valueOf(dto.getStatus()));
        } else {
            entity.setStatus(MaterialStatus.NORMAL);
        }

        entity = repository.save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public MaterialVO update(String id, MaterialDTO dto) {
        Material entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("物资不存在"));

        // 检查编码是否重复
        if (!entity.getMaterialCode().equals(dto.getMaterialCode())) {
            if (repository.existsByMaterialCodeAndDeletedFalse(dto.getMaterialCode())) {
                throw new BusinessException("物资编码已存在: " + dto.getMaterialCode());
            }
        }

        entity.setMaterialCode(dto.getMaterialCode());
        entity.setMaterialName(dto.getMaterialName());
        entity.setCategoryId(dto.getCategoryId());
        entity.setMaterialSpec(dto.getMaterialSpec());
        entity.setMaterialUnit(dto.getMaterialUnit());
        entity.setManufacturer(dto.getManufacturer());
        entity.setBrand(dto.getBrand());
        entity.setOrigin(dto.getOrigin());
        entity.setPurchasePrice(dto.getPurchasePrice());
        entity.setRetailPrice(dto.getRetailPrice());
        entity.setMinStock(dto.getMinStock());
        entity.setMaxStock(dto.getMaxStock());
        entity.setSafetyStock(dto.getSafetyStock());
        entity.setShelfLife(dto.getShelfLife());
        entity.setStorageCondition(dto.getStorageCondition());
        entity.setIsMedical(dto.getIsMedical());
        entity.setIsSterile(dto.getIsSterile());
        entity.setIsReusable(dto.getIsReusable());
        entity.setRemark(dto.getRemark());

        // 更新分类名称
        if (dto.getCategoryId() != null) {
            MaterialCategory category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            if (category != null) {
                entity.setCategoryName(category.getCategoryName());
            }
        }

        if (dto.getStatus() != null) {
            entity.setStatus(MaterialStatus.valueOf(dto.getStatus()));
        }

        entity = repository.save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Material entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("物资不存在"));
        entity.setDeleted(true);
        repository.save(entity);
    }

    @Override
    public MaterialVO getById(String id) {
        Material entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("物资不存在"));
        MaterialVO vo = toVO(entity);
        // 查询库存总量
        BigDecimal stock = inventoryRepository.sumQuantityByMaterialId(id);
        vo.setCurrentStock(stock != null ? stock : BigDecimal.ZERO);
        return vo;
    }

    @Override
    public MaterialVO getByCode(String code) {
        Material entity = repository.findByMaterialCode(code)
                .orElseThrow(() -> new BusinessException("物资不存在"));
        return toVO(entity);
    }

    @Override
    public PageResult<MaterialVO> list(Integer pageNum, Integer pageSize) {
        Page<Material> page = repository.findByDeletedFalse(PageRequest.of(pageNum - 1, pageSize));
        List<MaterialVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public PageResult<MaterialVO> query(QueryDTO query) {
        Page<Material> page = repository.search(query.getKeyword(),
                PageRequest.of(query.getPageNum() - 1, query.getPageSize()));
        List<MaterialVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public List<MaterialVO> listByCategory(String categoryId) {
        List<Material> list = repository.findByCategoryIdAndDeletedFalse(categoryId);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<MaterialVO> search(String keyword, Integer pageNum, Integer pageSize) {
        Page<Material> page = repository.search(keyword, PageRequest.of(pageNum - 1, pageSize));
        List<MaterialVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    private MaterialVO toVO(Material entity) {
        MaterialVO vo = new MaterialVO();
        vo.setId(entity.getId());
        vo.setMaterialCode(entity.getMaterialCode());
        vo.setMaterialName(entity.getMaterialName());
        vo.setCategoryId(entity.getCategoryId());
        vo.setCategoryName(entity.getCategoryName());
        vo.setMaterialSpec(entity.getMaterialSpec());
        vo.setMaterialUnit(entity.getMaterialUnit());
        vo.setManufacturer(entity.getManufacturer());
        vo.setBrand(entity.getBrand());
        vo.setOrigin(entity.getOrigin());
        vo.setPurchasePrice(entity.getPurchasePrice());
        vo.setRetailPrice(entity.getRetailPrice());
        vo.setPriceDate(entity.getPriceDate());
        vo.setMinStock(entity.getMinStock());
        vo.setMaxStock(entity.getMaxStock());
        vo.setSafetyStock(entity.getSafetyStock());
        vo.setShelfLife(entity.getShelfLife());
        vo.setStorageCondition(entity.getStorageCondition());
        vo.setIsMedical(entity.getIsMedical());
        vo.setIsSterile(entity.getIsSterile());
        vo.setIsReusable(entity.getIsReusable());
        vo.setStatus(entity.getStatus().getName());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}