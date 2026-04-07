package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.CheckPlanDTO;
import com.yhj.his.module.inventory.dto.CheckInputDTO;
import com.yhj.his.module.inventory.dto.CheckAdjustDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.vo.MaterialCheckVO;

import java.util.List;

/**
 * 库存盘点服务接口
 */
public interface CheckService {

    /**
     * 创建盘点计划
     */
    MaterialCheckVO createPlan(CheckPlanDTO dto);

    /**
     * 开始盘点
     */
    MaterialCheckVO start(String checkId);

    /**
     * 盘点录入
     */
    MaterialCheckVO input(CheckInputDTO dto);

    /**
     * 完成盘点
     */
    MaterialCheckVO complete(String checkId);

    /**
     * 差异处理
     */
    MaterialCheckVO adjust(CheckAdjustDTO dto);

    /**
     * 取消盘点
     */
    void cancel(String checkId);

    /**
     * 根据ID查询
     */
    MaterialCheckVO getById(String id);

    /**
     * 根据盘点单号查询
     */
    MaterialCheckVO getByNo(String checkNo);

    /**
     * 分页查询
     */
    PageResult<MaterialCheckVO> list(Integer pageNum, Integer pageSize);

    /**
     * 条件查询
     */
    PageResult<MaterialCheckVO> query(QueryDTO query);

    /**
     * 查询进行中的盘点
     */
    List<MaterialCheckVO> listInProgress();
}