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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备机房管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentInfoRepository equipmentInfoRepository;
    private final RoomScheduleRepository roomScheduleRepository;

    @Override
    @Transactional
    public EquipmentVO createEquipment(EquipmentDTO dto) {
        // 检查设备编码是否已存在
        if (dto.getEquipmentCode() != null && equipmentInfoRepository.findByEquipmentCode(dto.getEquipmentCode()).isPresent()) {
            throw new BusinessException("设备编码已存在");
        }

        EquipmentInfo equipment = new EquipmentInfo();
        equipment.setEquipmentCode(dto.getEquipmentCode());
        equipment.setEquipmentName(dto.getEquipmentName());
        equipment.setEquipmentType(dto.getEquipmentType());
        equipment.setModel(dto.getModel());
        equipment.setManufacturer(dto.getManufacturer());
        equipment.setSerialNumber(dto.getSerialNumber());
        equipment.setAeTitle(dto.getAeTitle());
        equipment.setIpAddress(dto.getIpAddress());
        equipment.setPort(dto.getPort());
        equipment.setRoomNo(dto.getRoomNo());
        equipment.setRoomName(dto.getRoomName());
        equipment.setPurchaseDate(dto.getPurchaseDate());
        equipment.setEnableDate(dto.getEnableDate());
        equipment.setStatus(dto.getStatus());
        equipment.setManagerId(dto.getManagerId());
        equipment.setManagerName(dto.getManagerName());
        equipment.setSortOrder(dto.getSortOrder());
        equipment.setRemark(dto.getRemark());

        equipment = equipmentInfoRepository.save(equipment);
        log.info("创建设备成功: equipmentCode={}, equipmentName={}", equipment.getEquipmentCode(), equipment.getEquipmentName());

        return convertEquipmentToVO(equipment);
    }

    @Override
    @Transactional
    public EquipmentVO updateEquipment(EquipmentDTO dto) {
        EquipmentInfo equipment = equipmentInfoRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("设备不存在"));

        equipment.setEquipmentCode(dto.getEquipmentCode());
        equipment.setEquipmentName(dto.getEquipmentName());
        equipment.setEquipmentType(dto.getEquipmentType());
        equipment.setModel(dto.getModel());
        equipment.setManufacturer(dto.getManufacturer());
        equipment.setSerialNumber(dto.getSerialNumber());
        equipment.setAeTitle(dto.getAeTitle());
        equipment.setIpAddress(dto.getIpAddress());
        equipment.setPort(dto.getPort());
        equipment.setRoomNo(dto.getRoomNo());
        equipment.setRoomName(dto.getRoomName());
        equipment.setPurchaseDate(dto.getPurchaseDate());
        equipment.setEnableDate(dto.getEnableDate());
        equipment.setStatus(dto.getStatus());
        equipment.setManagerId(dto.getManagerId());
        equipment.setManagerName(dto.getManagerName());
        equipment.setSortOrder(dto.getSortOrder());
        equipment.setRemark(dto.getRemark());

        equipment = equipmentInfoRepository.save(equipment);
        log.info("更新设备成功: equipmentId={}", equipment.getId());

        return convertEquipmentToVO(equipment);
    }

    @Override
    @Transactional
    public void deleteEquipment(String equipmentId) {
        EquipmentInfo equipment = equipmentInfoRepository.findById(equipmentId)
                .orElseThrow(() -> new BusinessException("设备不存在"));

        equipmentInfoRepository.delete(equipment);
        log.info("删除设备成功: equipmentId={}", equipmentId);
    }

    @Override
    public EquipmentVO getEquipmentById(String equipmentId) {
        EquipmentInfo equipment = equipmentInfoRepository.findById(equipmentId)
                .orElseThrow(() -> new BusinessException("设备不存在"));
        return convertEquipmentToVO(equipment);
    }

    @Override
    public EquipmentVO getEquipmentByCode(String equipmentCode) {
        EquipmentInfo equipment = equipmentInfoRepository.findByEquipmentCode(equipmentCode)
                .orElseThrow(() -> new BusinessException("设备不存在"));
        return convertEquipmentToVO(equipment);
    }

    @Override
    public EquipmentVO getEquipmentByRoomNo(String roomNo) {
        EquipmentInfo equipment = equipmentInfoRepository.findByRoomNo(roomNo).orElse(null);
        return equipment != null ? convertEquipmentToVO(equipment) : null;
    }

    @Override
    public List<EquipmentVO> getEquipmentByType(String equipmentType) {
        List<EquipmentInfo> list = equipmentInfoRepository.findByEquipmentType(equipmentType);
        return list.stream().map(this::convertEquipmentToVO).collect(Collectors.toList());
    }

    @Override
    public List<EquipmentVO> getNormalEquipment() {
        List<EquipmentInfo> list = equipmentInfoRepository.findNormalEquipment();
        return list.stream().map(this::convertEquipmentToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<EquipmentVO> queryEquipment(String equipmentCode, String equipmentName, String equipmentType, String status, String roomNo, Integer pageNum, Integer pageSize) {
        Page<EquipmentInfo> page = equipmentInfoRepository.findByConditions(
                equipmentCode, equipmentName, equipmentType, status, roomNo,
                PageUtils.toPageable(pageNum, pageSize)
        );
        List<EquipmentVO> list = page.getContent().stream().map(this::convertEquipmentToVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    @Transactional
    public EquipmentVO updateEquipmentStatus(String equipmentId, String status) {
        EquipmentInfo equipment = equipmentInfoRepository.findById(equipmentId)
                .orElseThrow(() -> new BusinessException("设备不存在"));

        equipment.setStatus(status);
        equipment = equipmentInfoRepository.save(equipment);
        log.info("更新设备状态成功: equipmentId={}, status={}", equipmentId, status);

        return convertEquipmentToVO(equipment);
    }

    @Override
    @Transactional
    public RoomScheduleVO createSchedule(RoomScheduleDTO dto) {
        RoomSchedule schedule = new RoomSchedule();
        schedule.setRoomNo(dto.getRoomNo());
        schedule.setRoomName(dto.getRoomName());
        schedule.setEquipmentId(dto.getEquipmentId());
        schedule.setEquipmentName(dto.getEquipmentName());
        schedule.setScheduleDate(dto.getScheduleDate());
        schedule.setShift(dto.getShift());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setTotalQuota(dto.getTotalQuota());
        schedule.setScheduledCount(0);
        schedule.setAvailableQuota(dto.getTotalQuota());
        schedule.setExamTypeLimit(dto.getExamTypeLimit());
        schedule.setDoctorId(dto.getDoctorId());
        schedule.setDoctorName(dto.getDoctorName());
        schedule.setTechnicianId(dto.getTechnicianId());
        schedule.setTechnicianName(dto.getTechnicianName());
        schedule.setStatus(dto.getStatus());
        schedule.setRemark(dto.getRemark());

        schedule = roomScheduleRepository.save(schedule);
        log.info("创建排班成功: roomNo={}, scheduleDate={}", dto.getRoomNo(), dto.getScheduleDate());

        return convertScheduleToVO(schedule);
    }

    @Override
    @Transactional
    public RoomScheduleVO updateSchedule(RoomScheduleDTO dto) {
        RoomSchedule schedule = roomScheduleRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("排班不存在"));

        schedule.setRoomNo(dto.getRoomNo());
        schedule.setRoomName(dto.getRoomName());
        schedule.setEquipmentId(dto.getEquipmentId());
        schedule.setEquipmentName(dto.getEquipmentName());
        schedule.setScheduleDate(dto.getScheduleDate());
        schedule.setShift(dto.getShift());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setTotalQuota(dto.getTotalQuota());
        schedule.setAvailableQuota(dto.getTotalQuota() - schedule.getScheduledCount());
        schedule.setExamTypeLimit(dto.getExamTypeLimit());
        schedule.setDoctorId(dto.getDoctorId());
        schedule.setDoctorName(dto.getDoctorName());
        schedule.setTechnicianId(dto.getTechnicianId());
        schedule.setTechnicianName(dto.getTechnicianName());
        schedule.setStatus(dto.getStatus());
        schedule.setRemark(dto.getRemark());

        schedule = roomScheduleRepository.save(schedule);
        log.info("更新排班成功: scheduleId={}", schedule.getId());

        return convertScheduleToVO(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(String scheduleId) {
        RoomSchedule schedule = roomScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException("排班不存在"));

        if (schedule.getScheduledCount() > 0) {
            throw new BusinessException("排班已有预约，无法删除");
        }

        roomScheduleRepository.delete(schedule);
        log.info("删除排班成功: scheduleId={}", scheduleId);
    }

    @Override
    public RoomScheduleVO getScheduleById(String scheduleId) {
        RoomSchedule schedule = roomScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException("排班不存在"));
        return convertScheduleToVO(schedule);
    }

    @Override
    public List<RoomScheduleVO> getSchedulesByRoomNo(String roomNo) {
        List<RoomSchedule> list = roomScheduleRepository.findByRoomNo(roomNo);
        return list.stream().map(this::convertScheduleToVO).collect(Collectors.toList());
    }

    @Override
    public List<RoomScheduleVO> getSchedulesByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<RoomSchedule> list = roomScheduleRepository.findByScheduleDate(localDate);
        return list.stream().map(this::convertScheduleToVO).collect(Collectors.toList());
    }

    @Override
    public List<RoomScheduleVO> getAvailableSchedules(String date, String examType) {
        LocalDate localDate = LocalDate.parse(date);
        List<RoomSchedule> list = roomScheduleRepository.findAvailableByDateAndExamType(localDate, examType);
        return list.stream().map(this::convertScheduleToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<RoomScheduleVO> querySchedules(String roomNo, String date, String shift, String status, Integer pageNum, Integer pageSize) {
        LocalDate scheduleDate = date != null ? LocalDate.parse(date) : null;
        Page<RoomSchedule> page = roomScheduleRepository.findByConditions(
                roomNo, scheduleDate, shift, status,
                PageUtils.toPageable(pageNum, pageSize)
        );
        List<RoomScheduleVO> list = page.getContent().stream().map(this::convertScheduleToVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    @Transactional
    public RoomScheduleVO openSchedule(String scheduleId) {
        RoomSchedule schedule = roomScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException("排班不存在"));

        schedule.setStatus("开放");
        schedule = roomScheduleRepository.save(schedule);
        log.info("开放排班成功: scheduleId={}", scheduleId);

        return convertScheduleToVO(schedule);
    }

    @Override
    @Transactional
    public RoomScheduleVO closeSchedule(String scheduleId) {
        RoomSchedule schedule = roomScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException("排班不存在"));

        schedule.setStatus("关闭");
        schedule = roomScheduleRepository.save(schedule);
        log.info("关闭排班成功: scheduleId={}", scheduleId);

        return convertScheduleToVO(schedule);
    }

    @Override
    @Transactional
    public List<RoomScheduleVO> batchCreateSchedules(List<RoomScheduleDTO> dtoList) {
        return dtoList.stream()
                .map(this::createSchedule)
                .collect(Collectors.toList());
    }

    private EquipmentVO convertEquipmentToVO(EquipmentInfo equipment) {
        EquipmentVO vo = new EquipmentVO();
        vo.setId(equipment.getId());
        vo.setEquipmentCode(equipment.getEquipmentCode());
        vo.setEquipmentName(equipment.getEquipmentName());
        vo.setEquipmentType(equipment.getEquipmentType());
        vo.setModel(equipment.getModel());
        vo.setManufacturer(equipment.getManufacturer());
        vo.setSerialNumber(equipment.getSerialNumber());
        vo.setAeTitle(equipment.getAeTitle());
        vo.setIpAddress(equipment.getIpAddress());
        vo.setPort(equipment.getPort());
        vo.setRoomNo(equipment.getRoomNo());
        vo.setRoomName(equipment.getRoomName());
        vo.setPurchaseDate(equipment.getPurchaseDate());
        vo.setEnableDate(equipment.getEnableDate());
        vo.setStatus(equipment.getStatus());
        vo.setManagerId(equipment.getManagerId());
        vo.setManagerName(equipment.getManagerName());
        vo.setSortOrder(equipment.getSortOrder());
        vo.setRemark(equipment.getRemark());
        vo.setCreateTime(equipment.getCreateTime() != null ? equipment.getCreateTime().toString() : null);
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