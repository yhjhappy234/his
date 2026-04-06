package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.PatientCreateRequest;
import com.yhj.his.module.outpatient.dto.PatientUpdateRequest;
import com.yhj.his.module.outpatient.entity.Patient;
import com.yhj.his.module.outpatient.vo.PatientVO;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 患者信息服务接口
 */
public interface PatientService {

    /**
     * 创建患者
     */
    PatientVO createPatient(PatientCreateRequest request);

    /**
     * 更新患者信息
     */
    PatientVO updatePatient(String id, PatientUpdateRequest request);

    /**
     * 根据ID查询患者
     */
    Optional<Patient> findById(String id);

    /**
     * 根据患者ID查询患者
     */
    Optional<Patient> findByPatientId(String patientId);

    /**
     * 根据身份证号查询患者
     */
    Optional<Patient> findByIdCardNo(String idCardNo);

    /**
     * 获取患者详情VO
     */
    PatientVO getPatientDetail(String id);

    /**
     * 分页查询患者列表
     */
    PageResult<PatientVO> listPatients(String name, String phone, String status, Pageable pageable);

    /**
     * 删除患者(逻辑删除)
     */
    void deletePatient(String id);

    /**
     * 检查患者ID是否存在
     */
    boolean existsByPatientId(String patientId);

    /**
     * 检查身份证号是否存在
     */
    boolean existsByIdCardNo(String idCardNo);

    /**
     * 设置黑名单状态
     */
    PatientVO setBlacklist(String id, boolean isBlacklist, String reason);

    /**
     * 搜索患者(按姓名或身份证号模糊查询)
     */
    List<PatientVO> searchPatients(String keyword);
}