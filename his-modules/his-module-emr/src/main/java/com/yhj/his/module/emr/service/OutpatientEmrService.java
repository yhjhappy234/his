package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.OutpatientEmrSaveDTO;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.entity.OutpatientEmr;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 门诊病历服务接口
 */
public interface OutpatientEmrService {

    /**
     * 创建门诊病历
     */
    OutpatientEmr createEmr(OutpatientEmrSaveDTO dto);

    /**
     * 更新门诊病历
     */
    OutpatientEmr updateEmr(String id, OutpatientEmrSaveDTO dto);

    /**
     * 删除门诊病历
     */
    void deleteEmr(String id);

    /**
     * 根据ID获取门诊病历
     */
    OutpatientEmr getEmrById(String id);

    /**
     * 根据就诊ID获取门诊病历
     */
    Optional<OutpatientEmr> getEmrByVisitId(String visitId);

    /**
     * 分页查询门诊病历
     */
    Page<OutpatientEmr> listEmrs(Pageable pageable);

    /**
     * 根据患者ID查询病历列表
     */
    List<OutpatientEmr> getEmrsByPatientId(String patientId);

    /**
     * 根据医生ID查询病历
     */
    Page<OutpatientEmr> getEmrsByDoctorId(String doctorId, Pageable pageable);

    /**
     * 根据科室和日期查询
     */
    Page<OutpatientEmr> getEmrsByDeptIdAndDate(String deptId, LocalDate visitDate, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<OutpatientEmr> getEmrsByStatus(EmrStatus status, Pageable pageable);

    /**
     * 根据科室和状态查询
     */
    Page<OutpatientEmr> getEmrsByDeptIdAndStatus(String deptId, EmrStatus status, Pageable pageable);

    /**
     * 查询患者最新病历
     */
    Optional<OutpatientEmr> getLatestEmrByPatientId(String patientId);

    /**
     * 根据患者姓名模糊查询
     */
    Page<OutpatientEmr> searchByPatientName(String patientName, Pageable pageable);

    /**
     * 提交病历
     */
    OutpatientEmr submitEmr(EmrSubmitDTO dto);

    /**
     * 提交门诊病历并返回质控结果
     */
    QcSubmitResultVO submitOutpatientEmr(EmrSubmitDTO dto);

    /**
     * 审核病历
     */
    OutpatientEmr auditEmr(String id, boolean approved, String auditorId, String auditorName, String comment);

    /**
     * 统计科室某日期的病历数
     */
    Long countByDeptIdAndVisitDate(String deptId, LocalDate visitDate);

    /**
     * 从模板创建病历
     */
    OutpatientEmr createFromTemplate(String templateId, OutpatientEmrSaveDTO dto);
}