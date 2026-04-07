package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.vo.AdmissionRegisterVO;

/**
 * 入院管理服务接口
 */
public interface AdmissionService {

    /**
     * 入院登记
     *
     * @param dto 入院登记请求
     * @return 入院登记结果
     */
    AdmissionRegisterVO register(AdmissionRegisterDTO dto);

    /**
     * 入院评估
     *
     * @param dto 入院评估请求
     * @return 评估是否成功
     */
    boolean assessment(AdmissionAssessmentDTO dto);

    /**
     * 预交金缴纳
     *
     * @param dto 预交金缴纳请求
     * @return 缴纳结果
     */
    DepositPaymentResponseDTO payDeposit(DepositPaymentDTO dto);

    /**
     * 查询住院记录
     *
     * @param admissionId 住院ID
     * @return 住院记录
     */
    AdmissionRegisterVO getById(String admissionId);

    /**
     * 根据住院号查询
     *
     * @param admissionNo 住院号
     * @return 住院记录
     */
    AdmissionRegisterVO getByAdmissionNo(String admissionNo);

    /**
     * 分页查询住院记录
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 状态筛选
     * @return 分页结果
     */
    PageResult<AdmissionRegisterVO> page(Integer pageNum, Integer pageSize, String status);
}