package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.RequisitionDTO;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.vo.MaterialRequisitionVO;

import java.util.List;

/**
 * 物资申领服务接口
 */
public interface RequisitionService {

    /**
     * 领料申请
     */
    MaterialRequisitionVO apply(RequisitionDTO dto);

    /**
     * 领料审批
     */
    MaterialRequisitionVO approve(AuditDTO dto);

    /**
     * 领料发放
     */
    MaterialRequisitionVO issue(ConfirmDTO dto);

    /**
     * 领料接收确认
     */
    MaterialRequisitionVO receive(ConfirmDTO dto);

    /**
     * 取消申请
     */
    void cancel(String id);

    /**
     * 根据ID查询
     */
    MaterialRequisitionVO getById(String id);

    /**
     * 根据申领单号查询
     */
    MaterialRequisitionVO getByNo(String requisitionNo);

    /**
     * 分页查询
     */
    PageResult<MaterialRequisitionVO> list(Integer pageNum, Integer pageSize);

    /**
     * 条件查询
     */
    PageResult<MaterialRequisitionVO> query(QueryDTO query);

    /**
     * 查询待审批申领单
     */
    List<MaterialRequisitionVO> listPending();

    /**
     * 查询某科室的申领记录
     */
    List<MaterialRequisitionVO> listByDept(String deptId);
}