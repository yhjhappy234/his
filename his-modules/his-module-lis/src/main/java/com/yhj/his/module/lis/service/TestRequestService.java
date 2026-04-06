package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestRequestCreateDTO;
import com.yhj.his.module.lis.entity.TestRequest;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.vo.TestRequestVO;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检验申请服务接口
 */
public interface TestRequestService {

    /**
     * 创建检验申请
     */
    TestRequestVO create(TestRequestCreateDTO dto);

    /**
     * 取消检验申请
     */
    TestRequestVO cancel(String id, String cancelReason, String cancelUserId);

    /**
     * 根据ID获取检验申请
     */
    TestRequestVO getById(String id);

    /**
     * 根据申请单号获取检验申请
     */
    TestRequestVO getByRequestNo(String requestNo);

    /**
     * 分页查询检验申请
     */
    PageResult<TestRequestVO> list(Pageable pageable);

    /**
     * 根据患者ID查询检验申请
     */
    List<TestRequestVO> listByPatientId(String patientId);

    /**
     * 根据状态查询检验申请
     */
    List<TestRequestVO> listByStatus(TestRequestStatus status);

    /**
     * 根据就诊ID查询检验申请
     */
    List<TestRequestVO> listByVisitId(String visitId);

    /**
     * 查询急诊申请
     */
    List<TestRequestVO> listEmergencyRequests();

    /**
     * 根据科室和时间范围查询
     */
    PageResult<TestRequestVO> listByDeptAndTime(String deptId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 更新申请状态
     */
    TestRequestVO updateStatus(String id, TestRequestStatus status);

    /**
     * 删除检验申请
     */
    void delete(String id);

    /**
     * 统计某状态的申请数量
     */
    long countByStatus(TestRequestStatus status);
}