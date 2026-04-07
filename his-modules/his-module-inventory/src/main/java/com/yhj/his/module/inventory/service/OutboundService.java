package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.OutboundDTO;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.vo.MaterialOutboundVO;

import java.util.List;

/**
 * 出库管理服务接口
 */
public interface OutboundService {

    /**
     * 出库申请
     */
    MaterialOutboundVO apply(OutboundDTO dto);

    /**
     * 出库审核
     */
    MaterialOutboundVO audit(AuditDTO dto);

    /**
     * 出库确认
     */
    MaterialOutboundVO confirm(ConfirmDTO dto);

    /**
     * 取消出库
     */
    void cancel(String id);

    /**
     * 根据ID查询
     */
    MaterialOutboundVO getById(String id);

    /**
     * 根据出库单号查询
     */
    MaterialOutboundVO getByNo(String outboundNo);

    /**
     * 分页查询
     */
    PageResult<MaterialOutboundVO> list(Integer pageNum, Integer pageSize);

    /**
     * 条件查询
     */
    PageResult<MaterialOutboundVO> query(QueryDTO query);

    /**
     * 查询待审核出库单
     */
    List<MaterialOutboundVO> listPending();
}