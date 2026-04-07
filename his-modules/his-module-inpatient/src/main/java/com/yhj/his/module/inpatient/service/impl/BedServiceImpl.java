package com.yhj.his.module.inpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.entity.Bed;
import com.yhj.his.module.inpatient.entity.InpatientAdmission;
import com.yhj.his.module.inpatient.enums.BedStatus;
import com.yhj.his.module.inpatient.enums.AdmissionStatus;
import com.yhj.his.module.inpatient.repository.BedRepository;
import com.yhj.his.module.inpatient.repository.InpatientAdmissionRepository;
import com.yhj.his.module.inpatient.service.BedService;
import com.yhj.his.module.inpatient.vo.BedVO;
import com.yhj.his.module.inpatient.vo.WardBedStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 床位管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BedServiceImpl implements BedService {

    private final BedRepository bedRepository;
    private final InpatientAdmissionRepository admissionRepository;

    @Override
    public List<BedVO> list(BedQueryDTO queryDTO) {
        List<Bed> beds;

        if (queryDTO.getWardId() != null) {
            if (queryDTO.getStatus() != null) {
                beds = bedRepository.findByWardIdAndStatus(queryDTO.getWardId(), queryDTO.getStatus());
            } else {
                beds = bedRepository.findByWardId(queryDTO.getWardId());
            }
        } else if (queryDTO.getStatus() != null) {
            beds = bedRepository.findByStatus(queryDTO.getStatus());
        } else {
            beds = bedRepository.findAll();
        }

        return beds.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public WardBedStatisticsVO getStatistics(String wardId) {
        WardBedStatisticsVO vo = new WardBedStatisticsVO();
        vo.setWardId(wardId);

        List<Bed> beds = bedRepository.findByWardId(wardId);

        // 获取病区名称
        if (!beds.isEmpty()) {
            vo.setWardName(beds.get(0).getWardName());
        }

        vo.setTotal((long) beds.size());
        vo.setVacant(bedRepository.countByWardIdAndStatus(wardId, BedStatus.VACANT));
        vo.setOccupied(bedRepository.countByWardIdAndStatus(wardId, BedStatus.OCCUPIED));
        vo.setReserved(bedRepository.countByWardIdAndStatus(wardId, BedStatus.RESERVED));
        vo.setMaintenance(bedRepository.countByWardIdAndStatus(wardId, BedStatus.MAINTENANCE));

        // 计算使用率
        if (vo.getTotal() > 0) {
            vo.setUtilizationRate((vo.getOccupied() * 100.0) / vo.getTotal());
        }

        vo.setBeds(beds.stream().map(this::convertToVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assign(BedAssignDTO dto) {
        InpatientAdmission admission = admissionRepository.findById(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        Bed bed = bedRepository.findByWardIdAndBedNo(dto.getWardId(), dto.getBedNo())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "床位不存在"));

        if (bed.getStatus() != BedStatus.VACANT) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "床位状态不允许分配");
        }

        // 更新床位状态
        bed.setStatus(BedStatus.OCCUPIED);
        bed.setAdmissionId(dto.getAdmissionId());
        bed.setPatientId(admission.getPatientId());
        bed.setPatientName(admission.getPatientName());
        bedRepository.save(bed);

        // 更新住院记录
        admission.setWardId(dto.getWardId());
        admission.setWardName(bed.getWardName());
        admission.setRoomNo(bed.getRoomNo());
        admission.setBedNo(dto.getBedNo());
        admission.setStatus(AdmissionStatus.IN_HOSPITAL);
        admissionRepository.save(admission);

        log.info("床位分配成功，住院ID：{}，床位号：{}", dto.getAdmissionId(), dto.getBedNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean change(BedChangeDTO dto) {
        InpatientAdmission admission = admissionRepository.findById(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "住院记录不存在"));

        // 查询原床位
        Bed oldBed = bedRepository.findByAdmissionId(dto.getAdmissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "原床位记录不存在"));

        // 查询新床位
        Bed newBed = bedRepository.findByWardIdAndBedNo(dto.getNewWardId(), dto.getNewBedNo())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "新床位不存在"));

        if (newBed.getStatus() != BedStatus.VACANT) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "新床位状态不允许分配");
        }

        // 释放原床位
        oldBed.setStatus(BedStatus.VACANT);
        oldBed.setAdmissionId(null);
        oldBed.setPatientId(null);
        oldBed.setPatientName(null);
        bedRepository.save(oldBed);

        // 分配新床位
        newBed.setStatus(BedStatus.OCCUPIED);
        newBed.setAdmissionId(dto.getAdmissionId());
        newBed.setPatientId(admission.getPatientId());
        newBed.setPatientName(admission.getPatientName());
        bedRepository.save(newBed);

        // 更新住院记录
        admission.setWardId(dto.getNewWardId());
        admission.setWardName(newBed.getWardName());
        admission.setRoomNo(newBed.getRoomNo());
        admission.setBedNo(dto.getNewBedNo());
        admissionRepository.save(admission);

        log.info("床位调换成功，住院ID：{}，原床位：{}，新床位：{}",
                dto.getAdmissionId(), oldBed.getBedNo(), dto.getNewBedNo());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(BedStatusUpdateDTO dto) {
        Bed bed = bedRepository.findById(dto.getBedId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "床位不存在"));

        BedStatus newStatus = BedStatus.valueOf(dto.getNewStatus());

        // 如果床位被占用，不允许变更状态
        if (bed.getStatus() == BedStatus.OCCUPIED && newStatus != BedStatus.OCCUPIED) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "床位正在使用，不允许变更状态");
        }

        bed.setStatus(newStatus);
        bedRepository.save(bed);

        log.info("床位状态变更成功，床位ID：{}，新状态：{}", dto.getBedId(), dto.getNewStatus());
        return true;
    }

    @Override
    public BedVO getById(String bedId) {
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "床位不存在"));
        return convertToVO(bed);
    }

    @Override
    public PageResult<BedVO> page(Integer pageNum, Integer pageSize, BedQueryDTO queryDTO) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.ASC, "roomNo", "bedNo"));

        Page<Bed> page;
        if (queryDTO.getWardId() != null) {
            page = bedRepository.findByWardId(queryDTO.getWardId(), pageRequest);
        } else {
            page = bedRepository.findAll(pageRequest);
        }

        List<BedVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean release(String admissionId) {
        Bed bed = bedRepository.findByAdmissionId(admissionId).orElse(null);
        if (bed != null) {
            bed.setStatus(BedStatus.VACANT);
            bed.setAdmissionId(null);
            bed.setPatientId(null);
            bed.setPatientName(null);
            bedRepository.save(bed);
            log.info("床位释放成功，床位号：{}", bed.getBedNo());
        }
        return true;
    }

    /**
     * 转换为VO
     */
    private BedVO convertToVO(Bed bed) {
        BedVO vo = new BedVO();
        vo.setBedId(bed.getId());
        vo.setBedNo(bed.getBedNo());
        vo.setWardId(bed.getWardId());
        vo.setWardName(bed.getWardName());
        vo.setRoomNo(bed.getRoomNo());
        vo.setBedType(bed.getBedType());
        vo.setBedLevel(bed.getBedLevel());
        vo.setDailyRate(bed.getDailyRate());
        vo.setStatus(bed.getStatus());
        vo.setAdmissionId(bed.getAdmissionId());
        vo.setPatientId(bed.getPatientId());
        vo.setPatientName(bed.getPatientName());

        // 解析设施配置
        if (bed.getFacilities() != null) {
            try {
                List<String> facilities = cn.hutool.json.JSONUtil.toList(bed.getFacilities(), String.class);
                vo.setFacilities(facilities);
            } catch (Exception e) {
                log.warn("解析设施配置失败", e);
            }
        }

        return vo;
    }
}