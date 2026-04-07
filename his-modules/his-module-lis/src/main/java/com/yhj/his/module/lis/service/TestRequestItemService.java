package com.yhj.his.module.lis.service;

import com.yhj.his.module.lis.entity.TestRequestItem;
import com.yhj.his.module.lis.vo.TestRequestVO;

import java.util.List;

/**
 * 检验申请明细服务接口
 */
public interface TestRequestItemService {

    /**
     * 根据申请ID查询所有明细
     */
    List<TestRequestVO.TestRequestItemVO> listByRequestId(String requestId);

    /**
     * 根据样本ID查询明细
     */
    List<TestRequestVO.TestRequestItemVO> listBySampleId(String sampleId);

    /**
     * 更新样本ID
     */
    void updateSampleId(String id, String sampleId);

    /**
     * 更新结果状态
     */
    void updateResultStatus(String id, String resultStatus);

    /**
     * 根据申请ID删除所有明细
     */
    void deleteByRequestId(String requestId);

    /**
     * 统计申请的项目数量
     */
    long countByRequestId(String requestId);
}