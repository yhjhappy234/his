package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.AuditLogQueryDTO;
import com.yhj.his.module.system.entity.AuditLog;
import com.yhj.his.module.system.enums.AuditLevel;
import com.yhj.his.module.system.enums.AuditType;
import com.yhj.his.module.system.repository.AuditLogRepository;
import com.yhj.his.module.system.service.impl.AuditLogServiceImpl;
import com.yhj.his.module.system.vo.AuditLogVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuditLogService 单元测试
 * 目标覆盖率: 90%+
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("审计日志服务测试")
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private AuditLog testAuditLog;
    private AuditLogQueryDTO testQueryDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试审计日志
        testAuditLog = new AuditLog();
        testAuditLog.setId("audit-001");
        testAuditLog.setAuditType(AuditType.LOGIN);
        testAuditLog.setUserId("user-001");
        testAuditLog.setLoginName("testuser");
        testAuditLog.setRealName("张三");
        testAuditLog.setAuditEvent("用户登录");
        testAuditLog.setAuditDesc("用户登录成功");
        testAuditLog.setAuditLevel(AuditLevel.NORMAL);
        testAuditLog.setClientIp("192.168.1.1");
        testAuditLog.setAuditTime(LocalDateTime.now());
        testAuditLog.setIsAlerted(false);
        testAuditLog.setCreateTime(LocalDateTime.now());

        // 初始化查询DTO
        testQueryDTO = new AuditLogQueryDTO();
        testQueryDTO.setUserId("user-001");
        testQueryDTO.setPageNum(1);
        testQueryDTO.setPageSize(10);
    }

    @Nested
    @DisplayName("记录审计日志测试")
    class LogTests {

        @Test
        @DisplayName("记录审计日志成功")
        void logSuccess() {
            // Given
            when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
                AuditLog log = invocation.getArgument(0);
                log.setId("audit-new");
                return log;
            });

            // When
            Result<Void> result = auditLogService.log(testAuditLog);

            // Then
            assertEquals(0, result.getCode());
            verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("记录审计日志自动设置时间")
        void logAutoSetTime() {
            // Given
            testAuditLog.setAuditTime(null);
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

            // When
            Result<Void> result = auditLogService.log(testAuditLog);

            // Then
            assertEquals(0, result.getCode());
            assertNotNull(testAuditLog.getAuditTime());
        }

        @Test
        @DisplayName("记录审计日志自动设置级别")
        void logAutoSetLevel() {
            // Given
            testAuditLog.setAuditLevel(null);
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

            // When
            Result<Void> result = auditLogService.log(testAuditLog);

            // Then
            assertEquals(0, result.getCode());
            assertEquals(AuditLevel.NORMAL, testAuditLog.getAuditLevel());
        }
    }

    @Nested
    @DisplayName("分页查询审计日志测试")
    class PageTests {

        @Test
        @DisplayName("分页查询成功")
        void pageSuccess() {
            // Given
            Page<AuditLog> page = new PageImpl<>(Arrays.asList(testAuditLog));
            when(auditLogRepository.findByCondition(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // When
            Result<PageResult<AuditLogVO>> result = auditLogService.page(testQueryDTO);

            // Then
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getList().size());
        }

        @Test
        @DisplayName("分页查询空结果")
        void pageEmpty() {
            // Given
            Page<AuditLog> emptyPage = new PageImpl<>(Collections.emptyList());
            when(auditLogRepository.findByCondition(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // When
            Result<PageResult<AuditLogVO>> result = auditLogService.page(testQueryDTO);

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().getList().isEmpty());
        }

        @Test
        @DisplayName("分页查询带时间范围")
        void pageWithTimeRange() {
            // Given
            testQueryDTO.setStartTime(LocalDateTime.now().minusDays(7));
            testQueryDTO.setEndTime(LocalDateTime.now());
            Page<AuditLog> page = new PageImpl<>(Arrays.asList(testAuditLog));
            when(auditLogRepository.findByCondition(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // When
            Result<PageResult<AuditLogVO>> result = auditLogService.page(testQueryDTO);

            // Then
            assertEquals(0, result.getCode());
        }
    }

    @Nested
    @DisplayName("获取用户最近审计日志测试")
    class GetRecentLogsTests {

        @Test
        @DisplayName("获取用户最近日志成功")
        void getRecentLogsSuccess() {
            // Given
            when(auditLogRepository.findTop10ByUserIdOrderByAuditTimeDesc("user-001"))
                    .thenReturn(Arrays.asList(testAuditLog));

            // When
            Result<java.util.List<AuditLogVO>> result = auditLogService.getRecentLogs("user-001");

            // Then
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("用户无审计日志")
        void getRecentLogsEmpty() {
            // Given
            when(auditLogRepository.findTop10ByUserIdOrderByAuditTimeDesc("user-001"))
                    .thenReturn(Collections.emptyList());

            // When
            Result<java.util.List<AuditLogVO>> result = auditLogService.getRecentLogs("user-001");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("获取未告警严重级别日志测试")
    class GetUnAlertedCriticalLogsTests {

        @Test
        @DisplayName("获取未告警严重日志成功")
        void getUnAlertedCriticalLogsSuccess() {
            // Given
            testAuditLog.setAuditLevel(AuditLevel.CRITICAL);
            when(auditLogRepository.findByAuditLevelAndIsAlertedFalse(AuditLevel.CRITICAL))
                    .thenReturn(Arrays.asList(testAuditLog));

            // When
            Result<java.util.List<AuditLogVO>> result = auditLogService.getUnAlertedCriticalLogs();

            // Then
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("无未告警严重日志")
        void getUnAlertedCriticalLogsEmpty() {
            // Given
            when(auditLogRepository.findByAuditLevelAndIsAlertedFalse(AuditLevel.CRITICAL))
                    .thenReturn(Collections.emptyList());

            // When
            Result<java.util.List<AuditLogVO>> result = auditLogService.getUnAlertedCriticalLogs();

            // Then
            assertEquals(0, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("标记已告警测试")
    class MarkAlertedTests {

        @Test
        @DisplayName("标记已告警成功")
        void markAlertedSuccess() {
            // Given
            when(auditLogRepository.findById("audit-001")).thenReturn(Optional.of(testAuditLog));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

            // When
            Result<Void> result = auditLogService.markAlerted("audit-001");

            // Then
            assertEquals(0, result.getCode());
            assertTrue(testAuditLog.getIsAlerted());
            assertNotNull(testAuditLog.getAlertTime());
            verify(auditLogRepository).save(any(AuditLog.class));
        }

        @Test
        @DisplayName("日志不存在时标记")
        void markAlertedNotFound() {
            // Given
            when(auditLogRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When
            Result<Void> result = auditLogService.markAlerted("nonexistent");

            // Then
            assertEquals(0, result.getCode());
            verify(auditLogRepository, never()).save(any(AuditLog.class));
        }
    }

    @Nested
    @DisplayName("删除历史日志测试")
    class DeleteHistoryLogsTests {

        @Test
        @DisplayName("删除历史日志成功")
        void deleteHistoryLogsSuccess() {
            // Given
            doNothing().when(auditLogRepository).deleteByAuditTimeBefore(any(LocalDateTime.class));

            // When
            Result<Void> result = auditLogService.deleteHistoryLogs(180);

            // Then
            assertEquals(0, result.getCode());
            verify(auditLogRepository).deleteByAuditTimeBefore(any(LocalDateTime.class));
        }

        @Test
        @DisplayName("删除30天历史日志")
        void deleteHistoryLogs30Days() {
            // Given
            doNothing().when(auditLogRepository).deleteByAuditTimeBefore(any(LocalDateTime.class));

            // When
            Result<Void> result = auditLogService.deleteHistoryLogs(30);

            // Then
            assertEquals(0, result.getCode());
            verify(auditLogRepository).deleteByAuditTimeBefore(any(LocalDateTime.class));
        }
    }
}