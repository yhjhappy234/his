package com.yhj.his.module.outpatient.service.impl;

import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.outpatient.dto.CallPatientRequest;
import com.yhj.his.module.outpatient.entity.Queue;
import com.yhj.his.module.outpatient.entity.Registration;
import com.yhj.his.module.outpatient.repository.QueueRepository;
import com.yhj.his.module.outpatient.repository.RegistrationRepository;
import com.yhj.his.module.outpatient.service.QueueService;
import com.yhj.his.module.outpatient.vo.QueueInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 排队服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private final QueueRepository queueRepository;
    private final RegistrationRepository registrationRepository;

    @Override
    @Transactional
    public Queue callPatient(CallPatientRequest request) {
        Registration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new BusinessException("挂号记录不存在"));

        registrationRepository.findCurrentPatient(request.getDoctorId(), LocalDate.now())
                .ifPresent(current -> {
                    throw new BusinessException("当前有患者正在就诊，请先结束当前就诊");
                });

        registration.setVisitStatus("就诊中");
        registration.setStartTime(LocalDateTime.now());
        registrationRepository.save(registration);

        Queue queue = queueRepository.findByRegistrationId(request.getRegistrationId())
                .orElseThrow(() -> new BusinessException("排队记录不存在"));

        queue.setStatus("就诊中");
        queue.setCallTime(LocalDateTime.now());
        queue.setStartTime(LocalDateTime.now());
        queue.setClinicRoom(request.getClinicRoom());
        queueRepository.save(queue);

        log.info("叫号成功: registrationId={}, patientName={}", request.getRegistrationId(), registration.getPatientName());
        return queue;
    }

    @Override
    public Optional<Queue> findById(String id) {
        return queueRepository.findById(id);
    }

    @Override
    public Optional<Queue> findByRegistrationId(String registrationId) {
        return queueRepository.findByRegistrationId(registrationId);
    }

    @Override
    public Queue getQueueDetail(String id) {
        return queueRepository.findById(id)
                .orElseThrow(() -> new BusinessException("排队记录不存在"));
    }

    @Override
    public QueueInfoVO getQueueInfo(String doctorId, LocalDate date) {
        Queue currentQueue = queueRepository.findCurrentPatient(doctorId, date).orElse(null);

        List<Queue> waitingList = queueRepository.findWaitingQueue(doctorId, date);

        List<Queue> allQueues = queueRepository.findByDoctorIdAndScheduleDate(doctorId, date);
        List<Queue> passedList = allQueues.stream()
                .filter(q -> "过号".equals(q.getStatus()))
                .collect(Collectors.toList());

        QueueInfoVO vo = new QueueInfoVO();
        vo.setCurrentNo(currentQueue != null ? currentQueue.getQueueNo() : null);
        vo.setCurrentPatient(currentQueue != null ? maskName(currentQueue.getPatientName()) : null);

        vo.setWaitingList(waitingList.stream()
                .map(q -> {
                    QueueInfoVO.WaitingPatientVO patient = new QueueInfoVO.WaitingPatientVO();
                    patient.setQueueNo(q.getQueueNo());
                    patient.setPatientName(maskName(q.getPatientName()));
                    patient.setStatus(q.getStatus());
                    patient.setWaitTime(q.getCheckInTime() != null ?
                            (int) Duration.between(q.getCheckInTime(), LocalDateTime.now()).toMinutes() : 0);
                    return patient;
                })
                .collect(Collectors.toList()));

        vo.setPassedList(passedList.stream()
                .map(q -> {
                    QueueInfoVO.PassedPatientVO patient = new QueueInfoVO.PassedPatientVO();
                    patient.setQueueNo(q.getQueueNo());
                    patient.setPatientName(maskName(q.getPatientName()));
                    patient.setPassTime(q.getCallTime() != null ?
                            q.getCallTime().format(DateTimeFormatter.ofPattern("HH:mm")) : null);
                    return patient;
                })
                .collect(Collectors.toList()));

        vo.setWaitCount(queueRepository.countWaiting(doctorId, date));
        vo.setPassCount(queueRepository.countPassed(doctorId, date));

        return vo;
    }

    @Override
    public List<Queue> listWaitingQueue(String doctorId, LocalDate date) {
        return queueRepository.findWaitingQueue(doctorId, date);
    }

    @Override
    public List<Queue> listDoctorQueue(String doctorId, LocalDate date) {
        return queueRepository.findByDoctorIdAndScheduleDate(doctorId, date);
    }

    @Override
    @Transactional
    public Queue startVisit(String queueId, String doctorId, String clinicRoom) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new BusinessException("排队记录不存在"));

        queue.setStatus("就诊中");
        queue.setStartTime(LocalDateTime.now());
        queue.setClinicRoom(clinicRoom);
        queueRepository.save(queue);

        Registration registration = registrationRepository.findById(queue.getRegistrationId()).orElse(null);
        if (registration != null) {
            registration.setVisitStatus("就诊中");
            registration.setStartTime(LocalDateTime.now());
            registrationRepository.save(registration);
        }

        log.info("开始就诊: queueId={}", queueId);
        return queue;
    }

    @Override
    @Transactional
    public Queue endVisit(String queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new BusinessException("排队记录不存在"));

        queue.setStatus("已完成");
        queue.setEndTime(LocalDateTime.now());
        queueRepository.save(queue);

        Registration registration = registrationRepository.findById(queue.getRegistrationId()).orElse(null);
        if (registration != null) {
            registration.setStatus("已就诊");
            registration.setVisitStatus("已完成");
            registration.setEndTime(LocalDateTime.now());
            registrationRepository.save(registration);
        }

        log.info("结束就诊: queueId={}", queueId);
        return queue;
    }

    @Override
    @Transactional
    public Queue markAsPassed(String queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new BusinessException("排队记录不存在"));

        queue.setStatus("过号");
        queueRepository.save(queue);

        Registration registration = registrationRepository.findById(queue.getRegistrationId()).orElse(null);
        if (registration != null) {
            registration.setVisitStatus("过号");
            registrationRepository.save(registration);
        }

        log.info("过号处理: queueId={}", queueId);
        return queue;
    }

    @Override
    @Transactional
    public Queue requeue(String queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new BusinessException("排队记录不存在"));

        queue.setStatus("等候中");
        queueRepository.save(queue);

        Registration registration = registrationRepository.findById(queue.getRegistrationId()).orElse(null);
        if (registration != null) {
            registration.setVisitStatus("待诊");
            registrationRepository.save(registration);
        }

        log.info("复诊入队: queueId={}", queueId);
        return queue;
    }

    @Override
    public Optional<Queue> getCurrentPatient(String doctorId, LocalDate date) {
        return queueRepository.findCurrentPatient(doctorId, date);
    }

    @Override
    public int countWaiting(String doctorId, LocalDate date) {
        return queueRepository.countWaiting(doctorId, date);
    }

    @Override
    public int countPassed(String doctorId, LocalDate date) {
        return queueRepository.countPassed(doctorId, date);
    }

    @Override
    @Transactional
    public Queue setPriority(String queueId, int priority) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new BusinessException("排队记录不存在"));

        queue.setPriority(priority);
        queueRepository.save(queue);
        return queue;
    }

    @Override
    public Queue nextPatient(String doctorId, LocalDate date) {
        List<Queue> waitingList = queueRepository.findWaitingQueue(doctorId, date);
        return waitingList.isEmpty() ? null : waitingList.get(0);
    }

    private String maskName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "*" + name.substring(name.length() - 1);
    }
}