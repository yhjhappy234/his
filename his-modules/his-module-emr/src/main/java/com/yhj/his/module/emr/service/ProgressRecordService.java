package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.ProgressRecordSaveDTO;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.entity.ProgressRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.ProgressRecordType;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 病程记录服务接口
 */
public interface ProgressRecordService {

    /**
     * 创建病程记录
     */
    ProgressRecord createRecord(ProgressRecordSaveDTO dto);

    /**
     * 更新病程记录
     */
    ProgressRecord updateRecord(String id, ProgressRecordSaveDTO dto);

    /**
     * 删除病程记录
     */
    void deleteRecord(String id);

    /**
     * 根据ID获取病程记录
     */
    ProgressRecord getRecordById(String id);

    /**
     * 根据住院ID查询病程记录列表
     */
    List<ProgressRecord> getRecordsByAdmissionId(String admissionId);

    /**
     * 根据住院ID和记录类型查询
     */
    List<ProgressRecord> getRecordsByAdmissionIdAndType(String admissionId, ProgressRecordType recordType);

    /**
     * 根据住院ID和日期查询
     */
    List<ProgressRecord> getRecordsByAdmissionIdAndDate(String admissionId, LocalDate recordDate);

    /**
     * 根据住院ID和日期范围查询
     */
    List<ProgressRecord> getRecordsByAdmissionIdAndDateRange(String admissionId, LocalDate startDate, LocalDate endDate);

    /**
     * 分页查询病程记录
     */
    Page<ProgressRecord> listRecords(Pageable pageable);

    /**
     * 根据患者ID查询
     */
    Page<ProgressRecord> getRecordsByPatientId(String patientId, Pageable pageable);

    /**
     * 根据医生ID查询
     */
    Page<ProgressRecord> getRecordsByDoctorId(String doctorId, Pageable pageable);

    /**
     * 查询首次病程记录
     */
    Optional<ProgressRecord> getFirstProgressRecord(String admissionId);

    /**
     * 根据状态查询
     */
    Page<ProgressRecord> getRecordsByStatus(EmrStatus status, Pageable pageable);

    /**
     * 统计住院期间的病程记录数
     */
    Long countByAdmissionId(String admissionId);

    /**
     * 提交病程记录
     */
    ProgressRecord submitRecord(EmrSubmitDTO dto);

    /**
     * 提交病程记录并返回质控结果
     */
    QcSubmitResultVO submitProgressRecord(EmrSubmitDTO dto);

    /**
     * 审核病程记录
     */
    ProgressRecord auditRecord(String id, boolean approved, String reviewerId, String reviewerName);

    /**
     * 创建首次病程记录
     */
    ProgressRecord createFirstProgressRecord(ProgressRecordSaveDTO dto);

    /**
     * 创建上级医师查房记录
     */
    ProgressRecord createChiefRoundRecord(ProgressRecordSaveDTO dto);
}