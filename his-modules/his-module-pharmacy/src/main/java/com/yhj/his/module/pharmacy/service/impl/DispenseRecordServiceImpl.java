package com.yhj.his.module.pharmacy.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.DispenseConfirmDTO;
import com.yhj.his.module.pharmacy.dto.DispenseQueryDTO;
import com.yhj.his.module.pharmacy.dto.PrescriptionAuditDTO;
import com.yhj.his.module.pharmacy.entity.DispenseDetail;
import com.yhj.his.module.pharmacy.entity.DispenseRecord;
import com.yhj.his.module.pharmacy.enums.AuditStatus;
import com.yhj.his.module.pharmacy.enums.DispenseStatus;
import com.yhj.his.module.pharmacy.repository.DispenseDetailRepository;
import com.yhj.his.module.pharmacy.repository.DispenseRecordRepository;
import com.yhj.his.module.pharmacy.service.DispenseRecordService;
import com.yhj.his.module.pharmacy.service.DrugInventoryService;
import com.yhj.his.module.pharmacy.vo.DispenseRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 发药记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DispenseRecordServiceImpl implements DispenseRecordService {

    private final DispenseRecordRepository dispenseRecordRepository;
    private final DispenseDetailRepository dispenseDetailRepository;
    private final DrugInventoryService drugInventoryService;

    @Override
    public Result<DispenseRecordVO> getDispenseRecordById(String dispenseId) {
        Optional<DispenseRecord> optional = dispenseRecordRepository.findById(dispenseId);
        if (!optional.isPresent()) {
            return Result.error("发药记录不存在: " + dispenseId);
        }
        DispenseRecord record = optional.get();
        List<DispenseDetail> details = dispenseDetailRepository.findByDispenseId(dispenseId);
        return Result.success(entityToVO(record, details));
    }

    @Override
    public Result<DispenseRecordVO> getDispenseRecordByNo(String dispenseNo) {
        Optional<DispenseRecord> optional = dispenseRecordRepository.findByDispenseNo(dispenseNo);
        if (!optional.isPresent()) {
            return Result.error("发药记录不存在: " + dispenseNo);
        }
        DispenseRecord record = optional.get();
        List<DispenseDetail> details = dispenseDetailRepository.findByDispenseId(record.getId());
        return Result.success(entityToVO(record, details));
    }

    @Override
    public Result<PageResult<DispenseRecordVO>> queryDispenseRecords(DispenseQueryDTO query) {
        Pageable pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
        AuditStatus auditStatus = query.getAuditStatus() != null ? AuditStatus.valueOf(query.getAuditStatus()) : null;
        DispenseStatus dispenseStatus = query.getDispenseStatus() != null ? DispenseStatus.valueOf(query.getDispenseStatus()) : null;
        Page<DispenseRecord> page = dispenseRecordRepository.queryRecords(
                query.getPharmacyId(), query.getPatientId(), query.getPrescriptionNo(),
                auditStatus, dispenseStatus, query.getStartDate(), query.getEndDate(), pageable);
        List<DispenseRecordVO> list = page.getContent().stream()
                .map(record -> {
                    List<DispenseDetail> details = dispenseDetailRepository.findByDispenseId(record.getId());
                    return entityToVO(record, details);
                })
                .collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize()));
    }

    @Override
    public Result<DispenseRecordVO> getPendingDispenseByPrescription(String prescriptionId) {
        Optional<DispenseRecord> optional = dispenseRecordRepository.findByPrescriptionIdAndDispenseStatus(
                prescriptionId, DispenseStatus.PENDING);
        if (!optional.isPresent()) {
            return Result.error("未找到待发药记录");
        }
        DispenseRecord record = optional.get();
        List<DispenseDetail> details = dispenseDetailRepository.findByDispenseId(record.getId());
        return Result.success(entityToVO(record, details));
    }

    @Override
    @Transactional
    public Result<DispenseRecordVO> auditPrescription(String dispenseId, PrescriptionAuditDTO dto) {
        Optional<DispenseRecord> optional = dispenseRecordRepository.findById(dispenseId);
        if (!optional.isPresent()) {
            return Result.error("发药记录不存在: " + dispenseId);
        }
        DispenseRecord record = optional.get();
        if (record.getAuditStatus() != AuditStatus.PENDING) {
            return Result.error("只有待审核状态的记录可以审核");
        }

        record.setAuditorId(dto.getAuditorId());
        record.setAuditorName(dto.getAuditorName());
        record.setAuditTime(LocalDateTime.now());
        record.setAuditRemark(dto.getAuditRemark());

        if (dto.getApproved()) {
            record.setAuditStatus(AuditStatus.APPROVED);
        } else {
            record.setAuditStatus(AuditStatus.REJECTED);
        }

        if (dto.getDetails() != null) {
            for (PrescriptionAuditDTO.DetailAuditDTO detailAudit : dto.getDetails()) {
                Optional<DispenseDetail> detailOptional = dispenseDetailRepository.findById(detailAudit.getDetailId());
                if (detailOptional.isPresent()) {
                    DispenseDetail detail = detailOptional.get();
                    detail.setAuditResult(detailAudit.getAuditResult());
                    detail.setAuditRemark(detailAudit.getAuditRemark());
                    dispenseDetailRepository.save(detail);
                }
            }
        }

        DispenseRecord saved = dispenseRecordRepository.save(record);
        List<DispenseDetail> details = dispenseDetailRepository.findByDispenseId(dispenseId);
        return Result.success(entityToVO(saved, details));
    }

    @Override
    @Transactional
    public Result<DispenseRecordVO> confirmDispense(DispenseConfirmDTO dto) {
        Optional<DispenseRecord> optional = dispenseRecordRepository.findById(dto.getDispenseId());
        if (!optional.isPresent()) {
            return Result.error("发药记录不存在: " + dto.getDispenseId());
        }
        DispenseRecord record = optional.get();
        if (record.getAuditStatus() != AuditStatus.APPROVED) {
            return Result.error("只有审核通过的记录可以发药");
        }
        if (record.getDispenseStatus() != DispenseStatus.PENDING) {
            return Result.error("只有待发药状态的记录可以确认发药");
        }

        record.setDispenserId(dto.getDispenserId());
        record.setDispenserName(dto.getDispenserName());
        record.setDispenseTime(LocalDateTime.now());
        record.setDispenseStatus(DispenseStatus.DISPENSED);

        if (dto.getDetails() != null) {
            for (DispenseConfirmDTO.DetailConfirmDTO detailConfirm : dto.getDetails()) {
                Optional<DispenseDetail> detailOptional = dispenseDetailRepository.findById(detailConfirm.getDetailId());
                if (detailOptional.isPresent()) {
                    DispenseDetail detail = detailOptional.get();
                    detail.setBatchNo(detailConfirm.getBatchNo());
                    detail.setQuantity(detailConfirm.getQuantity());
                    dispenseDetailRepository.save(detail);

                    drugInventoryService.outbound(detailConfirm.getDrugId(), detailConfirm.getQuantity(), "发药出库", dto.getDispenserId());
                }
            }
        }

        DispenseRecord saved = dispenseRecordRepository.save(record);
        List<DispenseDetail> details = dispenseDetailRepository.findByDispenseId(dto.getDispenseId());
        return Result.success(entityToVO(saved, details));
    }

    @Override
    @Transactional
    public Result<Void> cancelDispense(String dispenseId, String reason) {
        Optional<DispenseRecord> optional = dispenseRecordRepository.findById(dispenseId);
        if (!optional.isPresent()) {
            return Result.error("发药记录不存在: " + dispenseId);
        }
        DispenseRecord record = optional.get();
        if (record.getDispenseStatus() == DispenseStatus.DISPENSED || record.getDispenseStatus() == DispenseStatus.RETURNED) {
            return Result.error("已发药或已退药的记录不能取消");
        }
        record.setDispenseStatus(DispenseStatus.CANCELLED);
        record.setAuditRemark(reason);
        dispenseRecordRepository.save(record);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<DispenseRecordVO> processDrugReturn(String dispenseId, String reason, String operatorId) {
        Optional<DispenseRecord> optional = dispenseRecordRepository.findById(dispenseId);
        if (!optional.isPresent()) {
            return Result.error("发药记录不存在: " + dispenseId);
        }
        DispenseRecord record = optional.get();
        if (record.getDispenseStatus() != DispenseStatus.DISPENSED) {
            return Result.error("只有已发药的记录可以退药");
        }

        List<DispenseDetail> details = dispenseDetailRepository.findByDispenseId(dispenseId);
        for (DispenseDetail detail : details) {
            if (detail.getBatchNo() != null && detail.getQuantity() != null) {
                drugInventoryService.inbound(createReturnInboundDTO(detail, operatorId));
            }
        }

        record.setDispenseStatus(DispenseStatus.RETURNED);
        record.setAuditRemark(reason);
        DispenseRecord saved = dispenseRecordRepository.save(record);
        return Result.success(entityToVO(saved, details));
    }

    @Override
    public Result<List<DispenseRecordVO>> getPendingAuditRecords(String pharmacyId) {
        List<DispenseRecord> list = dispenseRecordRepository.findByPharmacyIdAndAuditStatus(pharmacyId, AuditStatus.PENDING);
        List<DispenseRecordVO> vos = list.stream()
                .map(record -> {
                    List<DispenseDetail> details = dispenseDetailRepository.findByDispenseId(record.getId());
                    return entityToVO(record, details);
                })
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<List<DispenseRecordVO>> getPendingDispenseRecords(String pharmacyId) {
        List<DispenseRecord> list = dispenseRecordRepository.findByPharmacyIdAndAuditStatusAndDispenseStatus(
                pharmacyId, AuditStatus.APPROVED, DispenseStatus.PENDING);
        List<DispenseRecordVO> vos = list.stream()
                .map(record -> {
                    List<DispenseDetail> details = dispenseDetailRepository.findByDispenseId(record.getId());
                    return entityToVO(record, details);
                })
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<List<DispenseRecordVO>> getPatientDispenseRecords(String patientId) {
        List<DispenseRecord> list = dispenseRecordRepository.findByPatientIdOrderByCreateTimeDesc(patientId);
        List<DispenseRecordVO> vos = list.stream()
                .map(record -> {
                    List<DispenseDetail> details = dispenseDetailRepository.findByDispenseId(record.getId());
                    return entityToVO(record, details);
                })
                .collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    @Transactional
    public Result<Void> updateAuditStatus(String dispenseId, AuditStatus status) {
        Optional<DispenseRecord> optional = dispenseRecordRepository.findById(dispenseId);
        if (!optional.isPresent()) {
            return Result.error("发药记录不存在: " + dispenseId);
        }
        DispenseRecord record = optional.get();
        record.setAuditStatus(status);
        dispenseRecordRepository.save(record);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> updateDispenseStatus(String dispenseId, DispenseStatus status) {
        Optional<DispenseRecord> optional = dispenseRecordRepository.findById(dispenseId);
        if (!optional.isPresent()) {
            return Result.error("发药记录不存在: " + dispenseId);
        }
        DispenseRecord record = optional.get();
        record.setDispenseStatus(status);
        dispenseRecordRepository.save(record);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> confirmReceive(String dispenseId) {
        Optional<DispenseRecord> optional = dispenseRecordRepository.findById(dispenseId);
        if (!optional.isPresent()) {
            return Result.error("发药记录不存在: " + dispenseId);
        }
        DispenseRecord record = optional.get();
        record.setReceiveConfirm(true);
        record.setReceiveTime(LocalDateTime.now());
        dispenseRecordRepository.save(record);
        return Result.successVoid();
    }

    private com.yhj.his.module.pharmacy.dto.InventoryInDTO createReturnInboundDTO(DispenseDetail detail, String operatorId) {
        com.yhj.his.module.pharmacy.dto.InventoryInDTO dto = new com.yhj.his.module.pharmacy.dto.InventoryInDTO();
        dto.setDrugId(detail.getDrugId());
        dto.setQuantity(detail.getQuantity());
        dto.setRemark("退药入库");
        return dto;
    }

    private DispenseRecordVO entityToVO(DispenseRecord entity, List<DispenseDetail> details) {
        DispenseRecordVO vo = new DispenseRecordVO();
        vo.setDispenseId(entity.getId());
        vo.setDispenseNo(entity.getDispenseNo());
        vo.setPrescriptionId(entity.getPrescriptionId());
        vo.setPrescriptionNo(entity.getPrescriptionNo());
        vo.setPatientId(entity.getPatientId());
        vo.setPatientName(entity.getPatientName());
        vo.setGender(entity.getGender());
        vo.setAge(entity.getAge());
        vo.setVisitType(entity.getVisitType());
        vo.setDeptId(entity.getDeptId());
        vo.setDeptName(entity.getDeptName());
        vo.setDoctorId(entity.getDoctorId());
        vo.setDoctorName(entity.getDoctorName());
        vo.setPharmacyId(entity.getPharmacyId());
        vo.setPharmacyName(entity.getPharmacyName());
        vo.setTotalAmount(entity.getTotalAmount());
        vo.setAuditStatus(entity.getAuditStatus());
        vo.setAuditorName(entity.getAuditorName());
        vo.setAuditTime(entity.getAuditTime());
        vo.setAuditRemark(entity.getAuditRemark());
        vo.setDispenseStatus(entity.getDispenseStatus());
        vo.setDispenserName(entity.getDispenserName());
        vo.setDispenseTime(entity.getDispenseTime());
        vo.setCreateTime(entity.getCreateTime());

        if (details != null) {
            List<DispenseRecordVO.DispenseDetailVO> detailVOs = details.stream().map(this::detailToVO).collect(Collectors.toList());
            vo.setDetails(detailVOs);
        }
        return vo;
    }

    private DispenseRecordVO.DispenseDetailVO detailToVO(DispenseDetail detail) {
        DispenseRecordVO.DispenseDetailVO vo = new DispenseRecordVO.DispenseDetailVO();
        vo.setDetailId(detail.getId());
        vo.setDrugId(detail.getDrugId());
        vo.setDrugCode(detail.getDrugCode());
        vo.setDrugName(detail.getDrugName());
        vo.setDrugSpec(detail.getDrugSpec());
        vo.setDrugUnit(detail.getDrugUnit());
        vo.setBatchNo(detail.getBatchNo());
        vo.setExpiryDate(detail.getExpiryDate() != null ? detail.getExpiryDate().toString() : null);
        vo.setQuantity(detail.getQuantity());
        vo.setRetailPrice(detail.getRetailPrice());
        vo.setAmount(detail.getAmount());
        vo.setDosage(detail.getDosage());
        vo.setFrequency(detail.getFrequency());
        vo.setDays(detail.getDays());
        vo.setRoute(detail.getRoute());
        vo.setAuditResult(detail.getAuditResult());
        vo.setAuditRemark(detail.getAuditRemark());
        return vo;
    }
}