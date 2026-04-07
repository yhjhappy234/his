package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.AdmissionRecordSaveDTO;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.entity.AdmissionRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 入院记录服务接口
 */
public interface AdmissionRecordService {

    /**
     * 创建入院记录
     */
    AdmissionRecord createRecord(AdmissionRecordSaveDTO dto);

    /**
     * 更新入院记录
     */
    AdmissionRecord updateRecord(String id, AdmissionRecordSaveDTO dto);

    /**
     * 删除入院记录
     */
    void deleteRecord(String id);

    /**
     * 根据ID获取入院记录
     */
    AdmissionRecord getRecordById(String id);

    /**
     * 根据住院ID获取入院记录
     */
    Optional<AdmissionRecord> getRecordByAdmissionId(String admissionId);

    /**
     * 分页查询入院记录
     */
    Page<AdmissionRecord> listRecords(Pageable pageable);

    /**
     * 根据患者ID查询入院记录列表
     */
    List<AdmissionRecord> getRecordsByPatientId(String patientId);

    /**
     * 根据科室查询
     */
    Page<AdmissionRecord> getRecordsByDeptId(String deptId, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<AdmissionRecord> getRecordsByStatus(EmrStatus status, Pageable pageable);

    /**
     * 根据科室和状态查询
     */
    Page<AdmissionRecord> getRecordsByDeptIdAndStatus(String deptId, EmrStatus status, Pageable pageable);

    /**
     * 查询患者最新入院记录
     */
    Optional<AdmissionRecord> getLatestRecordByPatientId(String patientId);

    /**
     * 根据患者姓名模糊查询
     */
    Page<AdmissionRecord> searchByPatientName(String patientName, Pageable pageable);

    /**
     * 提交入院记录
     */
    AdmissionRecord submitRecord(EmrSubmitDTO dto);

    /**
     * 提交入院记录并返回质控结果
     */
    QcSubmitResultVO submitAdmissionRecord(EmrSubmitDTO dto);

    /**
     * 审核入院记录
     */
    AdmissionRecord auditRecord(String id, boolean approved, String auditorId, String auditorName, String comment);

    /**
     * 从模板创建入院记录
     */
    AdmissionRecord createFromTemplate(String templateId, AdmissionRecordSaveDTO dto);
}