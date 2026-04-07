package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.dto.WarehouseDTO;
import com.yhj.his.module.inventory.entity.Warehouse;
import com.yhj.his.module.inventory.enums.WarehouseType;
import com.yhj.his.module.inventory.repository.WarehouseRepository;
import com.yhj.his.module.inventory.vo.WarehouseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 库房信息服务实现
 */
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository repository;

    @Override
    @Transactional
    public WarehouseVO create(WarehouseDTO dto) {
        // 检查编码是否存在
        if (repository.existsByWarehouseCodeAndDeletedFalse(dto.getWarehouseCode())) {
            throw new BusinessException("库房编码已存在: " + dto.getWarehouseCode());
        }

        Warehouse entity = new Warehouse();
        entity.setWarehouseCode(dto.getWarehouseCode());
        entity.setWarehouseName(dto.getWarehouseName());
        entity.setDeptId(dto.getDeptId());
        entity.setDeptName(dto.getDeptName());
        entity.setLocation(dto.getLocation());
        entity.setManagerId(dto.getManagerId());
        entity.setManagerName(dto.getManagerName());
        entity.setPhone(dto.getPhone());
        entity.setRemark(dto.getRemark());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        // 设置库房类型
        if (dto.getWarehouseType() != null) {
            entity.setWarehouseType(WarehouseType.valueOf(dto.getWarehouseType()));
        }

        entity = repository.save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public WarehouseVO update(String id, WarehouseDTO dto) {
        Warehouse entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("库房不存在"));

        // 检查编码是否重复
        if (!entity.getWarehouseCode().equals(dto.getWarehouseCode())) {
            if (repository.existsByWarehouseCodeAndDeletedFalse(dto.getWarehouseCode())) {
                throw new BusinessException("库房编码已存在: " + dto.getWarehouseCode());
            }
        }

        entity.setWarehouseCode(dto.getWarehouseCode());
        entity.setWarehouseName(dto.getWarehouseName());
        entity.setDeptId(dto.getDeptId());
        entity.setDeptName(dto.getDeptName());
        entity.setLocation(dto.getLocation());
        entity.setManagerId(dto.getManagerId());
        entity.setManagerName(dto.getManagerName());
        entity.setPhone(dto.getPhone());
        entity.setRemark(dto.getRemark());
        entity.setStatus(dto.getStatus());

        if (dto.getWarehouseType() != null) {
            entity.setWarehouseType(WarehouseType.valueOf(dto.getWarehouseType()));
        }

        entity = repository.save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Warehouse entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("库房不存在"));
        entity.setDeleted(true);
        repository.save(entity);
    }

    @Override
    public WarehouseVO getById(String id) {
        Warehouse entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("库房不存在"));
        return toVO(entity);
    }

    @Override
    public WarehouseVO getByCode(String code) {
        Warehouse entity = repository.findByWarehouseCode(code)
                .orElseThrow(() -> new BusinessException("库房不存在"));
        return toVO(entity);
    }

    @Override
    public PageResult<WarehouseVO> list(Integer pageNum, Integer pageSize) {
        Page<Warehouse> page = repository.findByDeletedFalse(PageRequest.of(pageNum - 1, pageSize));
        List<WarehouseVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public PageResult<WarehouseVO> query(QueryDTO query) {
        Page<Warehouse> page = repository.findByDeletedFalse(PageRequest.of(query.getPageNum() - 1, query.getPageSize()));
        List<WarehouseVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public List<WarehouseVO> listByType(String warehouseType) {
        List<Warehouse> list = repository.findByWarehouseTypeAndDeletedFalse(WarehouseType.valueOf(warehouseType));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<WarehouseVO> listByDept(String deptId) {
        List<Warehouse> list = repository.findByDeptIdAndDeletedFalse(deptId);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<WarehouseVO> listActive() {
        List<Warehouse> list = repository.findAllActive();
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    private WarehouseVO toVO(Warehouse entity) {
        WarehouseVO vo = new WarehouseVO();
        vo.setId(entity.getId());
        vo.setWarehouseCode(entity.getWarehouseCode());
        vo.setWarehouseName(entity.getWarehouseName());
        vo.setWarehouseType(entity.getWarehouseType() != null ? entity.getWarehouseType().name() : null);
        vo.setWarehouseTypeName(entity.getWarehouseType() != null ? entity.getWarehouseType().getName() : null);
        vo.setDeptId(entity.getDeptId());
        vo.setDeptName(entity.getDeptName());
        vo.setLocation(entity.getLocation());
        vo.setManagerId(entity.getManagerId());
        vo.setManagerName(entity.getManagerName());
        vo.setPhone(entity.getPhone());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}