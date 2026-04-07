package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.MaterialDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.vo.MaterialVO;

import java.util.List;

/**
 * 物资信息服务接口
 */
public interface MaterialService {

    /**
     * 创建物资
     */
    MaterialVO create(MaterialDTO dto);

    /**
     * 更新物资
     */
    MaterialVO update(String id, MaterialDTO dto);

    /**
     * 删除物资
     */
    void delete(String id);

    /**
     * 根据ID查询物资
     */
    MaterialVO getById(String id);

    /**
     * 根据编码查询物资
     */
    MaterialVO getByCode(String code);

    /**
     * 分页查询物资
     */
    PageResult<MaterialVO> list(Integer pageNum, Integer pageSize);

    /**
     * 条件查询物资
     */
    PageResult<MaterialVO> query(QueryDTO query);

    /**
     * 根据分类查询物资
     */
    List<MaterialVO> listByCategory(String categoryId);

    /**
     * 搜索物资
     */
    PageResult<MaterialVO> search(String keyword, Integer pageNum, Integer pageSize);
}