package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.dto.WarehouseDTO;
import com.yhj.his.module.inventory.entity.Warehouse;
import com.yhj.his.module.inventory.enums.WarehouseType;
import com.yhj.his.module.inventory.repository.MaterialInventoryRepository;
import com.yhj.his.module.inventory.repository.WarehouseRepository;
import com.yhj.his.module.inventory.service.WarehouseService;
import com.yhj.his.module.inventory.vo.WarehouseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 库房信息Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final MaterialInventoryRepository materialInventoryRepository;

    @Override
    @Transactional
    public WarehouseVO create(WarehouseDTO dto) {
        if (warehouseRepository.existsByWarehouseCodeAndDeletedFalse(dto.getWarehouseCode())) {
            throw new IllegalArgumentException("库房编码已存在: " + dto.getWarehouseCode());
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode(dto.getWarehouseCode());
        warehouse.setWarehouseName(dto.getWarehouseName());
        warehouse.setWarehouseType(dto.getWarehouseType() != null ? WarehouseType.valueOf(dto.getWarehouseType()) : null);
        warehouse.setDeptId(dto.getDeptId());
        warehouse.setDeptName(dto.getDeptName());
        warehouse.setLocation(dto.getLocation());
        warehouse.setManagerId(dto.getManagerId());
        warehouse.setManagerName(dto.getManagerName());
        warehouse.setPhone(dto.getPhone());
        warehouse.setRemark(dto.getRemark());
        warehouse.setStatus(1);
        warehouse.setDeleted(false);
        warehouse = warehouseRepository.save(warehouse);
        return toVO(warehouse);
    }

    @Override
    @Transactional
    public WarehouseVO update(String id, WarehouseDTO dto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("库房不存在: " + id));

        if (dto.getWarehouseCode() != null && !dto.getWarehouseCode().equals(warehouse.getWarehouseCode())) {
            if (warehouseRepository.existsByWarehouseCodeAndDeletedFalse(dto.getWarehouseCode())) {
                throw new IllegalArgumentException("库房编码已存在: " + dto.getWarehouseCode());
            }
            warehouse.setWarehouseCode(dto.getWarehouseCode());
        }
        if (dto.getWarehouseName() != null) warehouse.setWarehouseName(dto.getWarehouseName());
        if (dto.getWarehouseType() != null) warehouse.setWarehouseType(WarehouseType.valueOf(dto.getWarehouseType()));
        if (dto.getDeptId() != null) warehouse.setDeptId(dto.getDeptId());
        if (dto.getDeptName() != null) warehouse.setDeptName(dto.getDeptName());
        if (dto.getLocation() != null) warehouse.setLocation(dto.getLocation());
        if (dto.getManagerId() != null) warehouse.setManagerId(dto.getManagerId());
        if (dto.getManagerName() != null) warehouse.setManagerName(dto.getManagerName());
        if (dto.getPhone() != null) warehouse.setPhone(dto.getPhone());
        if (dto.getRemark() != null) warehouse.setRemark(dto.getRemark());

        warehouse = warehouseRepository.save(warehouse);
        return toVO(warehouse);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("库房不存在: " + id));
        warehouse.setDeleted(true);
        warehouseRepository.save(warehouse);
    }

    @Override
    public WarehouseVO getById(String id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("库房不存在: " + id));
        return toVO(warehouse);
    }

    @Override
    public WarehouseVO getByCode(String code) {
        Warehouse warehouse = warehouseRepository.findByWarehouseCode(code)
                .orElseThrow(() -> new IllegalArgumentException("库房不存在: " + code));
        return toVO(warehouse);
    }

    @Override
    public PageResult<WarehouseVO> list(Integer pageNum, Integer pageSize) {
        Page<Warehouse> page = warehouseRepository.findByDeletedFalse(PageRequest.of(pageNum - 1, pageSize));
        List<WarehouseVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public PageResult<WarehouseVO> query(QueryDTO query) {
        Page<Warehouse> page = warehouseRepository.findByDeletedFalse(PageRequest.of(query.getPageNum() - 1, query.getPageSize()));
        List<WarehouseVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public List<WarehouseVO> listByType(String warehouseType) {
        List<Warehouse> list = warehouseRepository.findByWarehouseTypeAndDeletedFalse(WarehouseType.valueOf(warehouseType));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<WarehouseVO> listByDept(String deptId) {
        List<Warehouse> list = warehouseRepository.findByDeptIdAndDeletedFalse(deptId);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<WarehouseVO> listActive() {
        List<Warehouse> list = warehouseRepository.findAllActive();
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    private WarehouseVO toVO(Warehouse warehouse) {
        WarehouseVO vo = new WarehouseVO();
        vo.setId(warehouse.getId());
        vo.setWarehouseCode(warehouse.getWarehouseCode());
        vo.setWarehouseName(warehouse.getWarehouseName());
        vo.setWarehouseType(warehouse.getWarehouseType() != null ? warehouse.getWarehouseType().name() : null);
        vo.setDeptId(warehouse.getDeptId());
        vo.setDeptName(warehouse.getDeptName());
        vo.setLocation(warehouse.getLocation());
        vo.setManagerId(warehouse.getManagerId());
        vo.setManagerName(warehouse.getManagerName());
        vo.setPhone(warehouse.getPhone());
        vo.setStatus(warehouse.getStatus());
        vo.setRemark(warehouse.getRemark());
        return vo;
    }
}