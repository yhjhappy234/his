package com.yhj.his.module.system.service.impl;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.system.dto.SystemParameterDTO;
import com.yhj.his.module.system.entity.SystemParameter;
import com.yhj.his.module.system.repository.SystemParameterRepository;
import com.yhj.his.module.system.service.SystemParameterService;
import com.yhj.his.module.system.vo.SystemParameterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统参数服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemParameterServiceImpl implements SystemParameterService {

    private final SystemParameterRepository systemParameterRepository;

    @Override
    @Transactional
    public Result<SystemParameterVO> create(SystemParameterDTO dto) {
        // 检查参数编码是否存在
        if (dto.getParamCode() != null && systemParameterRepository.existsByParamCodeAndDeletedFalse(dto.getParamCode())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "参数编码已存在");
        }

        SystemParameter param = new SystemParameter();
        param.setParamCode(dto.getParamCode());
        param.setParamName(dto.getParamName());
        param.setParamValue(dto.getParamValue());
        param.setParamType(dto.getParamType());
        param.setParamGroup(dto.getParamGroup());
        param.setDescription(dto.getDescription());
        param.setIsSystem(dto.getIsSystem() != null ? dto.getIsSystem() : false);
        param.setIsEditable(dto.getIsEditable() != null ? dto.getIsEditable() : true);
        param.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        param = systemParameterRepository.save(param);
        return Result.success("创建成功", convertToVO(param));
    }

    @Override
    @Transactional
    public Result<SystemParameterVO> update(SystemParameterDTO dto) {
        SystemParameter param = systemParameterRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "参数不存在"));

        if (param.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "参数已删除");
        }

        // 检查是否可编辑
        if (!param.getIsEditable()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "该参数不可编辑");
        }

        param.setParamName(dto.getParamName());
        param.setParamValue(dto.getParamValue());
        param.setParamType(dto.getParamType());
        param.setParamGroup(dto.getParamGroup());
        param.setDescription(dto.getDescription());
        param.setSortOrder(dto.getSortOrder());

        param = systemParameterRepository.save(param);
        return Result.success("更新成功", convertToVO(param));
    }

    @Override
    @Transactional
    public Result<Void> delete(String paramId) {
        SystemParameter param = systemParameterRepository.findById(paramId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "参数不存在"));

        if (param.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "参数已删除");
        }

        // 检查是否为系统参数
        if (param.getIsSystem()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "系统参数不可删除");
        }

        // 逻辑删除
        param.setDeleted(true);
        systemParameterRepository.save(param);

        return Result.successVoid();
    }

    @Override
    public Result<SystemParameterVO> getById(String paramId) {
        SystemParameter param = systemParameterRepository.findById(paramId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "参数不存在"));

        if (param.getDeleted()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "参数已删除");
        }

        return Result.success(convertToVO(param));
    }

    @Override
    public Result<String> getValueByCode(String paramCode) {
        SystemParameter param = systemParameterRepository.findByParamCodeAndDeletedFalse(paramCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "参数不存在: " + paramCode));

        return Result.success(param.getParamValue());
    }

    @Override
    public Result<PageResult<SystemParameterVO>> page(String paramName, String paramCode, String paramGroup,
                                                      String paramType, Integer pageNum, Integer pageSize) {
        Page<SystemParameter> page = systemParameterRepository.findByCondition(paramName, paramCode, paramGroup, paramType,
                PageUtils.of(pageNum, pageSize));

        List<SystemParameterVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<List<SystemParameterVO>> listAll() {
        List<SystemParameter> params = systemParameterRepository.findByDeletedFalseOrderBySortOrderAsc();
        List<SystemParameterVO> list = params.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<SystemParameterVO>> listByGroup(String paramGroup) {
        List<SystemParameter> params = systemParameterRepository.findByParamGroupAndDeletedFalseOrderBySortOrderAsc(paramGroup);
        List<SystemParameterVO> list = params.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<Map<String, String>> getValuesByCodes(List<String> paramCodes) {
        List<SystemParameter> params = systemParameterRepository.findByParamCodeInAndDeletedFalse(paramCodes);
        Map<String, String> map = params.stream()
                .collect(Collectors.toMap(SystemParameter::getParamCode,
                        p -> p.getParamValue() != null ? p.getParamValue() : "",
                        (v1, v2) -> v1));
        return Result.success(map);
    }

    /**
     * 转换SystemParameter实体到VO
     */
    private SystemParameterVO convertToVO(SystemParameter param) {
        SystemParameterVO vo = new SystemParameterVO();
        vo.setId(param.getId());
        vo.setParamCode(param.getParamCode());
        vo.setParamName(param.getParamName());
        vo.setParamValue(param.getParamValue());
        vo.setParamType(param.getParamType());
        vo.setParamGroup(param.getParamGroup());
        vo.setDescription(param.getDescription());
        vo.setIsSystem(param.getIsSystem());
        vo.setIsEditable(param.getIsEditable());
        vo.setSortOrder(param.getSortOrder());
        vo.setCreateTime(param.getCreateTime());
        vo.setUpdateTime(param.getUpdateTime());
        return vo;
    }
}