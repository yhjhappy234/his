package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.ExaminationRequestDto;
import com.yhj.his.module.outpatient.entity.ExaminationRequest;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 检查检验申请服务接口
 */
public interface ExaminationRequestService {

    /**
     * 创建检查检验申请
     */
    ExaminationRequest createRequest(ExaminationRequestDto request);

    /**
     * 根据ID查询申请
     */
    Optional<ExaminationRequest> findById(String id);

    /**
     * 根据申请单号查询
     */
    Optional<ExaminationRequest> findByRequestNo(String requestNo);

    /**
     * 获取申请详情
     */
    ExaminationRequest getRequestDetail(String id);

    /**
     * 分页查询申请列表
     */
    PageResult<ExaminationRequest> listRequests(String patientId, String doctorId, String requestType, String status, String payStatus, LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 查询挂号关联申请列表
     */
    List<ExaminationRequest> listRequestsByRegistration(String registrationId);

    /**
     * 查询患者申请列表
     */
    List<ExaminationRequest> listPatientRequests(String patientId);

    /**
     * 取消申请
     */
    void cancelRequest(String id, String reason);

    /**
     * 完成检查
     */
    ExaminationRequest completeRequest(String id);

    /**
     * 更新收费状态
     */
    ExaminationRequest updatePayStatus(String id, String payStatus);

    /**
     * 更新申请
     */
    ExaminationRequest updateRequest(String id, ExaminationRequestDto request);

    /**
     * 查询待检查申请
     */
    List<ExaminationRequest> listPendingRequests(String patientId);
}