package com.yhj.his.module.pacs.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.vo.*;

import java.util.List;

/**
 * 检查预约登记服务接口
 */
public interface ExamRequestService {

    /**
     * 创建检查申请
     */
    ExamRequestVO createRequest(ExamRequestDTO dto);

    /**
     * 预约安排
     */
    ExamRequestVO schedule(ScheduleDTO dto);

    /**
     * 检查登记
     */
    ExamRecordVO checkIn(CheckInDTO dto);

    /**
     * 取消预约
     */
    ExamRequestVO cancelRequest(String requestId, String reason);

    /**
     * 查询申请详情
     */
    ExamRequestVO getRequestById(String requestId);

    /**
     * 根据申请单号查询
     */
    ExamRequestVO getRequestByNo(String requestNo);

    /**
     * 查询患者申请列表
     */
    List<ExamRequestVO> getRequestsByPatientId(String patientId);

    /**
     * 分页查询申请列表
     */
    PageResult<ExamRequestVO> queryRequests(ExamQueryDTO queryDTO);

    /**
     * 查询待预约申请
     */
    List<ExamRequestVO> getPendingRequests();

    /**
     * 查询可用排班
     */
    List<RoomScheduleVO> getAvailableSchedules(String examType, String date);

    /**
     * 更新申请状态
     */
    ExamRequestVO updateStatus(String requestId, String status);
}