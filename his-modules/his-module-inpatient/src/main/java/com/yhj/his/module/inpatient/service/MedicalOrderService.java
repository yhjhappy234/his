package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.vo.MedicalOrderVO;
import com.yhj.his.module.inpatient.vo.OrderExecutionVO;

import java.util.List;

/**
 * 医嘱管理服务接口
 */
public interface MedicalOrderService {

    /**
     * 开立医嘱
     *
     * @param dto 医嘱开立请求
     * @return 医嘱ID
     */
    String create(OrderCreateDTO dto);

    /**
     * 审核医嘱
     *
     * @param dto 医嘱审核请求
     * @return 审核是否成功
     */
    boolean audit(OrderAuditDTO dto);

    /**
     * 执行医嘱
     *
     * @param dto 医嘱执行请求
     * @return 执行记录ID
     */
    String execute(OrderExecuteDTO dto);

    /**
     * 停止医嘱
     *
     * @param dto 停止医嘱请求
     * @return 停止是否成功
     */
    boolean stop(OrderStopDTO dto);

    /**
     * 作废医嘱
     *
     * @param orderId 医嘱ID
     * @param reason 作废原因
     * @return 作废是否成功
     */
    boolean cancel(String orderId, String reason);

    /**
     * 查询医嘱详情
     *
     * @param orderId 医嘱ID
     * @return 医嘱详情
     */
    MedicalOrderVO getById(String orderId);

    /**
     * 查询住院医嘱列表
     *
     * @param admissionId 住院ID
     * @return 医嘱列表
     */
    List<MedicalOrderVO> listByAdmission(String admissionId);

    /**
     * 查询正在执行的医嘱
     *
     * @param admissionId 住院ID
     * @return 医嘱列表
     */
    List<MedicalOrderVO> listActiveOrders(String admissionId);

    /**
     * 分页查询医嘱
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param admissionId 住院ID
     * @return 分页结果
     */
    PageResult<MedicalOrderVO> page(Integer pageNum, Integer pageSize, String admissionId);

    /**
     * 查询医嘱执行记录
     *
     * @param orderId 医嘱ID
     * @return 执行记录列表
     */
    List<OrderExecutionVO> listExecutions(String orderId);
}