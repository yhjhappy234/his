package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.AuditLogQueryDTO;
import com.yhj.his.module.system.entity.AuditLog;
import com.yhj.his.module.system.vo.AuditLogVO;

/**
 * 审计日志服务接口
 */
public interface AuditLogService {

    /**
     * 记录审计日志
     *
     * @param log 审计日志
     * @return 记录结果
     */
    Result<Void> log(AuditLog log);

    /**
     * 分页查询审计日志
     *
     * @param dto 查询条件
     * @return 日志分页列表
     */
    Result<PageResult<AuditLogVO>> page(AuditLogQueryDTO dto);

    /**
     * 获取用户最近的审计日志
     *
     * @param userId 用户ID
     * @return 日志列表
     */
    Result<java.util.List<AuditLogVO>> getRecentLogs(String userId);

    /**
     * 获取未告警的严重级别日志
     *
     * @return 日志列表
     */
    Result<java.util.List<AuditLogVO>> getUnAlertedCriticalLogs();

    /**
     * 标记已告警
     *
     * @param logId 日志ID
     * @return 更新结果
     */
    Result<Void> markAlerted(String logId);

    /**
     * 删除历史日志
     *
     * @param days 保留天数
     * @return 删除结果
     */
    Result<Void> deleteHistoryLogs(Integer days);
}