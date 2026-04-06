package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.InboundDTO;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.vo.MaterialInboundVO;

import java.util.List;

/**
 * 入库管理服务接口
 */
public interface InboundService {

    /**
     * 入库登记
     */
    MaterialInboundVO register(InboundDTO dto);

    /**
     * 入库审核
     */
    MaterialInboundVO audit(AuditDTO dto);

    /**
     * 入库确认
     */
    MaterialInboundVO confirm(ConfirmDTO dto);

    /**
     * 取消入库
     */
    void cancel(String id);

    /**
     * 根据ID查询
     */
    MaterialInboundVO getById(String id);

    /**
     * 根据入库单号查询
     */
    MaterialInboundVO getByNo(String inboundNo);

    /**
     * 分页查询
     */
    PageResult<MaterialInboundVO> list(Integer pageNum, Integer pageSize);

    /**
     * 条件查询
     */
    PageResult<MaterialInboundVO> query(QueryDTO query);

    /**
     * 查询待审核入库单
     */
    List<MaterialInboundVO> listPending();
}