package com.yhj.his.module.system.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.system.dto.AuditLogQueryDTO;
import com.yhj.his.module.system.entity.AuditLog;
import com.yhj.his.module.system.enums.AuditLevel;
import com.yhj.his.module.system.enums.AuditType;
import com.yhj.his.module.system.repository.AuditLogRepository;
import com.yhj.his.module.system.service.AuditLogService;
import com.yhj.his.module.system.vo.AuditLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审计日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public Result<Void> log(AuditLog log) {
        if (log.getAuditTime() == null) {
            log.setAuditTime(LocalDateTime.now());
        }
        if (log.getAuditLevel() == null) {
            log.setAuditLevel(AuditLevel.NORMAL);
        }
        auditLogRepository.save(log);
        return Result.successVoid();
    }

    @Override
    public Result<PageResult<AuditLogVO>> page(AuditLogQueryDTO dto) {
        AuditType type = dto.getAuditType() != null ? AuditType.fromCode(dto.getAuditType()) : null;
        AuditLevel level = dto.getAuditLevel() != null ? AuditLevel.fromCode(dto.getAuditLevel()) : null;

        Page<AuditLog> page = auditLogRepository.findByCondition(
                dto.getUserId(),
                dto.getLoginName(),
                type,
                level,
                dto.getAuditEvent(),
                dto.getStartTime(),
                dto.getEndTime(),
                PageUtils.ofDescByCreateTime(dto.getPageNum(), dto.getPageSize())
        );

        List<AuditLogVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, page.getTotalElements(), dto.getPageNum(), dto.getPageSize()));
    }

    @Override
    public Result<List<AuditLogVO>> getRecentLogs(String userId) {
        List<AuditLog> logs = auditLogRepository.findTop10ByUserIdOrderByAuditTimeDesc(userId);
        List<AuditLogVO> list = logs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<AuditLogVO>> getUnAlertedCriticalLogs() {
        List<AuditLog> logs = auditLogRepository.findByAuditLevelAndIsAlertedFalse(AuditLevel.CRITICAL);
        List<AuditLogVO> list = logs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result<Void> markAlerted(String logId) {
        AuditLog log = auditLogRepository.findById(logId).orElse(null);
        if (log != null) {
            log.setIsAlerted(true);
            log.setAlertTime(LocalDateTime.now());
            auditLogRepository.save(log);
        }
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> deleteHistoryLogs(Integer days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        auditLogRepository.deleteByAuditTimeBefore(threshold);
        log.info("已删除{}天前的审计日志", days);
        return Result.successVoid();
    }

    /**
     * 转换AuditLog实体到VO
     */
    private AuditLogVO convertToVO(AuditLog log) {
        AuditLogVO vo = new AuditLogVO();
        vo.setId(log.getId());
        vo.setAuditType(log.getAuditType() != null ? log.getAuditType().getCode() : null);
        vo.setUserId(log.getUserId());
        vo.setLoginName(log.getLoginName());
        vo.setRealName(log.getRealName());
        vo.setAuditEvent(log.getAuditEvent());
        vo.setAuditDesc(log.getAuditDesc());
        vo.setAuditLevel(log.getAuditLevel() != null ? log.getAuditLevel().getCode() : null);
        vo.setBeforeData(log.getBeforeData());
        vo.setAfterData(log.getAfterData());
        vo.setClientIp(log.getClientIp());
        vo.setAuditTime(log.getAuditTime());
        vo.setIsAlerted(log.getIsAlerted());
        vo.setAlertTime(log.getAlertTime());
        vo.setAlertWay(log.getAlertWay());
        vo.setCreateTime(log.getCreateTime());
        return vo;
    }
}