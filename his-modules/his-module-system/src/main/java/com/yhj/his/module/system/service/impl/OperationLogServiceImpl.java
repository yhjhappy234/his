package com.yhj.his.module.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.system.dto.OperationLogQueryDTO;
import com.yhj.his.module.system.entity.OperationLog;
import com.yhj.his.module.system.enums.OperationResult;
import com.yhj.his.module.system.repository.OperationLogRepository;
import com.yhj.his.module.system.service.OperationLogService;
import com.yhj.his.module.system.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository operationLogRepository;

    @Override
    public Result<Void> log(OperationLog log) {
        if (log.getLogNo() == null) {
            log.setLogNo(IdUtil.fastSimpleUUID());
        }
        if (log.getOperationTime() == null) {
            log.setOperationTime(LocalDateTime.now());
        }
        operationLogRepository.save(log);
        return Result.successVoid();
    }

    @Override
    public Result<PageResult<OperationLogVO>> page(OperationLogQueryDTO dto) {
        OperationResult result = dto.getOperationResult() != null ?
                OperationResult.fromCode(dto.getOperationResult()) : null;

        Page<OperationLog> page = operationLogRepository.findByCondition(
                dto.getUserId(),
                dto.getLoginName(),
                dto.getOperationType(),
                dto.getOperationModule(),
                result,
                dto.getStartTime(),
                dto.getEndTime(),
                PageUtils.ofDescByCreateTime(dto.getPageNum(), dto.getPageSize())
        );

        List<OperationLogVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, page.getTotalElements(), dto.getPageNum(), dto.getPageSize()));
    }

    @Override
    public Result<List<OperationLogVO>> getRecentLogs(String userId) {
        List<OperationLog> logs = operationLogRepository.findTop10ByUserIdOrderByOperationTimeDesc(userId);
        List<OperationLogVO> list = logs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result<Void> deleteHistoryLogs(Integer days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        operationLogRepository.deleteByOperationTimeBefore(threshold);
        log.info("已删除{}天前的操作日志", days);
        return Result.successVoid();
    }

    /**
     * 转换OperationLog实体到VO
     */
    private OperationLogVO convertToVO(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(log.getId());
        vo.setLogNo(log.getLogNo());
        vo.setUserId(log.getUserId());
        vo.setLoginName(log.getLoginName());
        vo.setRealName(log.getRealName());
        vo.setDeptId(log.getDeptId());
        vo.setDeptName(log.getDeptName());
        vo.setOperationType(log.getOperationType());
        vo.setOperationModule(log.getOperationModule());
        vo.setOperationFunc(log.getOperationFunc());
        vo.setOperationDesc(log.getOperationDesc());
        vo.setRequestMethod(log.getRequestMethod());
        vo.setRequestUrl(log.getRequestUrl());
        vo.setRequestParam(log.getRequestParam());
        vo.setResponseData(log.getResponseData());
        vo.setOperationResult(log.getOperationResult() != null ? log.getOperationResult().getCode() : null);
        vo.setErrorMsg(log.getErrorMsg());
        vo.setOperationTime(log.getOperationTime());
        vo.setDuration(log.getDuration());
        vo.setClientIp(log.getClientIp());
        vo.setServerIp(log.getServerIp());
        vo.setCreateTime(log.getCreateTime());
        return vo;
    }
}