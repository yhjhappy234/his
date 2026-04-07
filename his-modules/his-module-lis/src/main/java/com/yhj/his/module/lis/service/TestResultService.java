package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestResultAuditDTO;
import com.yhj.his.module.lis.dto.TestResultInputDTO;
import com.yhj.his.module.lis.entity.TestResult;
import com.yhj.his.module.lis.enums.ResultFlag;
import com.yhj.his.module.lis.vo.TestResultVO;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检验结果服务接口
 */
public interface TestResultService {

    /**
     * 录入检验结果
     */
    TestResultVO input(TestResultInputDTO dto);

    /**
     * 修改检验结果
     */
    TestResultVO modify(String id, String testValue, String modifyReason);

    /**
     * 审核检验结果
     */
    TestResultVO audit(TestResultAuditDTO dto);

    /**
     * 根据ID获取检验结果
     */
    TestResultVO getById(String id);

    /**
     * 根据申请ID获取所有结果
     */
    List<TestResultVO> listByRequestId(String requestId);

    /**
     * 根据样本ID获取所有结果
     */
    List<TestResultVO> listBySampleId(String sampleId);

    /**
     * 根据申请ID和项目ID获取结果
     */
    TestResultVO getByRequestIdAndItemId(String requestId, String itemId);

    /**
     * 分页查询检验结果
     */
    PageResult<TestResultVO> list(Pageable pageable);

    /**
     * 查询危急值结果
     */
    List<TestResultVO> listCriticalResults();

    /**
     * 查询异常结果
     */
    List<TestResultVO> listAbnormalResults();

    /**
     * 查询待审核结果
     */
    List<TestResultVO> listPendingAuditResults();

    /**
     * 根据检测时间范围查询
     */
    PageResult<TestResultVO> listByTestTime(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 查询患者历史结果
     */
    List<TestResultVO> listHistoryResults(String patientId, String itemId);

    /**
     * 删除检验结果
     */
    void delete(String id);

    /**
     * 统计申请的结果数量
     */
    long countByRequestId(String requestId);

    /**
     * 判断结果是否危急值
     */
    boolean checkCriticalValue(String itemId, String testValue);
}