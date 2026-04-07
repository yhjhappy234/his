package com.yhj.his.module.pacs.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.entity.*;
import com.yhj.his.module.pacs.repository.*;
import com.yhj.his.module.pacs.service.*;
import com.yhj.his.module.pacs.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 检查预约登记服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExamRequestServiceImpl implements ExamRequestService {

    private final ExamRequestRepository examRequestRepository;
    private final ExamRecordRepository examRecordRepository;
    private final RoomScheduleRepository roomScheduleRepository;
    private final ExamItemRepository examItemRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

    @Override
    @Transactional
    public ExamRequestVO createRequest(ExamRequestDTO dto) {
        // 查询检查项目信息
        ExamItem item = examItemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new BusinessException("检查项目不存在"));

        // 创建申请实体
        ExamRequest request = new ExamRequest();
        request.setRequestNo(generateRequestNo(item.getExamType()));
        request.setPatientId(dto.getPatientId());
        request.setPatientName(dto.getPatientName());
        request.setGender(dto.getGender());
        request.setAge(dto.getAge());
        request.setIdCardNo(dto.getIdCardNo());
        request.setVisitType(dto.getVisitType());
        request.setVisitId(dto.getVisitId());
        request.setAdmissionId(dto.getAdmissionId());
        request.setDeptId(dto.getDeptId());
        request.setDeptName(dto.getDeptName());
        request.setDoctorId(dto.getDoctorId());
        request.setDoctorName(dto.getDoctorName());
        request.setClinicalDiagnosis(dto.getClinicalDiagnosis());
        request.setClinicalInfo(dto.getClinicalInfo());
        request.setExamPurpose(dto.getExamPurpose());
        request.setItemId(dto.getItemId());
        request.setItemCode(item.getItemCode());
        request.setItemName(item.getItemName());
        request.setExamType(item.getExamType());
        request.setExamPart(dto.getExamPart() != null ? dto.getExamPart() : item.getExamPart());
        request.setExamMethod(dto.getExamMethod() != null ? dto.getExamMethod() : item.getExamMethod());
        request.setRequestTime(LocalDateTime.now());
        request.setIsEmergency(dto.getIsEmergency());
        request.setEmergencyLevel(dto.getEmergencyLevel());
        request.setTotalAmount(dto.getTotalAmount() != null ? dto.getTotalAmount() : item.getPrice());
        request.setPayStatus(dto.getPayStatus());
        request.setStatus("待预约");
        request.setRemark(dto.getRemark());

        request = examRequestRepository.save(request);
        log.info("创建检查申请成功: requestNo={}", request.getRequestNo());

        return convertToVO(request);
    }

    @Override
    @Transactional
    public ExamRequestVO schedule(ScheduleDTO dto) {
        ExamRequest request = examRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new BusinessException("申请不存在"));

        if (!"待预约".equals(request.getStatus())) {
            throw new BusinessException("申请状态不正确，无法预约");
        }

        // 查找排班并增加预约数
        if (dto.getRoomNo() != null) {
            LocalDateTime scheduleTime = dto.getScheduleTime();
            List<RoomSchedule> schedules = roomScheduleRepository.findByRoomNoAndScheduleDate(
                    dto.getRoomNo(), scheduleTime.toLocalDate());
            if (!schedules.isEmpty()) {
                RoomSchedule schedule = schedules.stream()
                        .filter(s -> scheduleTime.toLocalTime().isAfter(s.getStartTime()) &&
                                     scheduleTime.toLocalTime().isBefore(s.getEndTime()))
                        .findFirst()
                        .orElse(null);
                if (schedule != null && schedule.getAvailableQuota() > 0) {
                    roomScheduleRepository.incrementScheduledCount(schedule.getId());
                }
            }
        }

        request.setScheduleTime(dto.getScheduleTime());
        request.setStatus("已预约");
        request.setRemark(dto.getRemark() != null ? dto.getRemark() : request.getRemark());

        request = examRequestRepository.save(request);
        log.info("预约成功: requestNo={}, scheduleTime={}", request.getRequestNo(), dto.getScheduleTime());

        return convertToVO(request);
    }

    @Override
    @Transactional
    public ExamRecordVO checkIn(CheckInDTO dto) {
        ExamRequest request = examRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new BusinessException("申请不存在"));

        if (!"已预约".equals(request.getStatus())) {
            throw new BusinessException("申请状态不正确，无法登记");
        }

        // 创建检查记录
        ExamRecord record = new ExamRecord();
        record.setExamNo(generateExamNo(request.getExamType()));
        record.setRequestId(request.getId());
        record.setPatientId(request.getPatientId());
        record.setPatientName(request.getPatientName());
        record.setAccessionNo(record.getExamNo());
        record.setExamType(request.getExamType());
        record.setExamPart(request.getExamPart());
        record.setModality(request.getExamType());
        record.setEquipmentId(dto.getEquipmentId());
        record.setEquipmentName(dto.getEquipmentName());
        record.setRoomNo(dto.getRoomNo());
        record.setTechnicianId(dto.getTechnicianId());
        record.setTechnicianName(dto.getTechnicianName());
        record.setExamTime(LocalDateTime.now());
        record.setExamStatus("检查中");
        record.setReportStatus("待报告");
        record.setRemark(dto.getRemark());

        record = examRecordRepository.save(record);

        // 更新申请状态
        request.setStatus("已登记");
        request.setExamTime(LocalDateTime.now());
        examRequestRepository.save(request);

        log.info("检查登记成功: examNo={}", record.getExamNo());

        return convertRecordToVO(record);
    }

    @Override
    @Transactional
    public ExamRequestVO cancelRequest(String requestId, String reason) {
        ExamRequest request = examRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException("申请不存在"));

        if ("已取消".equals(request.getStatus()) || "已报告".equals(request.getStatus())) {
            throw new BusinessException("申请状态不正确，无法取消");
        }

        // 如果已预约，减少排班预约数
        if ("已预约".equals(request.getStatus()) && request.getScheduleTime() != null) {
            LocalDateTime scheduleTime = request.getScheduleTime();
            List<RoomSchedule> schedules = roomScheduleRepository.findByRoomNoAndScheduleDate(
                    request.getExamPart(), scheduleTime.toLocalDate());
            // 这里简化处理，实际应该根据机房号查询
        }

        request.setStatus("已取消");
        request.setRemark(reason);
        request = examRequestRepository.save(request);

        log.info("取消申请成功: requestNo={}, reason={}", request.getRequestNo(), reason);

        return convertToVO(request);
    }

    @Override
    public ExamRequestVO getRequestById(String requestId) {
        ExamRequest request = examRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException("申请不存在"));
        return convertToVO(request);
    }

    @Override
    public ExamRequestVO getRequestByNo(String requestNo) {
        ExamRequest request = examRequestRepository.findByRequestNo(requestNo)
                .orElseThrow(() -> new BusinessException("申请不存在"));
        return convertToVO(request);
    }

    @Override
    public List<ExamRequestVO> getRequestsByPatientId(String patientId) {
        List<ExamRequest> requests = examRequestRepository.findByPatientId(patientId);
        return requests.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<ExamRequestVO> queryRequests(ExamQueryDTO queryDTO) {
        Page<ExamRequest> page = examRequestRepository.findByConditions(
                queryDTO.getRequestNo(),
                queryDTO.getPatientId(),
                queryDTO.getPatientName(),
                queryDTO.getVisitType(),
                queryDTO.getExamType(),
                queryDTO.getStatus(),
                queryDTO.getDeptId(),
                queryDTO.getIsEmergency(),
                queryDTO.getRequestTimeStart(),
                queryDTO.getRequestTimeEnd(),
                PageUtils.toPageable(queryDTO.getPageNum(), queryDTO.getPageSize())
        );
        List<ExamRequestVO> list = page.getContent().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public List<ExamRequestVO> getPendingRequests() {
        List<ExamRequest> requests = examRequestRepository.findByStatus("待预约");
        return requests.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<RoomScheduleVO> getAvailableSchedules(String examType, String date) {
        List<RoomSchedule> schedules = roomScheduleRepository.findAvailableByDateAndExamType(
                LocalDateTime.parse(date + "T00:00:00").toLocalDate(),
                examType
        );
        return schedules.stream().map(this::convertScheduleToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExamRequestVO updateStatus(String requestId, String status) {
        ExamRequest request = examRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException("申请不存在"));
        request.setStatus(status);
        request = examRequestRepository.save(request);
        return convertToVO(request);
    }

    private String generateRequestNo(String examType) {
        String prefix = switch (examType) {
            case "X线" -> "XR";
            case "CT" -> "CT";
            case "MRI" -> "MR";
            case "超声" -> "US";
            case "内镜" -> "EN";
            default -> "EX";
        };
        return prefix + LocalDateTime.now().format(DATE_FORMATTER) +
               LocalDateTime.now().format(TIME_FORMATTER);
    }

    private String generateExamNo(String examType) {
        String prefix = switch (examType) {
            case "X线" -> "XR";
            case "CT" -> "CT";
            case "MRI" -> "MR";
            case "超声" -> "US";
            default -> "EX";
        };
        return prefix + LocalDateTime.now().format(DATE_FORMATTER) +
               String.format("%04d", examRequestRepository.countByRequestDate(LocalDateTime.now()) + 1);
    }

    private ExamRequestVO convertToVO(ExamRequest request) {
        ExamRequestVO vo = new ExamRequestVO();
        vo.setId(request.getId());
        vo.setRequestNo(request.getRequestNo());
        vo.setPatientId(request.getPatientId());
        vo.setPatientName(request.getPatientName());
        vo.setGender(request.getGender());
        vo.setAge(request.getAge());
        vo.setIdCardNo(request.getIdCardNo());
        vo.setVisitType(request.getVisitType());
        vo.setVisitId(request.getVisitId());
        vo.setAdmissionId(request.getAdmissionId());
        vo.setDeptId(request.getDeptId());
        vo.setDeptName(request.getDeptName());
        vo.setDoctorId(request.getDoctorId());
        vo.setDoctorName(request.getDoctorName());
        vo.setClinicalDiagnosis(request.getClinicalDiagnosis());
        vo.setClinicalInfo(request.getClinicalInfo());
        vo.setExamPurpose(request.getExamPurpose());
        vo.setItemId(request.getItemId());
        vo.setItemCode(request.getItemCode());
        vo.setItemName(request.getItemName());
        vo.setExamType(request.getExamType());
        vo.setExamPart(request.getExamPart());
        vo.setExamMethod(request.getExamMethod());
        vo.setRequestTime(request.getRequestTime());
        vo.setIsEmergency(request.getIsEmergency());
        vo.setEmergencyLevel(request.getEmergencyLevel());
        vo.setScheduleTime(request.getScheduleTime());
        vo.setExamTime(request.getExamTime());
        vo.setReportTime(request.getReportTime());
        vo.setStatus(request.getStatus());
        vo.setTotalAmount(request.getTotalAmount());
        vo.setPayStatus(request.getPayStatus());
        vo.setRemark(request.getRemark());
        vo.setCreateTime(request.getCreateTime());
        return vo;
    }

    private ExamRecordVO convertRecordToVO(ExamRecord record) {
        ExamRecordVO vo = new ExamRecordVO();
        vo.setId(record.getId());
        vo.setExamNo(record.getExamNo());
        vo.setRequestId(record.getRequestId());
        vo.setPatientId(record.getPatientId());
        vo.setPatientName(record.getPatientName());
        vo.setAccessionNo(record.getAccessionNo());
        vo.setStudyId(record.getStudyId());
        vo.setExamType(record.getExamType());
        vo.setExamPart(record.getExamPart());
        vo.setModality(record.getModality());
        vo.setEquipmentId(record.getEquipmentId());
        vo.setEquipmentName(record.getEquipmentName());
        vo.setRoomNo(record.getRoomNo());
        vo.setTechnicianId(record.getTechnicianId());
        vo.setTechnicianName(record.getTechnicianName());
        vo.setExamTime(record.getExamTime());
        vo.setExamDuration(record.getExamDuration());
        vo.setSeriesCount(record.getSeriesCount());
        vo.setImageCount(record.getImageCount());
        vo.setStoragePath(record.getStoragePath());
        vo.setContrastAgent(record.getContrastAgent());
        vo.setContrastDose(record.getContrastDose());
        vo.setRadiationDose(record.getRadiationDose());
        vo.setExamStatus(record.getExamStatus());
        vo.setReportStatus(record.getReportStatus());
        vo.setExamDescription(record.getExamDescription());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }

    private RoomScheduleVO convertScheduleToVO(RoomSchedule schedule) {
        RoomScheduleVO vo = new RoomScheduleVO();
        vo.setId(schedule.getId());
        vo.setRoomNo(schedule.getRoomNo());
        vo.setRoomName(schedule.getRoomName());
        vo.setEquipmentId(schedule.getEquipmentId());
        vo.setEquipmentName(schedule.getEquipmentName());
        vo.setScheduleDate(schedule.getScheduleDate());
        vo.setShift(schedule.getShift());
        vo.setStartTime(schedule.getStartTime());
        vo.setEndTime(schedule.getEndTime());
        vo.setTotalQuota(schedule.getTotalQuota());
        vo.setScheduledCount(schedule.getScheduledCount());
        vo.setAvailableQuota(schedule.getAvailableQuota());
        vo.setExamTypeLimit(schedule.getExamTypeLimit());
        vo.setDoctorId(schedule.getDoctorId());
        vo.setDoctorName(schedule.getDoctorName());
        vo.setTechnicianId(schedule.getTechnicianId());
        vo.setTechnicianName(schedule.getTechnicianName());
        vo.setStatus(schedule.getStatus());
        vo.setRemark(schedule.getRemark());
        vo.setCreateTime(schedule.getCreateTime() != null ? schedule.getCreateTime().toLocalDate() : null);
        return vo;
    }
}