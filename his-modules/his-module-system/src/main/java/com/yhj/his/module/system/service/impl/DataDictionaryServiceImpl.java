package com.yhj.his.module.system.service.impl;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.system.dto.DataDictionaryDTO;
import com.yhj.his.module.system.entity.DataDictionary;
import com.yhj.his.module.system.repository.DataDictionaryRepository;
import com.yhj.his.module.system.service.DataDictionaryService;
import com.yhj.his.module.system.vo.DataDictionaryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据字典服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataDictionaryServiceImpl implements DataDictionaryService {

    private final DataDictionaryRepository dataDictionaryRepository;

    @Override
    @Transactional
    public Result<DataDictionaryVO> create(DataDictionaryDTO dto) {
        // 检查字典类型和编码是否存在
        if (dto.getDictCode() != null &&
            dataDictionaryRepository.existsByDictTypeAndDictCodeAndDeletedFalse(dto.getDictType(), dto.getDictCode())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "字典编码已存在");
        }

        DataDictionary dict = new DataDictionary();
        dict.setDictType(dto.getDictType());
        dict.setDictCode(dto.getDictCode());
        dict.setDictName(dto.getDictName());
        dict.setDictValue(dto.getDictValue());
        dict.setParentCode(dto.getParentCode());
        dict.setDescription(dto.getDescription());

        // 计算层级
        if (dto.getParentCode() != null) {
            DataDictionary parent = dataDictionaryRepository.findByDictTypeAndDictCodeAndDeletedFalse(
                    dto.getDictType(), dto.getParentCode()).orElse(null);
            if (parent != null) {
                dict.setDictLevel(parent.getDictLevel() + 1);
            } else {
                dict.setDictLevel(1);
            }
        } else {
            dict.setDictLevel(dto.getDictLevel() != null ? dto.getDictLevel() : 1);
        }

        dict.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        dict.setIsEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true);
        dict.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);

        dict = dataDictionaryRepository.save(dict);
        return Result.success("创建成功", convertToVO(dict));
    }

    @Override
    @Transactional
    public Result<DataDictionaryVO> update(DataDictionaryDTO dto) {
        DataDictionary dict = dataDictionaryRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "字典项不存在"));

        if (dict.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "字典项已删除");
        }

        dict.setDictName(dto.getDictName());
        dict.setDictValue(dto.getDictValue());
        dict.setParentCode(dto.getParentCode());
        dict.setSortOrder(dto.getSortOrder());
        dict.setIsEnabled(dto.getIsEnabled());
        dict.setIsDefault(dto.getIsDefault());
        dict.setDescription(dto.getDescription());

        // 更新层级
        if (dto.getParentCode() != null && !dto.getParentCode().equals(dict.getParentCode())) {
            DataDictionary parent = dataDictionaryRepository.findByDictTypeAndDictCodeAndDeletedFalse(
                    dto.getDictType(), dto.getParentCode()).orElse(null);
            if (parent != null) {
                dict.setDictLevel(parent.getDictLevel() + 1);
            }
        }

        dict = dataDictionaryRepository.save(dict);
        return Result.success("更新成功", convertToVO(dict));
    }

    @Override
    @Transactional
    public Result<Void> delete(String dictId) {
        DataDictionary dict = dataDictionaryRepository.findById(dictId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "字典项不存在"));

        if (dict.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "字典项已删除");
        }

        // 检查是否有子字典项
        List<DataDictionary> children = dataDictionaryRepository.findByDictTypeAndParentCodeAndDeletedFalseOrderBySortOrderAsc(
                dict.getDictType(), dict.getDictCode());
        if (!children.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "该字典项有" + children.size() + "个子项，无法删除");
        }

        // 逻辑删除
        dict.setDeleted(true);
        dataDictionaryRepository.save(dict);

        return Result.successVoid();
    }

    @Override
    public Result<DataDictionaryVO> getById(String dictId) {
        DataDictionary dict = dataDictionaryRepository.findById(dictId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "字典项不存在"));

        if (dict.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "字典项已删除");
        }

        return Result.success(convertToVO(dict));
    }

    @Override
    public Result<PageResult<DataDictionaryVO>> page(String dictType, String dictName, String dictCode,
                                                      Boolean isEnabled, Integer pageNum, Integer pageSize) {
        Page<DataDictionary> page = dataDictionaryRepository.findByCondition(dictType, dictName, dictCode, isEnabled,
                PageUtils.of(pageNum, pageSize));

        List<DataDictionaryVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<List<DataDictionaryVO>> listByType(String dictType) {
        List<DataDictionary> dicts = dataDictionaryRepository.findByDictTypeAndDeletedFalseOrderBySortOrderAsc(dictType);
        List<DataDictionaryVO> list = dicts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<DataDictionaryVO>> listEnabledByType(String dictType) {
        List<DataDictionary> dicts = dataDictionaryRepository.findByDictTypeAndIsEnabledTrueAndDeletedFalseOrderBySortOrderAsc(dictType);
        List<DataDictionaryVO> list = dicts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<String>> listAllTypes() {
        List<String> types = dataDictionaryRepository.findAllDictTypes();
        return Result.success(types);
    }

    @Override
    public Result<List<DataDictionaryVO>> getTree(String dictType) {
        List<DataDictionary> dicts = dataDictionaryRepository.findByDictTypeAndDeletedFalseOrderBySortOrderAsc(dictType);
        List<DataDictionaryVO> tree = buildTree(dicts, null);
        return Result.success(tree);
    }

    @Override
    public Result<DataDictionaryVO> getDefault(String dictType) {
        DataDictionary dict = dataDictionaryRepository.findByDictTypeAndIsEnabledTrueAndIsDefaultTrueAndDeletedFalse(dictType)
                .orElse(null);
        return Result.success(dict != null ? convertToVO(dict) : null);
    }

    /**
     * 构建字典树
     */
    private List<DataDictionaryVO> buildTree(List<DataDictionary> dicts, String parentCode) {
        List<DataDictionaryVO> tree = new ArrayList<>();

        for (DataDictionary dict : dicts) {
            String dictParentCode = dict.getParentCode();
            boolean matchParent = (parentCode == null && dictParentCode == null) ||
                                  (parentCode != null && parentCode.equals(dictParentCode));

            if (matchParent) {
                DataDictionaryVO vo = convertToVO(dict);
                // 递归构建子字典
                List<DataDictionaryVO> children = buildTree(dicts, dict.getDictCode());
                if (!children.isEmpty()) {
                    vo.setChildren(children);
                }
                tree.add(vo);
            }
        }

        return tree.stream()
                .sorted(Comparator.comparing(DataDictionaryVO::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * 转换DataDictionary实体到VO
     */
    private DataDictionaryVO convertToVO(DataDictionary dict) {
        DataDictionaryVO vo = new DataDictionaryVO();
        vo.setId(dict.getId());
        vo.setDictType(dict.getDictType());
        vo.setDictCode(dict.getDictCode());
        vo.setDictName(dict.getDictName());
        vo.setDictValue(dict.getDictValue());
        vo.setParentCode(dict.getParentCode());
        vo.setDictLevel(dict.getDictLevel());
        vo.setSortOrder(dict.getSortOrder());
        vo.setIsEnabled(dict.getIsEnabled());
        vo.setIsDefault(dict.getIsDefault());
        vo.setDescription(dict.getDescription());
        vo.setCreateTime(dict.getCreateTime());
        return vo;
    }
}