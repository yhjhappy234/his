package com.yhj.his.module.emr.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.emr.dto.RectificationDTO;
import com.yhj.his.module.emr.vo.QcResultVO;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;

/**
 * 病历质控服务接口
 */
public interface QcService {

    /**
     * 执行质控检查
     */
    QcResultVO performQcCheck(String recordId, String recordType);

    /**
     * 执行质控检查并返回提交结果
     */
    QcSubmitResultVO performQcCheckAndReturn(String recordId, String recordType);

    /**
     * 获取质控结果
     */
    QcResultVO getQcResult(String recordId);

    /**
     * 获取质控结果详情
     */
    QcResultVO getQcResultById(String qcResultId);

    /**
     * 获取患者质控记录
     */
    PageResult<QcResultVO> getPatientQcResults(String patientId, Integer pageNum, Integer pageSize);

    /**
     * 获取待整改列表
     */
    PageResult<QcResultVO> getPendingRectificationList(Integer pageNum, Integer pageSize);

    /**
     * 发送整改通知
     */
    void sendRectificationNotice(String qcResultId);

    /**
     * 完成整改
     */
    void completeRectification(RectificationDTO dto);

    /**
     * 人工复核质控结果
     */
    QcResultVO manualReview(String qcResultId, String qcUserId, String qcUserName,
                             Integer score, String comment);
}