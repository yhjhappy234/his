package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.PrescriptionCreateRequest;
import com.yhj.his.module.outpatient.entity.OutpatientPrescription;
import com.yhj.his.module.outpatient.vo.PrescriptionResultVO;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 门诊处方服务接口
 */
public interface OutpatientPrescriptionService {

    /**
     * 开立处方
     */
    PrescriptionResultVO createPrescription(PrescriptionCreateRequest request);

    /**
     * 根据ID查询处方
     */
    Optional<OutpatientPrescription> findById(String id);

    /**
     * 根据处方号查询处方
     */
    Optional<OutpatientPrescription> findByPrescriptionNo(String prescriptionNo);

    /**
     * 获取处方详情
     */
    PrescriptionResultVO getPrescriptionDetail(String id);

    /**
     * 分页查询处方列表
     */
    PageResult<OutpatientPrescription> listPrescriptions(String patientId, String doctorId, String payStatus, String status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 查询挂号关联处方列表
     */
    List<OutpatientPrescription> listPrescriptionsByRegistration(String registrationId);

    /**
     * 查询患者处方列表
     */
    List<OutpatientPrescription> listPatientPrescriptions(String patientId);

    /**
     * 作废处方
     */
    void voidPrescription(String id, String reason);

    /**
     * 审核处方
     */
    OutpatientPrescription auditPrescription(String id, boolean approved, String auditorId, String auditorName, String remark);

    /**
     * 计算处方金额
     */
    PrescriptionResultVO calculateAmount(String prescriptionId);

    /**
     * 更新处方
     */
    PrescriptionResultVO updatePrescription(String id, PrescriptionCreateRequest request);
}