package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.OperationLogQueryDTO;
import com.yhj.his.module.system.entity.OperationLog;
import com.yhj.his.module.system.vo.OperationLogVO;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {

    /**
     * 记录操作日志
     *
     * @param log 操作日志
     * @return 记录结果
     */
    Result<Void> log(OperationLog log);

    /**
     * 分页查询操作日志
     *
     * @param dto 查询条件
     * @return 日志分页列表
     */
    Result<PageResult<OperationLogVO>> page(OperationLogQueryDTO dto);

    /**
     * 获取用户最近的操作日志
     *
     * @param userId 用户ID
     * @return 日志列表
     */
    Result<java.util.List<OperationLogVO>> getRecentLogs(String userId);

    /**
     * 删除历史日志
     *
     * @param days 保留天数
     * @return 删除结果
     */
    Result<Void> deleteHistoryLogs(Integer days);
}