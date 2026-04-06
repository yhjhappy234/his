package com.yhj.his.module.system.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.AuditLogQueryDTO;
import com.yhj.his.module.system.service.AuditLogService;
import com.yhj.his.module.system.vo.AuditLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审计日志控制器
 */
@Tag(name = "审计日志管理", description = "审计日志查询、告警等接口")
@RestController
@RequestMapping("/api/system/v1/log/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Operation(summary = "分页查询审计日志")
    @GetMapping("/page")
    public Result<PageResult<AuditLogVO>> page(AuditLogQueryDTO dto) {
        return auditLogService.page(dto);
    }

    @Operation(summary = "获取用户最近的审计日志")
    @GetMapping("/recent/{userId}")
    public Result<List<AuditLogVO>> getRecentLogs(@Parameter(description = "用户ID") @PathVariable String userId) {
        return auditLogService.getRecentLogs(userId);
    }

    @Operation(summary = "获取未告警的严重级别日志")
    @GetMapping("/unalerted")
    public Result<List<AuditLogVO>> getUnAlertedCriticalLogs() {
        return auditLogService.getUnAlertedCriticalLogs();
    }

    @Operation(summary = "标记已告警")
    @PutMapping("/alerted/{logId}")
    public Result<Void> markAlerted(@Parameter(description = "日志ID") @PathVariable String logId) {
        return auditLogService.markAlerted(logId);
    }

    @Operation(summary = "删除历史日志")
    @DeleteMapping("/history")
    public Result<Void> deleteHistoryLogs(@Parameter(description = "保留天数") @RequestParam(defaultValue = "180") Integer days) {
        return auditLogService.deleteHistoryLogs(days);
    }
}