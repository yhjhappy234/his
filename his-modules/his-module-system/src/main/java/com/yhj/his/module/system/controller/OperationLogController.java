package com.yhj.his.module.system.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.OperationLogQueryDTO;
import com.yhj.his.module.system.service.OperationLogService;
import com.yhj.his.module.system.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志管理", description = "操作日志查询、删除等接口")
@RestController
@RequestMapping("/api/system/v1/log/operation")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    public Result<PageResult<OperationLogVO>> page(OperationLogQueryDTO dto) {
        return operationLogService.page(dto);
    }

    @Operation(summary = "获取用户最近的操作日志")
    @GetMapping("/recent/{userId}")
    public Result<List<OperationLogVO>> getRecentLogs(@Parameter(description = "用户ID") @PathVariable String userId) {
        return operationLogService.getRecentLogs(userId);
    }

    @Operation(summary = "删除历史日志")
    @DeleteMapping("/history")
    public Result<Void> deleteHistoryLogs(@Parameter(description = "保留天数") @RequestParam(defaultValue = "180") Integer days) {
        return operationLogService.deleteHistoryLogs(days);
    }
}