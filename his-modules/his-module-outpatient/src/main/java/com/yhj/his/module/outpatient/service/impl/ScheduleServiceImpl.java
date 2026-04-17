package com.yhj.his.module.outpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.outpatient.dto.ScheduleCreateRequest;
import com.yhj.his.module.outpatient.entity.Schedule;
import com.yhj.his.module.outpatient.repository.OutpatientScheduleRepository;
import com.yhj.his.module.outpatient.service.ScheduleService;
import com.yhj.his.module.outpatient.vo.ScheduleVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 排班服务实现
 */
@Slf4j
@Service("outpatientScheduleService")
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final OutpatientScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public ScheduleVO createSchedule(ScheduleCreateRequest request) {
        // 检查是否已存在排班
        Optional<Schedule> existing = scheduleRepository.findByDoctorIdAndScheduleDateAndTimePeriod(
                request.getDoctorId(), request.getScheduleDate(), request.getTimePeriod());
        if (existing.isPresent()) {
            throw new BusinessException("该医生在该时间段已有排班");
        }

        Schedule schedule = new Schedule();
        schedule.setDeptId(request.getDeptId());
        schedule.setDeptName(request.getDeptName());
        schedule.setDoctorId(request.getDoctorId());
        schedule.setDoctorName(request.getDoctorName());
        schedule.setDoctorTitle(request.getDoctorTitle());
        schedule.setScheduleDate(request.getScheduleDate());
        schedule.setTimePeriod(request.getTimePeriod());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setTotalQuota(request.getTotalQuota());
        schedule.setBookedQuota(0);
        schedule.setAvailableQuota(request.getTotalQuota());
        schedule.setRegistrationType(request.getRegistrationType());
        schedule.setRegistrationFee(request.getRegistrationFee());
        schedule.setDiagnosisFee(request.getDiagnosisFee());
        schedule.setClinicRoom(request.getClinicRoom());
        schedule.setRemark(request.getRemark());
        schedule.setStatus("正常");

        Schedule saved = scheduleRepository.save(schedule);
        log.info("创建排班成功: doctorId={}, date={}", saved.getDoctorId(), saved.getScheduleDate());
        return convertToVO(saved);
    }

    @Override
    @Transactional
    public ScheduleVO updateSchedule(String id, ScheduleCreateRequest request) {
        Schedule schedule = findById(id).orElseThrow(() -> new BusinessException("排班不存在"));

        // 检查是否已有人预约
        if (schedule.getBookedQuota() > 0) {
            throw new BusinessException("已有患者预约，无法修改排班");
        }

        schedule.setDeptId(request.getDeptId());
        schedule.setDeptName(request.getDeptName());
        schedule.setDoctorId(request.getDoctorId());
        schedule.setDoctorName(request.getDoctorName());
        schedule.setDoctorTitle(request.getDoctorTitle());
        schedule.setScheduleDate(request.getScheduleDate());
        schedule.setTimePeriod(request.getTimePeriod());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setTotalQuota(request.getTotalQuota());
        schedule.setAvailableQuota(request.getTotalQuota());
        schedule.setRegistrationType(request.getRegistrationType());
        schedule.setRegistrationFee(request.getRegistrationFee());
        schedule.setDiagnosisFee(request.getDiagnosisFee());
        schedule.setClinicRoom(request.getClinicRoom());
        schedule.setRemark(request.getRemark());

        Schedule saved = scheduleRepository.save(schedule);
        log.info("更新排班成功: id={}", saved.getId());
        return convertToVO(saved);
    }

    @Override
    public Optional<Schedule> findById(String id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public ScheduleVO getScheduleDetail(String id) {
        Schedule schedule = findById(id).orElseThrow(() -> new BusinessException("排班不存在"));
        return convertToVO(schedule);
    }

    @Override
    public PageResult<ScheduleVO> listSchedules(String deptId, String doctorId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<Schedule> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (deptId != null && !deptId.isEmpty()) {
                predicates.add(cb.equal(root.get("deptId"), deptId));
            }
            if (doctorId != null && !doctorId.isEmpty()) {
                predicates.add(cb.equal(root.get("doctorId"), doctorId));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("scheduleDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("scheduleDate"), endDate));
            }
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Schedule> page = scheduleRepository.findAll(spec, pageable);
        List<ScheduleVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<ScheduleVO> listSchedulesByDate(LocalDate date) {
        return scheduleRepository.findByScheduleDate(date).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleVO> listSchedulesByDeptAndDate(String deptId, LocalDate date) {
        Specification<Schedule> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deptId"), deptId));
            predicates.add(cb.equal(root.get("scheduleDate"), date));
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return scheduleRepository.findAll(spec).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleVO> listSchedulesByDoctor(String doctorId, LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByDoctorIdAndScheduleDateBetween(doctorId, startDate, endDate).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSchedule(String id) {
        Schedule schedule = findById(id).orElseThrow(() -> new BusinessException("排班不存在"));
        if (schedule.getBookedQuota() > 0) {
            throw new BusinessException("已有患者预约，无法删除排班");
        }
        schedule.setDeleted(true);
        scheduleRepository.save(schedule);
        log.info("删除排班成功: id={}", id);
    }

    @Override
    @Transactional
    public ScheduleVO stopSchedule(String id, String reason) {
        Schedule schedule = findById(id).orElseThrow(() -> new BusinessException("排班不存在"));
        schedule.setStatus("停诊");
        schedule.setStopReason(reason);
        Schedule saved = scheduleRepository.save(schedule);
        log.info("停诊成功: id={}, reason={}", id, reason);
        return convertToVO(saved);
    }

    @Override
    @Transactional
    public ScheduleVO restoreSchedule(String id) {
        Schedule schedule = findById(id).orElseThrow(() -> new BusinessException("排班不存在"));
        schedule.setStatus("正常");
        schedule.setStopReason(null);
        Schedule saved = scheduleRepository.save(schedule);
        log.info("恢复排班成功: id={}", id);
        return convertToVO(saved);
    }

    @Override
    @Transactional
    public boolean updateQuota(String scheduleId, int totalQuota) {
        Schedule schedule = findById(scheduleId).orElseThrow(() -> new BusinessException("排班不存在"));
        if (totalQuota < schedule.getBookedQuota()) {
            throw new BusinessException("总号源数不能小于已预约数");
        }
        schedule.setTotalQuota(totalQuota);
        schedule.setAvailableQuota(totalQuota - schedule.getBookedQuota());
        scheduleRepository.save(schedule);
        return true;
    }

    @Override
    public List<ScheduleVO> listAvailableSchedules(String deptId, LocalDate date) {
        Specification<Schedule> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (deptId != null && !deptId.isEmpty()) {
                predicates.add(cb.equal(root.get("deptId"), deptId));
            }
            predicates.add(cb.equal(root.get("scheduleDate"), date));
            predicates.add(cb.equal(root.get("status"), "正常"));
            predicates.add(cb.greaterThan(root.get("availableQuota"), 0));
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return scheduleRepository.findAll(spec).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private ScheduleVO convertToVO(Schedule schedule) {
        ScheduleVO vo = new ScheduleVO();
        vo.setScheduleId(schedule.getId());
        vo.setDeptId(schedule.getDeptId());
        vo.setDeptName(schedule.getDeptName());
        vo.setDoctorId(schedule.getDoctorId());
        vo.setDoctorName(schedule.getDoctorName());
        vo.setDoctorTitle(schedule.getDoctorTitle());
        vo.setScheduleDate(schedule.getScheduleDate());
        vo.setTimePeriod(schedule.getTimePeriod());
        vo.setStartTime(schedule.getStartTime());
        vo.setEndTime(schedule.getEndTime());
        vo.setTotalQuota(schedule.getTotalQuota());
        vo.setBookedQuota(schedule.getBookedQuota());
        vo.setAvailableQuota(schedule.getAvailableQuota());
        vo.setRegistrationType(schedule.getRegistrationType());
        vo.setRegistrationFee(schedule.getRegistrationFee());
        vo.setDiagnosisFee(schedule.getDiagnosisFee());
        vo.setStatus(schedule.getStatus());
        vo.setStopReason(schedule.getStopReason());
        vo.setClinicRoom(schedule.getClinicRoom());
        return vo;
    }
}