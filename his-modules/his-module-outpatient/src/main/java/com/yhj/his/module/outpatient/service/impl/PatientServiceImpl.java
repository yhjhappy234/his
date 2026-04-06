package com.yhj.his.module.outpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.outpatient.dto.PatientCreateRequest;
import com.yhj.his.module.outpatient.dto.PatientUpdateRequest;
import com.yhj.his.module.outpatient.entity.Patient;
import com.yhj.his.module.outpatient.repository.PatientRepository;
import com.yhj.his.module.outpatient.service.PatientService;
import com.yhj.his.module.outpatient.vo.PatientVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 患者信息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final SequenceGenerator sequenceGenerator;

    @Override
    @Transactional
    public PatientVO createPatient(PatientCreateRequest request) {
        // 检查身份证号是否已存在
        if (request.getIdCardNo() != null && existsByIdCardNo(request.getIdCardNo())) {
            throw new BusinessException("身份证号已存在");
        }

        Patient patient = new Patient();
        patient.setPatientId(sequenceGenerator.generate("PAT", 8));
        patient.setIdCardNo(request.getIdCardNo());
        patient.setName(request.getName());
        patient.setGender(request.getGender());
        patient.setBirthDate(request.getBirthDate());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setEmergencyPhone(request.getEmergencyPhone());
        patient.setBloodType(request.getBloodType());
        patient.setAllergyHistory(request.getAllergyHistory());
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setMedicalInsuranceNo(request.getMedicalInsuranceNo());
        patient.setRemark(request.getRemark());
        patient.setStatus("正常");
        patient.setNoShowCount(0);
        patient.setIsBlacklist(false);

        Patient saved = patientRepository.save(patient);
        log.info("创建患者成功: patientId={}", saved.getPatientId());
        return convertToVO(saved);
    }

    @Override
    @Transactional
    public PatientVO updatePatient(String id, PatientUpdateRequest request) {
        Patient patient = findById(id).orElseThrow(() -> new BusinessException("患者不存在"));

        if (request.getName() != null) {
            patient.setName(request.getName());
        }
        if (request.getPhone() != null) {
            patient.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            patient.setAddress(request.getAddress());
        }
        if (request.getEmergencyContact() != null) {
            patient.setEmergencyContact(request.getEmergencyContact());
        }
        if (request.getEmergencyPhone() != null) {
            patient.setEmergencyPhone(request.getEmergencyPhone());
        }
        if (request.getBloodType() != null) {
            patient.setBloodType(request.getBloodType());
        }
        if (request.getAllergyHistory() != null) {
            patient.setAllergyHistory(request.getAllergyHistory());
        }
        if (request.getMedicalHistory() != null) {
            patient.setMedicalHistory(request.getMedicalHistory());
        }
        if (request.getRemark() != null) {
            patient.setRemark(request.getRemark());
        }

        Patient saved = patientRepository.save(patient);
        log.info("更新患者成功: patientId={}", saved.getPatientId());
        return convertToVO(saved);
    }

    @Override
    public Optional<Patient> findById(String id) {
        return patientRepository.findById(id);
    }

    @Override
    public Optional<Patient> findByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId);
    }

    @Override
    public Optional<Patient> findByIdCardNo(String idCardNo) {
        return patientRepository.findByIdCardNo(idCardNo);
    }

    @Override
    public PatientVO getPatientDetail(String id) {
        Patient patient = findById(id).orElseThrow(() -> new BusinessException("患者不存在"));
        return convertToVO(patient);
    }

    @Override
    public PageResult<PatientVO> listPatients(String name, String phone, String status, Pageable pageable) {
        Specification<Patient> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            if (phone != null && !phone.isEmpty()) {
                predicates.add(cb.like(root.get("phone"), "%" + phone + "%"));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Patient> page = patientRepository.findAll(spec, pageable);
        List<PatientVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    @Transactional
    public void deletePatient(String id) {
        Patient patient = findById(id).orElseThrow(() -> new BusinessException("患者不存在"));
        patient.setDeleted(true);
        patientRepository.save(patient);
        log.info("删除患者成功: patientId={}", patient.getPatientId());
    }

    @Override
    public boolean existsByPatientId(String patientId) {
        return patientRepository.existsByPatientId(patientId);
    }

    @Override
    public boolean existsByIdCardNo(String idCardNo) {
        return patientRepository.existsByIdCardNo(idCardNo);
    }

    @Override
    @Transactional
    public PatientVO setBlacklist(String id, boolean isBlacklist, String reason) {
        Patient patient = findById(id).orElseThrow(() -> new BusinessException("患者不存在"));
        patient.setIsBlacklist(isBlacklist);
        patient.setRemark(reason != null ? reason : patient.getRemark());
        Patient saved = patientRepository.save(patient);
        log.info("设置患者黑名单状态: patientId={}, isBlacklist={}", saved.getPatientId(), isBlacklist);
        return convertToVO(saved);
    }

    @Override
    public List<PatientVO> searchPatients(String keyword) {
        Specification<Patient> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                    cb.like(root.get("name"), "%" + keyword + "%"),
                    cb.like(root.get("idCardNo"), "%" + keyword + "%"),
                    cb.like(root.get("phone"), "%" + keyword + "%")
                ));
            }
            predicates.add(cb.equal(root.get("deleted"), false));
            predicates.add(cb.equal(root.get("status"), "正常"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return patientRepository.findAll(spec).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private PatientVO convertToVO(Patient patient) {
        PatientVO vo = new PatientVO();
        vo.setId(patient.getId());
        vo.setPatientId(patient.getPatientId());
        vo.setIdCardNo(patient.getIdCardNo());
        vo.setName(patient.getName());
        vo.setGender(patient.getGender());
        vo.setBirthDate(patient.getBirthDate());
        vo.setAge(calculateAge(patient.getBirthDate()));
        vo.setPhone(patient.getPhone());
        vo.setAddress(patient.getAddress());
        vo.setEmergencyContact(patient.getEmergencyContact());
        vo.setEmergencyPhone(patient.getEmergencyPhone());
        vo.setBloodType(patient.getBloodType());
        vo.setAllergyHistory(patient.getAllergyHistory());
        vo.setMedicalHistory(patient.getMedicalHistory());
        vo.setMedicalInsuranceNo(patient.getMedicalInsuranceNo());
        vo.setStatus(patient.getStatus());
        vo.setNoShowCount(patient.getNoShowCount());
        vo.setIsBlacklist(patient.getIsBlacklist());
        vo.setRemark(patient.getRemark());
        vo.setCreateTime(patient.getCreateTime());
        vo.setUpdateTime(patient.getUpdateTime());
        return vo;
    }

    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}