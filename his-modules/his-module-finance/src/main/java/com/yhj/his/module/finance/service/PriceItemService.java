package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.finance.dto.PriceAdjustDTO;
import com.yhj.his.module.finance.dto.PriceItemCreateDTO;
import com.yhj.his.module.finance.dto.PriceItemUpdateDTO;
import com.yhj.his.module.finance.entity.PriceItem;
import com.yhj.his.module.finance.vo.PriceItemVO;

import java.util.List;

/**
 * 收费项目服务接口
 */
public interface PriceItemService {

    /**
     * 创建收费项目
     */
    PriceItemVO create(PriceItemCreateDTO dto);

    /**
     * 更新收费项目
     */
    PriceItemVO update(PriceItemUpdateDTO dto);

    /**
     * 删除收费项目(逻辑删除)
     */
    void delete(String id);

    /**
     * 根据ID查询
     */
    PriceItemVO getById(String id);

    /**
     * 根据编码查询
     */
    PriceItemVO getByCode(String itemCode);

    /**
     * 分页查询
     */
    PageResult<PriceItemVO> pageList(String itemName, String itemCategory, String status, int pageNum, int pageSize);

    /**
     * 根据分类查询列表
     */
    List<PriceItemVO> listByCategory(String itemCategory);

    /**
     * 查询生效的项目列表
     */
    List<PriceItemVO> listEffectiveItems();

    /**
     * 调整价格
     */
    PriceAdjustDTO adjustPrice(String itemId, java.math.BigDecimal newPrice, String reason);

    /**
     * 启用/停用项目
     */
    PriceItemVO updateStatus(String id, String status);

    /**
     * 批量导入收费项目
     */
    List<PriceItemVO> batchImport(List<PriceItemCreateDTO> items);

    /**
     * 根据名称模糊搜索
     */
    List<PriceItemVO> searchByName(String itemName);
}