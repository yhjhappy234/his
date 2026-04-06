package com.yhj.his.module.finance.service.impl;

import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.PriceAdjustDTO;
import com.yhj.his.module.finance.dto.PriceItemCreateDTO;
import com.yhj.his.module.finance.dto.PriceItemUpdateDTO;
import com.yhj.his.module.finance.entity.PriceItem;
import com.yhj.his.module.finance.repository.PriceItemRepository;
import com.yhj.his.module.finance.service.PriceItemService;
import com.yhj.his.module.finance.vo.PriceItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收费项目服务实现
 */
@Service
@RequiredArgsConstructor
public class PriceItemServiceImpl implements PriceItemService {

    private final PriceItemRepository priceItemRepository;

    @Override
    @Transactional
    public PriceItemVO create(PriceItemCreateDTO dto) {
        // 检查编码是否已存在
        if (priceItemRepository.existsByItemCode(dto.getItemCode())) {
            throw new BusinessException("项目编码已存在: " + dto.getItemCode());
        }

        PriceItem item = new PriceItem();
        item.setItemCode(dto.getItemCode());
        item.setItemName(dto.getItemName());
        item.setItemCategory(PriceItem.ItemCategory.valueOf(dto.getItemCategory()));
        item.setItemUnit(dto.getItemUnit());
        item.setItemSpec(dto.getItemSpec());
        item.setStandardPrice(dto.getStandardPrice());
        item.setRetailPrice(dto.getRetailPrice());
        item.setWholesalePrice(dto.getWholesalePrice());

        if (dto.getInsuranceType() != null) {
            item.setInsuranceType(PriceItem.InsuranceType.valueOf(dto.getInsuranceType()));
        }
        item.setInsuranceCode(dto.getInsuranceCode());
        item.setInsurancePrice(dto.getInsurancePrice());
        item.setReimbursementRatio(dto.getReimbursementRatio());

        item.setEffectiveDate(dto.getEffectiveDate() != null ? dto.getEffectiveDate() : LocalDate.now());
        item.setExpireDate(dto.getExpireDate());
        item.setVersionNo(dto.getVersionNo() != null ? dto.getVersionNo() : "V1");
        item.setRemark(dto.getRemark());
        item.setStatus(PriceItem.PriceItemStatus.ACTIVE);

        priceItemRepository.save(item);
        return toVO(item);
    }

    @Override
    @Transactional
    public PriceItemVO update(PriceItemUpdateDTO dto) {
        PriceItem item = priceItemRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("收费项目不存在: " + dto.getId()));

        if (dto.getItemName() != null) {
            item.setItemName(dto.getItemName());
        }
        if (dto.getItemCategory() != null) {
            item.setItemCategory(PriceItem.ItemCategory.valueOf(dto.getItemCategory()));
        }
        if (dto.getItemUnit() != null) {
            item.setItemUnit(dto.getItemUnit());
        }
        if (dto.getItemSpec() != null) {
            item.setItemSpec(dto.getItemSpec());
        }
        if (dto.getStandardPrice() != null) {
            item.setStandardPrice(dto.getStandardPrice());
        }
        if (dto.getRetailPrice() != null) {
            item.setRetailPrice(dto.getRetailPrice());
        }
        if (dto.getWholesalePrice() != null) {
            item.setWholesalePrice(dto.getWholesalePrice());
        }
        if (dto.getInsuranceType() != null) {
            item.setInsuranceType(PriceItem.InsuranceType.valueOf(dto.getInsuranceType()));
        }
        if (dto.getInsuranceCode() != null) {
            item.setInsuranceCode(dto.getInsuranceCode());
        }
        if (dto.getInsurancePrice() != null) {
            item.setInsurancePrice(dto.getInsurancePrice());
        }
        if (dto.getReimbursementRatio() != null) {
            item.setReimbursementRatio(dto.getReimbursementRatio());
        }
        if (dto.getEffectiveDate() != null) {
            item.setEffectiveDate(dto.getEffectiveDate());
        }
        if (dto.getExpireDate() != null) {
            item.setExpireDate(dto.getExpireDate());
        }
        if (dto.getVersionNo() != null) {
            item.setVersionNo(dto.getVersionNo());
        }
        if (dto.getStatus() != null) {
            item.setStatus(PriceItem.PriceItemStatus.valueOf(dto.getStatus()));
        }
        if (dto.getRemark() != null) {
            item.setRemark(dto.getRemark());
        }

        priceItemRepository.save(item);
        return toVO(item);
    }

    @Override
    @Transactional
    public void delete(String id) {
        PriceItem item = priceItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("收费项目不存在: " + id));
        item.setDeleted(true);
        priceItemRepository.save(item);
    }

    @Override
    public PriceItemVO getById(String id) {
        PriceItem item = priceItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("收费项目不存在: " + id));
        return toVO(item);
    }

    @Override
    public PriceItemVO getByCode(String itemCode) {
        PriceItem item = priceItemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> new BusinessException("收费项目不存在: " + itemCode));
        return toVO(item);
    }

    @Override
    public PageResult<PriceItemVO> pageList(String itemName, String itemCategory, String status, int pageNum, int pageSize) {
        Specification<PriceItem> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (itemName != null && !itemName.isEmpty()) {
                predicates = cb.and(predicates, cb.like(root.get("itemName"), "%" + itemName + "%"));
            }
            if (itemCategory != null && !itemCategory.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("itemCategory"), PriceItem.ItemCategory.valueOf(itemCategory)));
            }
            if (status != null && !status.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), PriceItem.PriceItemStatus.valueOf(status)));
            }
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            return predicates;
        };

        Page<PriceItem> page = priceItemRepository.findAll(spec, PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending()));
        List<PriceItemVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public List<PriceItemVO> listByCategory(String itemCategory) {
        List<PriceItem> items = priceItemRepository.findByItemCategoryAndStatus(
                PriceItem.ItemCategory.valueOf(itemCategory), PriceItem.PriceItemStatus.ACTIVE);
        return items.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<PriceItemVO> listEffectiveItems() {
        List<PriceItem> items = priceItemRepository.findEffectiveItems(PriceItem.PriceItemStatus.ACTIVE, LocalDate.now());
        return items.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PriceAdjustDTO adjustPrice(String itemId, BigDecimal newPrice, String reason) {
        PriceItem item = priceItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("收费项目不存在: " + itemId));

        PriceAdjustDTO adjust = new PriceAdjustDTO();
        adjust.setItemId(itemId);
        adjust.setItemCode(item.getItemCode());
        adjust.setOldPrice(item.getRetailPrice());
        adjust.setNewPrice(newPrice);
        adjust.setReason(reason);
        adjust.setEffectiveDate(LocalDate.now());
        adjust.setOldVersionNo(item.getVersionNo());

        // 更新价格和版本
        String newVersion = "V" + (Integer.parseInt(item.getVersionNo().replace("V", "")) + 1);
        item.setRetailPrice(newPrice);
        item.setVersionNo(newVersion);
        item.setEffectiveDate(LocalDate.now());

        priceItemRepository.save(item);

        adjust.setNewVersionNo(newVersion);
        return adjust;
    }

    @Override
    @Transactional
    public PriceItemVO updateStatus(String id, String status) {
        PriceItem item = priceItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("收费项目不存在: " + id));
        item.setStatus(PriceItem.PriceItemStatus.valueOf(status));
        priceItemRepository.save(item);
        return toVO(item);
    }

    @Override
    @Transactional
    public List<PriceItemVO> batchImport(List<PriceItemCreateDTO> items) {
        List<PriceItemVO> result = new java.util.ArrayList<>();
        for (PriceItemCreateDTO dto : items) {
            try {
                result.add(create(dto));
            } catch (BusinessException e) {
                // 记录失败项，继续处理其他项
            }
        }
        return result;
    }

    @Override
    public List<PriceItemVO> searchByName(String itemName) {
        List<PriceItem> items = priceItemRepository.findByItemNameContaining(itemName);
        return items.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 实体转VO
     */
    private PriceItemVO toVO(PriceItem item) {
        PriceItemVO vo = new PriceItemVO();
        vo.setId(item.getId());
        vo.setItemCode(item.getItemCode());
        vo.setItemName(item.getItemName());
        vo.setItemCategory(item.getItemCategory() != null ? item.getItemCategory().name() : null);
        vo.setItemCategoryDesc(item.getItemCategory() != null ? item.getItemCategory().getDescription() : null);
        vo.setItemUnit(item.getItemUnit());
        vo.setItemSpec(item.getItemSpec());
        vo.setStandardPrice(item.getStandardPrice());
        vo.setRetailPrice(item.getRetailPrice());
        vo.setWholesalePrice(item.getWholesalePrice());
        vo.setInsuranceType(item.getInsuranceType() != null ? item.getInsuranceType().name() : null);
        vo.setInsuranceTypeDesc(item.getInsuranceType() != null ? item.getInsuranceType().getDescription() : null);
        vo.setInsuranceCode(item.getInsuranceCode());
        vo.setInsurancePrice(item.getInsurancePrice());
        vo.setReimbursementRatio(item.getReimbursementRatio());
        vo.setEffectiveDate(item.getEffectiveDate());
        vo.setExpireDate(item.getExpireDate());
        vo.setVersionNo(item.getVersionNo());
        vo.setStatus(item.getStatus() != null ? item.getStatus().name() : null);
        vo.setStatusDesc(item.getStatus() != null ? item.getStatus().getDescription() : null);
        vo.setRemark(item.getRemark());
        vo.setCreateTime(item.getCreateTime());
        vo.setUpdateTime(item.getUpdateTime());
        return vo;
    }
}