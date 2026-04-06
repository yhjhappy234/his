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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 检查项目管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExamItemServiceImpl implements ExamItemService {

    private final ExamItemRepository examItemRepository;

    @Override
    @Transactional
    public ExamItemVO createItem(ExamItemDTO dto) {
        // 检查项目编码是否已存在
        if (dto.getItemCode() != null && examItemRepository.findByItemCode(dto.getItemCode()).isPresent()) {
            throw new BusinessException("项目编码已存在");
        }

        ExamItem item = new ExamItem();
        item.setItemCode(dto.getItemCode());
        item.setItemName(dto.getItemName());
        item.setExamType(dto.getExamType());
        item.setExamPart(dto.getExamPart());
        item.setExamMethod(dto.getExamMethod());
        item.setPrice(dto.getPrice());
        item.setTurnaroundTime(dto.getTurnaroundTime());
        item.setEquipmentType(dto.getEquipmentType());
        item.setStatus(dto.getStatus());
        item.setNeedContrast(dto.getNeedContrast());
        item.setNeedSchedule(dto.getNeedSchedule());
        item.setExamDuration(dto.getExamDuration());
        item.setPreparationRequirement(dto.getPreparationRequirement());
        item.setAttention(dto.getAttention());
        item.setSortOrder(dto.getSortOrder());
        item.setRemark(dto.getRemark());

        item = examItemRepository.save(item);
        log.info("创建检查项目成功: itemCode={}, itemName={}", item.getItemCode(), item.getItemName());

        return convertToVO(item);
    }

    @Override
    @Transactional
    public ExamItemVO updateItem(ExamItemDTO dto) {
        ExamItem item = examItemRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException("项目不存在"));

        // 如果修改了编码，检查是否重复
        if (dto.getItemCode() != null && !dto.getItemCode().equals(item.getItemCode())) {
            if (examItemRepository.findByItemCode(dto.getItemCode()).isPresent()) {
                throw new BusinessException("项目编码已存在");
            }
        }

        item.setItemCode(dto.getItemCode());
        item.setItemName(dto.getItemName());
        item.setExamType(dto.getExamType());
        item.setExamPart(dto.getExamPart());
        item.setExamMethod(dto.getExamMethod());
        item.setPrice(dto.getPrice());
        item.setTurnaroundTime(dto.getTurnaroundTime());
        item.setEquipmentType(dto.getEquipmentType());
        item.setStatus(dto.getStatus());
        item.setNeedContrast(dto.getNeedContrast());
        item.setNeedSchedule(dto.getNeedSchedule());
        item.setExamDuration(dto.getExamDuration());
        item.setPreparationRequirement(dto.getPreparationRequirement());
        item.setAttention(dto.getAttention());
        item.setSortOrder(dto.getSortOrder());
        item.setRemark(dto.getRemark());

        item = examItemRepository.save(item);
        log.info("更新检查项目成功: itemId={}", item.getId());

        return convertToVO(item);
    }

    @Override
    @Transactional
    public void deleteItem(String itemId) {
        ExamItem item = examItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("项目不存在"));

        examItemRepository.delete(item);
        log.info("删除检查项目成功: itemId={}", itemId);
    }

    @Override
    @Transactional
    public ExamItemVO enableItem(String itemId) {
        ExamItem item = examItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("项目不存在"));

        item.setStatus("启用");
        item = examItemRepository.save(item);
        log.info("启用检查项目成功: itemId={}", itemId);

        return convertToVO(item);
    }

    @Override
    @Transactional
    public ExamItemVO disableItem(String itemId) {
        ExamItem item = examItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("项目不存在"));

        item.setStatus("停用");
        item = examItemRepository.save(item);
        log.info("停用检查项目成功: itemId={}", itemId);

        return convertToVO(item);
    }

    @Override
    public ExamItemVO getItemById(String itemId) {
        ExamItem item = examItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("项目不存在"));
        return convertToVO(item);
    }

    @Override
    public ExamItemVO getItemByCode(String itemCode) {
        ExamItem item = examItemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> new BusinessException("项目不存在"));
        return convertToVO(item);
    }

    @Override
    public List<ExamItemVO> getActiveItems() {
        List<ExamItem> items = examItemRepository.findActiveItems();
        return items.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<ExamItemVO> getItemsByExamType(String examType) {
        List<ExamItem> items = examItemRepository.findByExamType(examType);
        return items.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<ExamItemVO> queryItems(String itemCode, String itemName, String examType, String status, Integer pageNum, Integer pageSize) {
        Page<ExamItem> page = examItemRepository.findByConditions(
                itemCode, itemName, examType, null, status,
                PageUtils.toPageable(pageNum, pageSize)
        );
        List<ExamItemVO> list = page.getContent().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public List<ExamItemVO> getContrastItems() {
        List<ExamItem> items = examItemRepository.findContrastItems();
        return items.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ExamItemVO> batchCreateItems(List<ExamItemDTO> dtoList) {
        return dtoList.stream()
                .map(this::createItem)
                .collect(Collectors.toList());
    }

    private ExamItemVO convertToVO(ExamItem item) {
        ExamItemVO vo = new ExamItemVO();
        vo.setId(item.getId());
        vo.setItemCode(item.getItemCode());
        vo.setItemName(item.getItemName());
        vo.setExamType(item.getExamType());
        vo.setExamPart(item.getExamPart());
        vo.setExamMethod(item.getExamMethod());
        vo.setPrice(item.getPrice());
        vo.setTurnaroundTime(item.getTurnaroundTime());
        vo.setEquipmentType(item.getEquipmentType());
        vo.setStatus(item.getStatus());
        vo.setNeedContrast(item.getNeedContrast());
        vo.setNeedSchedule(item.getNeedSchedule());
        vo.setExamDuration(item.getExamDuration());
        vo.setPreparationRequirement(item.getPreparationRequirement());
        vo.setAttention(item.getAttention());
        vo.setSortOrder(item.getSortOrder());
        vo.setRemark(item.getRemark());
        vo.setCreateTime(item.getCreateTime() != null ? item.getCreateTime().toString() : null);
        return vo;
    }
}