package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.RecordSaveRequest;
import com.yhj.his.module.outpatient.entity.OutpatientRecord;
import com.yhj.his.module.outpatient.vo.OutpatientRecordVO;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 门诊病历服务接口
 */
public interface OutpatientRecordService {

    /**
     * 保存病历(草稿)
     */
    OutpatientRecordVO saveDraft(RecordSaveRequest request);

    /**
     * 提交病历
     */
    OutpatientRecordVO submitRecord(RecordSaveRequest request);

    /**
     * 根据ID查询病历
     */
    Optional<OutpatientRecord> findById(String id);

    /**
     * 根据挂号ID查询病历
     */
    Optional<OutpatientRecord> findByRegistrationId(String registrationId);

    /**
     * 获取病历详情VO
     */
    OutpatientRecordVO getRecordDetail(String id);

    /**
     * 根据挂号ID获取病历详情
     */
    OutpatientRecordVO getRecordByRegistrationId(String registrationId);

    /**
     * 分页查询病历列表
     */
    PageResult<OutpatientRecordVO> listRecords(String patientId, String doctorId, String deptId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 查询患者病历列表
     */
    List<OutpatientRecordVO> listPatientRecords(String patientId);

    /**
     * 查询患者历史病历(最近N次)
     */
    List<OutpatientRecordVO> listRecentRecords(String patientId, int limit);

    /**
     * 作废病历
     */
    void voidRecord(String id, String reason);

    /**
     * 更新病历
     */
    OutpatientRecordVO updateRecord(String id, RecordSaveRequest request);
}