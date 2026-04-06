package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.OperationRecordSaveDTO;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.entity.OperationRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * 手术记录服务接口
 */
public interface OperationRecordService {

    /**
     * 创建手术记录
     */
    OperationRecord createRecord(OperationRecordSaveDTO dto);

    /**
     * 更新手术记录
     */
    OperationRecord updateRecord(String id, OperationRecordSaveDTO dto);

    /**
     * 删除手术记录
     */
    void deleteRecord(String id);

    /**
     * 根据ID获取手术记录
     */
    OperationRecord getRecordById(String id);

    /**
     * 根据住院ID查询手术记录列表
     */
    List<OperationRecord> getRecordsByAdmissionId(String admissionId);

    /**
     * 根据住院ID和日期范围查询
     */
    List<OperationRecord> getRecordsByAdmissionIdAndDateRange(String admissionId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据患者ID查询
     */
    List<OperationRecord> getRecordsByPatientId(String patientId);

    /**
     * 分页查询手术记录
     */
    Page<OperationRecord> listRecords(Pageable pageable);

    /**
     * 根据科室查询
     */
    Page<OperationRecord> getRecordsByDeptId(String deptId, Pageable pageable);

    /**
     * 根据主刀医生查询
     */
    Page<OperationRecord> getRecordsBySurgeonId(String surgeonId, Pageable pageable);

    /**
     * 根据手术日期查询
     */
    Page<OperationRecord> getRecordsByOperationDate(LocalDate operationDate, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<OperationRecord> getRecordsByStatus(EmrStatus status, Pageable pageable);

    /**
     * 根据患者姓名模糊查询
     */
    Page<OperationRecord> searchByPatientName(String patientName, Pageable pageable);

    /**
     * 根据手术名称模糊查询
     */
    Page<OperationRecord> searchByOperationName(String operationName, Pageable pageable);

    /**
     * 统计医生手术数量
     */
    Long countBySurgeonId(String surgeonId);

    /**
     * 提交手术记录
     */
    OperationRecord submitRecord(EmrSubmitDTO dto);

    /**
     * 提交手术记录并返回质控结果
     */
    QcSubmitResultVO submitOperationRecord(EmrSubmitDTO dto);

    /**
     * 审核手术记录
     */
    OperationRecord auditRecord(String id, boolean approved, String auditorId, String auditorName, String comment);

    /**
     * 从模板创建手术记录
     */
    OperationRecord createFromTemplate(String templateId, OperationRecordSaveDTO dto);
}