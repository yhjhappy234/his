package com.yhj.his.module.lis.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestItemCreateDTO;
import com.yhj.his.module.lis.dto.TestItemUpdateDTO;
import com.yhj.his.module.lis.entity.TestItem;
import com.yhj.his.module.lis.enums.TestItemCategory;
import com.yhj.his.module.lis.enums.TestItemStatus;
import com.yhj.his.module.lis.repository.TestItemRepository;
import com.yhj.his.module.lis.service.TestItemService;
import com.yhj.his.module.lis.vo.TestItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 检验项目服务实现
 */
@Service
@RequiredArgsConstructor
public class TestItemServiceImpl implements TestItemService {

    private final TestItemRepository testItemRepository;

    @Override
    @Transactional
    public TestItemVO create(TestItemCreateDTO dto) {
        if (existsByItemCode(dto.getItemCode())) {
            throw new IllegalArgumentException("项目编码已存在: " + dto.getItemCode());
        }

        TestItem item = new TestItem();
        item.setItemCode(dto.getItemCode());
        item.setItemName(dto.getItemName());
        item.setItemNameEn(dto.getItemNameEn());
        item.setPinyinCode(dto.getPinyinCode());
        item.setCategory(TestItemCategory.valueOf(dto.getCategory()));
        item.setSpecimenType(com.yhj.his.module.lis.enums.SpecimenType.valueOf(dto.getSpecimenType()));
        item.setTestMethod(dto.getTestMethod());
        item.setUnit(dto.getUnit());
        item.setReferenceMin(dto.getReferenceMin());
        item.setReferenceMax(dto.getReferenceMax());
        item.setReferenceText(dto.getReferenceText());
        item.setCriticalLow(dto.getCriticalLow());
        item.setCriticalHigh(dto.getCriticalHigh());
        item.setCritical(dto.getCritical() != null ? dto.getCritical() : false);
        item.setPrice(dto.getPrice());
        item.setTurnaroundTime(dto.getTurnaroundTime());
        item.setInstrumentId(dto.getInstrumentId());
        item.setReagentId(dto.getReagentId());
        item.setStatus(TestItemStatus.NORMAL);
        item.setRemark(dto.getRemark());

        TestItem saved = testItemRepository.save(item);
        return toVO(saved);
    }

    @Override
    @Transactional
    public TestItemVO update(String id, TestItemUpdateDTO dto) {
        TestItem item = testItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验项目不存在: " + id));

        if (dto.getItemName() != null) item.setItemName(dto.getItemName());
        if (dto.getItemNameEn() != null) item.setItemNameEn(dto.getItemNameEn());
        if (dto.getPinyinCode() != null) item.setPinyinCode(dto.getPinyinCode());
        if (dto.getCategory() != null) item.setCategory(TestItemCategory.valueOf(dto.getCategory()));
        if (dto.getSpecimenType() != null) item.setSpecimenType(com.yhj.his.module.lis.enums.SpecimenType.valueOf(dto.getSpecimenType()));
        if (dto.getTestMethod() != null) item.setTestMethod(dto.getTestMethod());
        if (dto.getUnit() != null) item.setUnit(dto.getUnit());
        if (dto.getReferenceMin() != null) item.setReferenceMin(dto.getReferenceMin());
        if (dto.getReferenceMax() != null) item.setReferenceMax(dto.getReferenceMax());
        if (dto.getReferenceText() != null) item.setReferenceText(dto.getReferenceText());
        if (dto.getCriticalLow() != null) item.setCriticalLow(dto.getCriticalLow());
        if (dto.getCriticalHigh() != null) item.setCriticalHigh(dto.getCriticalHigh());
        if (dto.getCritical() != null) item.setCritical(dto.getCritical());
        if (dto.getPrice() != null) item.setPrice(dto.getPrice());
        if (dto.getTurnaroundTime() != null) item.setTurnaroundTime(dto.getTurnaroundTime());
        if (dto.getInstrumentId() != null) item.setInstrumentId(dto.getInstrumentId());
        if (dto.getReagentId() != null) item.setReagentId(dto.getReagentId());
        if (dto.getStatus() != null) item.setStatus(TestItemStatus.valueOf(dto.getStatus()));
        if (dto.getRemark() != null) item.setRemark(dto.getRemark());

        TestItem saved = testItemRepository.save(item);
        return toVO(saved);
    }

    @Override
    public TestItemVO getById(String id) {
        TestItem item = testItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验项目不存在: " + id));
        return toVO(item);
    }

    @Override
    public TestItemVO getByItemCode(String itemCode) {
        TestItem item = testItemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> new IllegalArgumentException("检验项目不存在: " + itemCode));
        return toVO(item);
    }

    @Override
    public PageResult<TestItemVO> list(Pageable pageable) {
        Page<TestItem> page = testItemRepository.findAll(pageable);
        List<TestItemVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public PageResult<TestItemVO> search(String keyword, TestItemStatus status, Pageable pageable) {
        String searchKeyword = "%" + keyword + "%";
        Page<TestItem> page = testItemRepository.findByKeywordAndStatus(searchKeyword, status, pageable);
        List<TestItemVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<TestItemVO> listByCategory(TestItemCategory category) {
        return testItemRepository.findByCategory(category).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<TestItemVO> listByStatus(TestItemStatus status) {
        return testItemRepository.findByStatusOrderByItemName(status).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<TestItemVO> listCriticalItems() {
        return testItemRepository.findByCriticalTrueAndStatus(TestItemStatus.NORMAL).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(String id) {
        TestItem item = testItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验项目不存在: " + id));
        item.setDeleted(true);
        testItemRepository.save(item);
    }

    @Override
    @Transactional
    public TestItemVO enable(String id) {
        TestItem item = testItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验项目不存在: " + id));
        item.setStatus(TestItemStatus.NORMAL);
        TestItem saved = testItemRepository.save(item);
        return toVO(saved);
    }

    @Override
    @Transactional
    public TestItemVO disable(String id) {
        TestItem item = testItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("检验项目不存在: " + id));
        item.setStatus(TestItemStatus.DISABLED);
        TestItem saved = testItemRepository.save(item);
        return toVO(saved);
    }

    @Override
    public boolean existsByItemCode(String itemCode) {
        return testItemRepository.existsByItemCode(itemCode);
    }

    private TestItemVO toVO(TestItem item) {
        TestItemVO vo = new TestItemVO();
        vo.setId(item.getId());
        vo.setItemCode(item.getItemCode());
        vo.setItemName(item.getItemName());
        vo.setItemNameEn(item.getItemNameEn());
        vo.setPinyinCode(item.getPinyinCode());
        vo.setCategory(item.getCategory().name());
        vo.setCategoryDesc(item.getCategory().getDescription());
        vo.setSpecimenType(item.getSpecimenType().name());
        vo.setSpecimenTypeDesc(item.getSpecimenType().getDescription());
        vo.setTestMethod(item.getTestMethod());
        vo.setUnit(item.getUnit());
        vo.setReferenceMin(item.getReferenceMin());
        vo.setReferenceMax(item.getReferenceMax());
        vo.setReferenceText(item.getReferenceText());
        vo.setCriticalLow(item.getCriticalLow());
        vo.setCriticalHigh(item.getCriticalHigh());
        vo.setCritical(item.getCritical());
        vo.setPrice(item.getPrice());
        vo.setTurnaroundTime(item.getTurnaroundTime());
        vo.setInstrumentId(item.getInstrumentId());
        vo.setReagentId(item.getReagentId());
        vo.setStatus(item.getStatus().name());
        vo.setStatusDesc(item.getStatus().getDescription());
        vo.setRemark(item.getRemark());
        vo.setCreateTime(item.getCreateTime());
        vo.setUpdateTime(item.getUpdateTime());
        return vo;
    }
}