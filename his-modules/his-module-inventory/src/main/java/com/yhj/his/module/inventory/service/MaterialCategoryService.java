package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.MaterialCategoryDTO;
import com.yhj.his.module.inventory.vo.MaterialCategoryVO;

import java.util.List;

/**
 * 物资分类服务接口
 */
public interface MaterialCategoryService {

    /**
     * 创建分类
     */
    MaterialCategoryVO create(MaterialCategoryDTO dto);

    /**
     * 更新分类
     */
    MaterialCategoryVO update(String id, MaterialCategoryDTO dto);

    /**
     * 删除分类
     */
    void delete(String id);

    /**
     * 根据ID查询分类
     */
    MaterialCategoryVO getById(String id);

    /**
     * 根据编码查询分类
     */
    MaterialCategoryVO getByCode(String code);

    /**
     * 分页查询分类
     */
    PageResult<MaterialCategoryVO> list(Integer pageNum, Integer pageSize);

    /**
     * 查询分类树
     */
    List<MaterialCategoryVO> tree();

    /**
     * 查询子分类
     */
    List<MaterialCategoryVO> getChildren(String parentId);
}