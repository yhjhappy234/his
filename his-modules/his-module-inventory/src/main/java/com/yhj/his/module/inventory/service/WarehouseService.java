package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.WarehouseDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.vo.WarehouseVO;

import java.util.List;

/**
 * 库房信息服务接口
 */
public interface WarehouseService {

    /**
     * 创建库房
     */
    WarehouseVO create(WarehouseDTO dto);

    /**
     * 更新库房
     */
    WarehouseVO update(String id, WarehouseDTO dto);

    /**
     * 删除库房
     */
    void delete(String id);

    /**
     * 根据ID查询
     */
    WarehouseVO getById(String id);

    /**
     * 根据编码查询
     */
    WarehouseVO getByCode(String code);

    /**
     * 分页查询
     */
    PageResult<WarehouseVO> list(Integer pageNum, Integer pageSize);

    /**
     * 条件查询
     */
    PageResult<WarehouseVO> query(QueryDTO query);

    /**
     * 根据类型查询
     */
    List<WarehouseVO> listByType(String warehouseType);

    /**
     * 根据科室查询
     */
    List<WarehouseVO> listByDept(String deptId);

    /**
     * 查询所有启用的库房
     */
    List<WarehouseVO> listActive();
}