package com.yhj.his.module.pharmacy.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.pharmacy.dto.*;
import com.yhj.his.module.pharmacy.entity.Drug;
import com.yhj.his.module.pharmacy.enums.DrugCategory;
import com.yhj.his.module.pharmacy.enums.DrugStatus;
import com.yhj.his.module.pharmacy.repository.DrugRepository;
import com.yhj.his.module.pharmacy.service.DrugService;
import com.yhj.his.module.pharmacy.vo.DrugVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DrugServiceImpl implements DrugService {

    private final DrugRepository drugRepository;

    @Override
    @Transactional
    public Result<DrugVO> createDrug(DrugCreateDTO dto) {
        if (drugRepository.existsByDrugCode(dto.getDrugCode())) {
            throw new BusinessException("药品编码已存在: " + dto.getDrugCode());
        }

        Drug drug = new Drug();
        drug.setDrugCode(dto.getDrugCode());
        drug.setDrugName(dto.getDrugName());
        drug.setGenericName(dto.getGenericName());
        drug.setTradeName(dto.getTradeName());
        drug.setPinyinCode(dto.getPinyinCode() != null ? dto.getPinyinCode() : generatePinyinCode(dto.getDrugName()));
        drug.setCustomCode(dto.getCustomCode());
        drug.setDrugCategory(dto.getDrugCategory());
        drug.setDrugForm(dto.getDrugForm());
        drug.setDrugSpec(dto.getDrugSpec());
        drug.setDrugUnit(dto.getDrugUnit());
        drug.setPackageUnit(dto.getPackageUnit());
        drug.setPackageQuantity(dto.getPackageQuantity());
        drug.setManufacturer(dto.getManufacturer());
        drug.setOrigin(dto.getOrigin());
        drug.setApprovalNo(dto.getApprovalNo());
        drug.setPurchasePrice(dto.getPurchasePrice());
        drug.setRetailPrice(dto.getRetailPrice());
        drug.setIsPrescription(dto.getIsPrescription());
        drug.setIsOtc(dto.getIsOtc());
        drug.setIsEssential(dto.getIsEssential());
        drug.setIsInsurance(dto.getIsInsurance());
        drug.setInsuranceCode(dto.getInsuranceCode());
        drug.setInsuranceType(dto.getInsuranceType());
        drug.setStorageCondition(dto.getStorageCondition());
        drug.setShelfLife(dto.getShelfLife());
        drug.setAlertDays(dto.getAlertDays());
        drug.setMinStock(dto.getMinStock());
        drug.setMaxStock(dto.getMaxStock());
        drug.setStatus(dto.getStatus());

        drug = drugRepository.save(drug);
        log.info("创建药品成功: {}", drug.getDrugCode());
        return Result.success(toDrugVO(drug));
    }

    @Override
    @Transactional
    public Result<DrugVO> updateDrug(String drugId, DrugUpdateDTO dto) {
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new BusinessException("药品不存在: " + drugId));

        if (dto.getDrugName() != null) drug.setDrugName(dto.getDrugName());
        if (dto.getGenericName() != null) drug.setGenericName(dto.getGenericName());
        if (dto.getTradeName() != null) drug.setTradeName(dto.getTradeName());
        if (dto.getPinyinCode() != null) drug.setPinyinCode(dto.getPinyinCode());
        if (dto.getCustomCode() != null) drug.setCustomCode(dto.getCustomCode());
        if (dto.getDrugCategory() != null) drug.setDrugCategory(dto.getDrugCategory());
        if (dto.getDrugForm() != null) drug.setDrugForm(dto.getDrugForm());
        if (dto.getDrugSpec() != null) drug.setDrugSpec(dto.getDrugSpec());
        if (dto.getDrugUnit() != null) drug.setDrugUnit(dto.getDrugUnit());
        if (dto.getPackageUnit() != null) drug.setPackageUnit(dto.getPackageUnit());
        if (dto.getPackageQuantity() != null) drug.setPackageQuantity(dto.getPackageQuantity());
        if (dto.getManufacturer() != null) drug.setManufacturer(dto.getManufacturer());
        if (dto.getOrigin() != null) drug.setOrigin(dto.getOrigin());
        if (dto.getApprovalNo() != null) drug.setApprovalNo(dto.getApprovalNo());
        if (dto.getPurchasePrice() != null) drug.setPurchasePrice(dto.getPurchasePrice());
        if (dto.getRetailPrice() != null) drug.setRetailPrice(dto.getRetailPrice());
        if (dto.getIsPrescription() != null) drug.setIsPrescription(dto.getIsPrescription());
        if (dto.getIsOtc() != null) drug.setIsOtc(dto.getIsOtc());
        if (dto.getIsEssential() != null) drug.setIsEssential(dto.getIsEssential());
        if (dto.getIsInsurance() != null) drug.setIsInsurance(dto.getIsInsurance());
        if (dto.getInsuranceCode() != null) drug.setInsuranceCode(dto.getInsuranceCode());
        if (dto.getInsuranceType() != null) drug.setInsuranceType(dto.getInsuranceType());
        if (dto.getStorageCondition() != null) drug.setStorageCondition(dto.getStorageCondition());
        if (dto.getShelfLife() != null) drug.setShelfLife(dto.getShelfLife());
        if (dto.getAlertDays() != null) drug.setAlertDays(dto.getAlertDays());
        if (dto.getMinStock() != null) drug.setMinStock(dto.getMinStock());
        if (dto.getMaxStock() != null) drug.setMaxStock(dto.getMaxStock());
        if (dto.getStatus() != null) drug.setStatus(dto.getStatus());

        drug = drugRepository.save(drug);
        log.info("更新药品成功: {}", drug.getDrugCode());
        return Result.success(toDrugVO(drug));
    }

    @Override
    @Transactional
    public Result<Void> deleteDrug(String drugId) {
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new BusinessException("药品不存在: " + drugId));
        drug.setDeleted(true);
        drugRepository.save(drug);
        log.info("删除药品成功: {}", drug.getDrugCode());
        return Result.successVoid();
    }

    @Override
    public Result<DrugVO> getDrugById(String drugId) {
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new BusinessException("药品不存在: " + drugId));
        return Result.success(toDrugVO(drug));
    }

    @Override
    public Result<DrugVO> getDrugByCode(String drugCode) {
        Drug drug = drugRepository.findByDrugCode(drugCode)
                .orElseThrow(() -> new BusinessException("药品不存在: " + drugCode));
        return Result.success(toDrugVO(drug));
    }

    @Override
    public Result<PageResult<DrugVO>> queryDrugs(DrugQueryDTO query) {
        Pageable pageable = PageRequest.of(query.getPageNum() - 1, query.getPageSize());
        DrugCategory category = query.getDrugCategory() != null ? DrugCategory.valueOf(query.getDrugCategory()) : null;
        DrugStatus status = query.getStatus() != null ? DrugStatus.valueOf(query.getStatus()) : null;

        Page<Drug> page = drugRepository.queryDrugs(
                query.getKeyword(), category, query.getDrugForm(),
                query.getIsPrescription(), query.getIsOtc(), query.getIsInsurance(),
                query.getManufacturer(), status, pageable);

        List<DrugVO> list = page.getContent().stream().map(this::toDrugVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize()));
    }

    @Override
    public Result<List<DrugVO>> searchDrugs(String keyword) {
        List<Drug> drugs = drugRepository.findByDrugNameContaining(keyword);
        if (drugs.isEmpty()) {
            drugs = drugRepository.findByPinyinCodeContaining(keyword.toUpperCase());
        }
        List<DrugVO> list = drugs.stream()
                .filter(d -> !d.getDeleted() && d.getStatus() == DrugStatus.NORMAL)
                .map(this::toDrugVO).collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<String>> getDrugCategories() {
        return Result.success(java.util.Arrays.stream(DrugCategory.values())
                .map(DrugCategory::getName)
                .collect(Collectors.toList()));
    }

    @Override
    public Result<List<DrugVO>> getDrugsByCategory(String category) {
        DrugCategory drugCategory = DrugCategory.valueOf(category);
        List<Drug> drugs = drugRepository.findByDrugCategoryAndStatus(drugCategory, DrugStatus.NORMAL);
        List<DrugVO> list = drugs.stream()
                .filter(d -> !d.getDeleted())
                .map(this::toDrugVO).collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<DrugVO>> getPrescriptionDrugs() {
        List<Drug> drugs = drugRepository.findPrescriptionDrugs();
        return Result.success(drugs.stream().map(this::toDrugVO).collect(Collectors.toList()));
    }

    @Override
    public Result<List<DrugVO>> getOtcDrugs() {
        List<Drug> drugs = drugRepository.findOtcDrugs();
        return Result.success(drugs.stream().map(this::toDrugVO).collect(Collectors.toList()));
    }

    @Override
    public Result<List<DrugVO>> getInsuranceDrugs() {
        List<Drug> drugs = drugRepository.findInsuranceDrugs();
        return Result.success(drugs.stream().map(this::toDrugVO).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public Result<Void> toggleDrugStatus(String drugId, String status) {
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new BusinessException("药品不存在"));
        drug.setStatus(DrugStatus.valueOf(status));
        drugRepository.save(drug);
        return Result.successVoid();
    }

    private DrugVO toDrugVO(Drug drug) {
        DrugVO vo = new DrugVO();
        vo.setDrugId(drug.getId());
        vo.setDrugCode(drug.getDrugCode());
        vo.setDrugName(drug.getDrugName());
        vo.setGenericName(drug.getGenericName());
        vo.setTradeName(drug.getTradeName());
        vo.setPinyinCode(drug.getPinyinCode());
        vo.setDrugCategory(drug.getDrugCategory());
        vo.setDrugForm(drug.getDrugForm());
        vo.setDrugSpec(drug.getDrugSpec());
        vo.setDrugUnit(drug.getDrugUnit());
        vo.setPackageUnit(drug.getPackageUnit());
        vo.setPackageQuantity(drug.getPackageQuantity());
        vo.setManufacturer(drug.getManufacturer());
        vo.setOrigin(drug.getOrigin());
        vo.setApprovalNo(drug.getApprovalNo());
        vo.setPurchasePrice(drug.getPurchasePrice());
        vo.setRetailPrice(drug.getRetailPrice());
        vo.setIsPrescription(drug.getIsPrescription());
        vo.setIsOtc(drug.getIsOtc());
        vo.setIsEssential(drug.getIsEssential());
        vo.setIsInsurance(drug.getIsInsurance());
        vo.setInsuranceCode(drug.getInsuranceCode());
        vo.setInsuranceType(drug.getInsuranceType());
        vo.setStorageCondition(drug.getStorageCondition());
        vo.setShelfLife(drug.getShelfLife());
        vo.setAlertDays(drug.getAlertDays());
        vo.setMinStock(drug.getMinStock());
        vo.setMaxStock(drug.getMaxStock());
        vo.setStatus(drug.getStatus());
        vo.setCreateTime(drug.getCreateTime());
        vo.setUpdateTime(drug.getUpdateTime());
        return vo;
    }

    private String generatePinyinCode(String drugName) {
        if (drugName == null || drugName.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : drugName.toCharArray()) {
            if (Character.isLetter(c)) sb.append(Character.toUpperCase(c));
        }
        return sb.toString();
    }
}