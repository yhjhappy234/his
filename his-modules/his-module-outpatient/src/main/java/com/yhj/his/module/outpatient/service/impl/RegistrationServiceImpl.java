package com.yhj.his.module.outpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.outpatient.dto.AppointmentCancelRequest;
import com.yhj.his.module.outpatient.dto.AppointmentCreateRequest;
import com.yhj.his.module.outpatient.dto.CheckInRequest;
import com.yhj.his.module.outpatient.entity.Patient;
import com.yhj.his.module.outpatient.entity.Queue;
import com.yhj.his.module.outpatient.entity.Registration;
import com.yhj.his.module.outpatient.entity.Schedule;
import com.yhj.his.module.outpatient.repository.PatientRepository;
import com.yhj.his.module.outpatient.repository.QueueRepository;
import com.yhj.his.module.outpatient.repository.RegistrationRepository;
import com.yhj.his.module.outpatient.repository.ScheduleRepository;
import com.yhj.his.module.outpatient.service.RegistrationService;
import com.yhj.his.module.outpatient.vo.AppointmentResultVO;
import com.yhj.his.module.outpatient.vo.CheckInResultVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 挂号服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final PatientRepository patientRepository;
    private final ScheduleRepository scheduleRepository;
    private final QueueRepository queueRepository;
    private final SequenceGenerator sequenceGenerator;

    @Override
    @Transactional
    public AppointmentResultVO createAppointment(AppointmentCreateRequest request) {
        // 查询患者信息
        Patient patient = patientRepository.findByPatientId(request.getPatientId())
                .orElseThrow(() -> new BusinessException("患者不存在"));

        // 检查患者是否在黑名单
        if (patient.getIsBlacklist()) {
            throw new BusinessException("患者已在黑名单中，无法预约挂号");
        }

        // 查询或选择排班
        Schedule schedule;
        if (request.getScheduleId() != null) {
            schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new BusinessException("排班不存在"));
        } else {
            // 自动选择排班
            List<Schedule> schedules = scheduleRepository.findByDeptIdAndScheduleDateBetween(
                    request.getDeptId(), request.getScheduleDate(), request.getScheduleDate());
            schedule = schedules.stream()
                    .filter(s -> s.getAvailableQuota() > 0 && s.getStatus().equals("正常"))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("无可预约排班"));
        }

        // 检查排班状态
        if (!schedule.getStatus().equals("正常")) {
            throw new BusinessException("排班已停诊");
        }

        // 检查是否有剩余号源
        if (schedule.getAvailableQuota() <= 0) {
            throw new BusinessException("号源已满");
        }

        // 更新排班号源
        int updated = scheduleRepository.incrementBookedQuota(schedule.getId());
        if (updated == 0) {
            throw new BusinessException("预约失败，号源不足");
        }

        // 生成排队号
        Optional<Integer> maxQueueNo = registrationRepository.findMaxQueueNoByScheduleId(schedule.getId());
        int queueNo = maxQueueNo.orElse(0) + 1;

        // 创建挂号记录
        Registration registration = new Registration();
        registration.setPatientId(patient.getPatientId());
        registration.setPatientName(patient.getName());
        registration.setIdCardNo(patient.getIdCardNo());
        registration.setGender(patient.getGender());
        registration.setAge(Period.between(patient.getBirthDate(), LocalDate.now()).getYears());
        registration.setPhone(patient.getPhone());
        registration.setDeptId(schedule.getDeptId());
        registration.setDeptName(schedule.getDeptName());
        registration.setDoctorId(schedule.getDoctorId());
        registration.setDoctorName(schedule.getDoctorName());
        registration.setScheduleId(schedule.getId());
        registration.setScheduleDate(request.getScheduleDate());
        registration.setTimePeriod(schedule.getTimePeriod());
        registration.setQueueNo(queueNo);
        registration.setVisitNo(sequenceGenerator.generate("VIS", 10));
        registration.setRegistrationType(schedule.getRegistrationType() != null ? schedule.getRegistrationType() : "普通");
        registration.setRegistrationFee(schedule.getRegistrationFee() != null ? schedule.getRegistrationFee() : BigDecimal.ZERO);
        registration.setDiagnosisFee(schedule.getDiagnosisFee() != null ? schedule.getDiagnosisFee() : BigDecimal.ZERO);
        registration.setTotalFee(registration.getRegistrationFee().add(registration.getDiagnosisFee()));
        registration.setStatus("已预约");
        registration.setVisitStatus("待诊");
        registration.setSource(request.getSource() != null ? request.getSource() : "现场");
        registration.setBookingTime(LocalDateTime.now());
        registration.setClinicRoom(schedule.getClinicRoom());
        registration.setRemark(request.getRemark());

        Registration saved = registrationRepository.save(registration);
        log.info("创建挂号成功: registrationId={}, visitNo={}", saved.getId(), saved.getVisitNo());

        return buildAppointmentResult(saved, schedule);
    }

    @Override
    @Transactional
    public void cancelAppointment(AppointmentCancelRequest request) {
        Registration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new BusinessException("挂号记录不存在"));

        if (!registration.getStatus().equals("已预约") && !registration.getStatus().equals("已挂号")) {
            throw new BusinessException("当前状态无法取消预约");
        }

        // 释放号源
        if (registration.getScheduleId() != null) {
            scheduleRepository.decrementBookedQuota(registration.getScheduleId());
        }

        // 更新挂号状态
        registration.setStatus("已退号");
        registration.setCancelReason(request.getCancelReason());
        registration.setCancelTime(LocalDateTime.now());
        registrationRepository.save(registration);

        // 删除排队记录(如果已签到)
        Optional<Queue> queue = queueRepository.findByRegistrationId(registration.getId());
        queue.ifPresent(q -> queueRepository.delete(q));

        log.info("取消预约成功: registrationId={}", registration.getId());
    }

    @Override
    @Transactional
    public CheckInResultVO checkIn(CheckInRequest request) {
        Registration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new BusinessException("挂号记录不存在"));

        if (!registration.getPatientId().equals(request.getPatientId())) {
            throw new BusinessException("患者信息不匹配");
        }

        if (!registration.getStatus().equals("已预约") && !registration.getStatus().equals("已挂号")) {
            throw new BusinessException("当前状态无法签到");
        }

        // 更新挂号状态
        registration.setStatus("已签到");
        registration.setCheckInTime(LocalDateTime.now());
        registrationRepository.save(registration);

        // 创建排队记录
        Queue queue = new Queue();
        queue.setRegistrationId(registration.getId());
        queue.setPatientId(registration.getPatientId());
        queue.setPatientName(registration.getPatientName());
        queue.setDeptId(registration.getDeptId());
        queue.setDeptName(registration.getDeptName());
        queue.setDoctorId(registration.getDoctorId());
        queue.setDoctorName(registration.getDoctorName());
        queue.setScheduleDate(LocalDateTime.now());
        queue.setTimePeriod(registration.getTimePeriod());
        queue.setQueueNo(registration.getQueueNo());
        queue.setClinicRoom(registration.getClinicRoom());
        queue.setStatus("等候中");
        queue.setCheckInTime(LocalDateTime.now());
        queueRepository.save(queue);

        // 计算等候人数
        int waitCount = queueRepository.countWaiting(registration.getDoctorId(), LocalDate.now());

        log.info("签到成功: registrationId={}, queueNo={}", registration.getId(), registration.getQueueNo());

        return buildCheckInResult(registration, waitCount);
    }

    @Override
    public Optional<Registration> findById(String id) {
        return registrationRepository.findById(id);
    }

    @Override
    public Optional<Registration> findByVisitNo(String visitNo) {
        return registrationRepository.findByVisitNo(visitNo);
    }

    @Override
    public Registration getRegistrationDetail(String id) {
        return findById(id).orElseThrow(() -> new BusinessException("挂号记录不存在"));
    }

    @Override
    public PageResult<Registration> listRegistrations(String patientId, String deptId, String doctorId, String status, LocalDate date, Pageable pageable) {
        Specification<Registration> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (patientId != null && !patientId.isEmpty()) {
                predicates.add(cb.equal(root.get("patientId"), patientId));
            }
            if (deptId != null && !deptId.isEmpty()) {
                predicates.add(cb.equal(root.get("deptId"), deptId));
            }
            if (doctorId != null && !doctorId.isEmpty()) {
                predicates.add(cb.equal(root.get("doctorId"), doctorId));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (date != null) {
                predicates.add(cb.equal(root.get("scheduleDate"), date));
            }
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Registration> page = registrationRepository.findAll(spec, pageable);
        return PageResult.of(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<Registration> listPatientRegistrations(String patientId) {
        return registrationRepository.findByPatientIdOrderByScheduleDateDesc(patientId);
    }

    @Override
    public List<Registration> listDoctorRegistrations(String doctorId, LocalDate date) {
        return registrationRepository.findByDoctorIdAndScheduleDate(doctorId, date);
    }

    @Override
    @Transactional
    public void refundRegistration(String id, String reason) {
        Registration registration = getRegistrationDetail(id);

        if (registration.getStatus().equals("已退号")) {
            throw new BusinessException("挂号已退");
        }

        if (registration.getVisitStatus().equals("已完成")) {
            throw new BusinessException("就诊已完成，无法退号");
        }

        // 释放号源
        if (registration.getScheduleId() != null) {
            scheduleRepository.decrementBookedQuota(registration.getScheduleId());
        }

        // 更新状态
        registration.setStatus("已退号");
        registration.setCancelReason(reason);
        registration.setCancelTime(LocalDateTime.now());
        registrationRepository.save(registration);

        // 删除排队记录
        Optional<Queue> queue = queueRepository.findByRegistrationId(registration.getId());
        queue.ifPresent(q -> queueRepository.delete(q));

        log.info("退号成功: registrationId={}", id);
    }

    @Override
    @Transactional
    public Registration startVisit(String registrationId, String doctorId) {
        Registration registration = getRegistrationDetail(registrationId);

        if (!registration.getDoctorId().equals(doctorId)) {
            throw new BusinessException("非该医生的挂号记录");
        }

        if (!registration.getStatus().equals("已签到")) {
            throw new BusinessException("患者未签到");
        }

        registration.setVisitStatus("就诊中");
        registration.setStartTime(LocalDateTime.now());
        registrationRepository.save(registration);

        // 更新排队状态
        Optional<Queue> queue = queueRepository.findByRegistrationId(registrationId);
        queue.ifPresent(q -> {
            q.setStatus("就诊中");
            q.setStartTime(LocalDateTime.now());
            queueRepository.save(q);
        });

        log.info("开始就诊: registrationId={}", registrationId);
        return registration;
    }

    @Override
    @Transactional
    public Registration endVisit(String registrationId) {
        Registration registration = getRegistrationDetail(registrationId);

        registration.setVisitStatus("已完成");
        registration.setEndTime(LocalDateTime.now());
        registrationRepository.save(registration);

        // 更新排队状态
        Optional<Queue> queue = queueRepository.findByRegistrationId(registrationId);
        queue.ifPresent(q -> {
            q.setStatus("已完成");
            q.setEndTime(LocalDateTime.now());
            queueRepository.save(q);
        });

        log.info("结束就诊: registrationId={}", registrationId);
        return registration;
    }

    @Override
    public List<Registration> listWaitingPatients(String doctorId, LocalDate date) {
        return registrationRepository.findWaitingPatients(doctorId, date);
    }

    @Override
    public Optional<Registration> getCurrentPatient(String doctorId, LocalDate date) {
        return registrationRepository.findCurrentPatient(doctorId, date);
    }

    private AppointmentResultVO buildAppointmentResult(Registration registration, Schedule schedule) {
        AppointmentResultVO vo = new AppointmentResultVO();
        vo.setAppointmentId(registration.getId());
        vo.setQueueNo(registration.getQueueNo());
        vo.setVisitNo(registration.getVisitNo());
        vo.setScheduleTime(schedule.getTimePeriod());
        vo.setRegistrationFee(registration.getRegistrationFee());
        vo.setDiagnosisFee(registration.getDiagnosisFee());
        vo.setTotalFee(registration.getTotalFee());
        vo.setClinicRoom(schedule.getClinicRoom());
        vo.setDoctorName(schedule.getDoctorName());
        return vo;
    }

    private CheckInResultVO buildCheckInResult(Registration registration, int waitCount) {
        CheckInResultVO vo = new CheckInResultVO();
        vo.setQueueNo(registration.getQueueNo());
        vo.setWaitCount(waitCount);
        vo.setEstimatedWaitTime(waitCount * 10); // 假设每位患者10分钟
        vo.setClinicRoom(registration.getClinicRoom());
        vo.setDoctorName(registration.getDoctorName());
        return vo;
    }
}