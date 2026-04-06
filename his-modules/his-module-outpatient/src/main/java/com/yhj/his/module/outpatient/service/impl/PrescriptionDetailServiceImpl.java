package com.yhj.his.module.outpatient.service.impl;

import com.yhj.his.module.outpatient.entity.PrescriptionDetail;
import com.yhj.his.module.outpatient.repository.PrescriptionDetailRepository;
import com.yhj.his.module.outpatient.service.PrescriptionDetailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * 处方明细服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrescriptionDetailServiceImpl implements PrescriptionDetailService {

    private final PrescriptionDetailRepository prescriptionDetailRepository;

    @Override
    @Transactional
    public PrescriptionDetail save(PrescriptionDetail detail) {
        log.info("保存处方明细: drugId={}", detail.getDrugId());

        // 计算金额
        if (detail.getAmount() == null) {
            detail.setAmount(calculateAmount(detail));
        }

        return prescriptionDetailRepository.save(detail);
    }

    @Override
    @Transactional
    public List<PrescriptionDetail> saveAll(List<PrescriptionDetail> details) {
        log.info("批量保存处方明细: count={}", details.size());

        // 计算金额
        details.forEach(detail -> {
            if (detail.getAmount() == null) {
                detail.setAmount(calculateAmount(detail));
            }
        });

        return prescriptionDetailRepository.saveAll(details);
    }

    @Override
    public Optional<PrescriptionDetail> findById(String id) {
        return prescriptionDetailRepository.findById(id);
    }

    @Override
    public List<PrescriptionDetail> findAll() {
        return prescriptionDetailRepository.findAll();
    }

    @Override
    public Page<PrescriptionDetail> findAll(Pageable pageable) {
        return prescriptionDetailRepository.findAll(pageable);
    }

    @Override
    public List<PrescriptionDetail> findByPrescriptionId(String prescriptionId) {
        return prescriptionDetailRepository.findByPrescriptionId(prescriptionId);
    }

    @Override
    public List<PrescriptionDetail> findByDrugId(String drugId) {
        return prescriptionDetailRepository.findByDrugId(drugId);
    }

    @Override
    @Transactional
    public PrescriptionDetail update(PrescriptionDetail detail) {
        log.info("更新处方明细: id={}", detail.getId());

        PrescriptionDetail existing = prescriptionDetailRepository.findById(detail.getId())
                .orElseThrow(() -> new IllegalArgumentException("处方明细不存在"));

        // 更新字段
        existing.setDrugId(detail.getDrugId());
        existing.setDrugName(detail.getDrugName());
        existing.setDrugSpec(detail.getDrugSpec());
        existing.setDrugUnit(detail.getDrugUnit());
        existing.setDrugForm(detail.getDrugForm());
        existing.setQuantity(detail.getQuantity());
        existing.setDosage(detail.getDosage());
        existing.setFrequency(detail.getFrequency());
        existing.setDays(detail.getDays());
        existing.setRoute(detail.getRoute());
        existing.setUnitPrice(detail.getUnitPrice());
        existing.setGroupNo(detail.getGroupNo());
        existing.setSkinTest(detail.getSkinTest());
        existing.setSkinTestResult(detail.getSkinTestResult());
        existing.setIsEssential(detail.getIsEssential());
        existing.setIsMedicalInsurance(detail.getIsMedicalInsurance());
        existing.setRemark(detail.getRemark());

        // 重新计算金额
        existing.setAmount(calculateAmount(existing));

        return prescriptionDetailRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        log.info("删除处方明细: id={}", id);
        prescriptionDetailRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByPrescriptionId(String prescriptionId) {
        log.info("删除处方所有明细: prescriptionId={}", prescriptionId);
        prescriptionDetailRepository.deleteByPrescriptionId(prescriptionId);
    }

    @Override
    public BigDecimal calculateAmount(PrescriptionDetail detail) {
        if (detail.getUnitPrice() == null || detail.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return detail.getUnitPrice().multiply(detail.getQuantity())
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void updateAmounts(List<PrescriptionDetail> details) {
        log.info("批量更新明细金额: count={}", details.size());

        details.forEach(detail -> {
            detail.setAmount(calculateAmount(detail));
        });

        prescriptionDetailRepository.saveAll(details);
    }

    @Override
    public long countByPrescriptionId(String prescriptionId) {
        return prescriptionDetailRepository.findByPrescriptionId(prescriptionId).size();
    }

    @Override
    public boolean existsById(String id) {
        return prescriptionDetailRepository.existsById(id);
    }

    @Override
    public Optional<PrescriptionDetail> findByPrescriptionIdAndDrugId(String prescriptionId, String drugId) {
        List<PrescriptionDetail> details = prescriptionDetailRepository.findByPrescriptionId(prescriptionId);
        return details.stream()
                .filter(d -> d.getDrugId().equals(drugId))
                .findFirst();
    }
}