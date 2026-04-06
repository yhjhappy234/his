package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.DischargeRecordSaveDTO;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.entity.DischargeRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 出院记录服务接口
 */
public interface DischargeRecordService {

    /**
     * 创建出院记录
     */
    DischargeRecord createRecord(DischargeRecordSaveDTO dto);

    /**
     * 更新出院记录
     */
    DischargeRecord updateRecord(String id, DischargeRecordSaveDTO dto);

    /**
     * 删除出院记录
     */
    void deleteRecord(String id);

    /**
     * 根据ID获取出院记录
     */
    DischargeRecord getRecordById(String id);

    /**
     * 根据住院ID获取出院记录
     */
    Optional<DischargeRecord> getRecordByAdmissionId(String admissionId);

    /**
     * 分页查询出院记录
     */
    Page<DischargeRecord> listRecords(Pageable pageable);

    /**
     * 根据患者ID查询出院记录列表
     */
    List<DischargeRecord> getRecordsByPatientId(String patientId);

    /**
     * 根据科室查询
     */
    Page<DischargeRecord> getRecordsByDeptId(String deptId, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<DischargeRecord> getRecordsByStatus(EmrStatus status, Pageable pageable);

    /**
     * 根据科室和状态查询
     */
    Page<DischargeRecord> getRecordsByDeptIdAndStatus(String deptId, EmrStatus status, Pageable pageable);

    /**
     * 查询患者最新出院记录
     */
    Optional<DischargeRecord> getLatestRecordByPatientId(String patientId);

    /**
     * 根据患者姓名模糊查询
     */
    Page<DischargeRecord> searchByPatientName(String patientName, Pageable pageable);

    /**
     * 提交出院记录
     */
    DischargeRecord submitRecord(EmrSubmitDTO dto);

    /**
     * 提交出院记录并返回质控结果
     */
    QcSubmitResultVO submitDischargeRecord(EmrSubmitDTO dto);

    /**
     * 审核出院记录
     */
    DischargeRecord auditRecord(String id, boolean approved, String auditorId, String auditorName, String comment);

    /**
     * 从模板创建出院记录
     */
    DischargeRecord createFromTemplate(String templateId, DischargeRecordSaveDTO dto);
}