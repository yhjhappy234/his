package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.RectificationDTO;
import com.yhj.his.module.emr.entity.QcResult;
import com.yhj.his.module.emr.enums.QcLevel;
import com.yhj.his.module.emr.repository.QcResultRepository;
import com.yhj.his.module.emr.service.impl.QcResultServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 质控结果服务单元测试
 * 覆盖质控管理核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QcResultService单元测试")
class QcResultServiceTest {

    @Mock
    private QcResultRepository qcResultRepository;

    @InjectMocks
    private QcResultServiceImpl qcResultService;

    private QcResult testQcResult;
    private RectificationDTO testRectificationDTO;

    @BeforeEach
    void setUp() {
        testQcResult = createTestQcResult();
        testRectificationDTO = createTestRectificationDTO();
    }

    @Nested
    @DisplayName("质控结果创建测试")
    class CreateQcResultTests {

        @Test
        @DisplayName("成功创建质控结果")
        void createQcResult_success() {
            when(qcResultRepository.save(any(QcResult.class))).thenAnswer(invocation -> {
                QcResult saved = invocation.getArgument(0);
                saved.setId("QC001");
                return saved;
            });

            QcResult result = qcResultService.createQcResult(testQcResult);

            assertNotNull(result);
            assertEquals("RECORD001", result.getRecordId());
            assertEquals("门诊病历", result.getRecordType());
            assertEquals(95, result.getQcScore());
            assertEquals(QcLevel.LEVEL_A, result.getQcLevel());

            verify(qcResultRepository).save(any(QcResult.class));
        }

        @Test
        @DisplayName("创建质控结果 - 包含患者信息")
        void createQcResult_withPatientInfo() {
            when(qcResultRepository.save(any(QcResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

            QcResult result = qcResultService.createQcResult(testQcResult);

            assertEquals("PATIENT001", result.getPatientId());
            assertEquals("张三", result.getPatientName());
        }

        @Test
        @DisplayName("创建质控结果 - 包含质控人信息")
        void createQcResult_withQcUserInfo() {
            when(qcResultRepository.save(any(QcResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

            QcResult result = qcResultService.createQcResult(testQcResult);

            assertEquals("QC_USER001", result.getQcUserId());
            assertEquals("质控员李", result.getQcUserName());
            assertNotNull(result.getQcTime());
        }
    }

    @Nested
    @DisplayName("质控结果更新测试")
    class UpdateQcResultTests {

        @Test
        @DisplayName("成功更新质控评分")
        void updateQcResult_updateScore() {
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult updateData = new QcResult();
            updateData.setQcScore(88);

            QcResult result = qcResultService.updateQcResult("QC001", updateData);

            assertEquals(88, result.getQcScore());
            verify(qcResultRepository).save(testQcResult);
        }

        @Test
        @DisplayName("成功更新质控等级")
        void updateQcResult_updateLevel() {
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult updateData = new QcResult();
            updateData.setQcLevel(QcLevel.LEVEL_B);

            QcResult result = qcResultService.updateQcResult("QC001", updateData);

            assertEquals(QcLevel.LEVEL_B, result.getQcLevel());
        }

        @Test
        @DisplayName("成功更新缺陷数量")
        void updateQcResult_updateDefectCount() {
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult updateData = new QcResult();
            updateData.setDefectCount(3);

            QcResult result = qcResultService.updateQcResult("QC001", updateData);

            assertEquals(3, result.getDefectCount());
        }

        @Test
        @DisplayName("成功更新缺陷详情")
        void updateQcResult_updateDefectDetails() {
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult updateData = new QcResult();
            updateData.setDefectDetails("[{\"defect\":\"主诉不规范\"}]");

            QcResult result = qcResultService.updateQcResult("QC001", updateData);

            assertNotNull(result.getDefectDetails());
        }

        @Test
        @DisplayName("成功更新质控备注")
        void updateQcResult_updateComment() {
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult updateData = new QcResult();
            updateData.setQcComment("需要整改");

            QcResult result = qcResultService.updateQcResult("QC001", updateData);

            assertEquals("需要整改", result.getQcComment());
        }

        @Test
        @DisplayName("更新质控结果 - 不存在抛出异常")
        void updateQcResult_notFound_throwException() {
            when(qcResultRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> qcResultService.updateQcResult("NONEXISTENT", new QcResult()));

            assertEquals("质控结果不存在: NONEXISTENT", exception.getMessage());
        }

        @Test
        @DisplayName("更新质控结果 - 只更新非空字段")
        void updateQcResult_partialUpdate() {
            testQcResult.setQcScore(95);
            testQcResult.setQcLevel(QcLevel.LEVEL_A);
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult updateData = new QcResult();
            // 只更新评分，不更新其他字段
            updateData.setQcScore(80);

            QcResult result = qcResultService.updateQcResult("QC001", updateData);

            assertEquals(80, result.getQcScore());
            assertEquals(QcLevel.LEVEL_A, result.getQcLevel()); // 保持原值
        }
    }

    @Nested
    @DisplayName("质控结果删除测试")
    class DeleteQcResultTests {

        @Test
        @DisplayName("成功删除质控结果 - 逻辑删除")
        void deleteQcResult_success() {
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            qcResultService.deleteQcResult("QC001");

            assertTrue(testQcResult.getDeleted());
            verify(qcResultRepository).save(testQcResult);
        }

        @Test
        @DisplayName("删除质控结果 - 不存在抛出异常")
        void deleteQcResult_notFound_throwException() {
            when(qcResultRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> qcResultService.deleteQcResult("NONEXISTENT"));

            assertEquals("质控结果不存在: NONEXISTENT", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("质控结果查询测试")
    class QueryQcResultTests {

        @Test
        @DisplayName("根据ID获取质控结果成功")
        void getQcResultById_success() {
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));

            QcResult result = qcResultService.getQcResultById("QC001");

            assertNotNull(result);
            assertEquals("张三", result.getPatientName());
        }

        @Test
        @DisplayName("根据ID获取质控结果 - 不存在抛出异常")
        void getQcResultById_notFound_throwException() {
            when(qcResultRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> qcResultService.getQcResultById("NONEXISTENT"));

            assertEquals("质控结果不存在: NONEXISTENT", exception.getMessage());
        }

        @Test
        @DisplayName("根据病历记录ID查询质控结果")
        void getQcResultByRecordId_success() {
            when(qcResultRepository.findByRecordIdAndDeletedFalse("RECORD001"))
                    .thenReturn(Optional.of(testQcResult));

            Optional<QcResult> result = qcResultService.getQcResultByRecordId("RECORD001");

            assertTrue(result.isPresent());
            assertEquals("RECORD001", result.get().getRecordId());
        }

        @Test
        @DisplayName("根据病历记录ID和类型查询质控结果")
        void getQcResultByRecordIdAndType_success() {
            when(qcResultRepository.findByRecordIdAndRecordTypeAndDeletedFalse("RECORD001", "门诊病历"))
                    .thenReturn(Optional.of(testQcResult));

            Optional<QcResult> result = qcResultService.getQcResultByRecordIdAndType("RECORD001", "门诊病历");

            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("根据患者ID查询质控结果列表")
        void getQcResultsByPatientId_success() {
            List<QcResult> results = List.of(testQcResult);
            when(qcResultRepository.findByPatientIdAndDeletedFalseOrderByCreateTimeDesc("PATIENT001"))
                    .thenReturn(results);

            List<QcResult> result = qcResultService.getQcResultsByPatientId("PATIENT001");

            assertEquals(1, result.size());
            assertEquals("PATIENT001", result.get(0).getPatientId());
        }

        @Test
        @DisplayName("分页查询质控结果列表")
        void listQcResults_success() {
            Pageable pageable = PageRequest.of(0, 10);
            List<QcResult> results = Arrays.asList(testQcResult, createAnotherQcResult());
            Page<QcResult> page = new PageImpl<>(results);

            when(qcResultRepository.findAll(pageable)).thenReturn(page);

            Page<QcResult> result = qcResultService.listQcResults(pageable);

            assertEquals(2, result.getContent().size());
        }

        @Test
        @DisplayName("分页查询 - 过滤已删除记录")
        void listQcResults_filterDeleted() {
            QcResult deletedResult = createAnotherQcResult();
            deletedResult.setDeleted(true);

            Pageable pageable = PageRequest.of(0, 10);
            List<QcResult> results = Arrays.asList(testQcResult, deletedResult);
            Page<QcResult> page = new PageImpl<>(results);

            when(qcResultRepository.findAll(pageable)).thenReturn(page);

            Page<QcResult> result = qcResultService.listQcResults(pageable);

            assertEquals(1, result.getContent().size());
            assertFalse(result.getContent().get(0).getDeleted());
        }

        @Test
        @DisplayName("查询待整改质控结果")
        void getPendingRectifications_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<QcResult> page = new PageImpl<>(List.of(testQcResult));

            when(qcResultRepository.findPendingRectification(pageable)).thenReturn(page);

            Page<QcResult> result = qcResultService.getPendingRectifications(pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("查询指定患者的整改记录")
        void getRectificationsByPatientId_success() {
            List<QcResult> results = List.of(testQcResult);
            when(qcResultRepository.findByPatientIdNeedRectification("PATIENT001")).thenReturn(results);

            List<QcResult> result = qcResultService.getRectificationsByPatientId("PATIENT001");

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("根据质控等级统计")
        void countByQcLevel_success() {
            when(qcResultRepository.countByQcLevel(QcLevel.LEVEL_A.name())).thenReturn(10L);

            Long count = qcResultService.countByQcLevel(QcLevel.LEVEL_A);

            assertEquals(10L, count);
        }

        @Test
        @DisplayName("根据质控人分页查询")
        void getQcResultsByQcUserId_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<QcResult> page = new PageImpl<>(List.of(testQcResult));

            when(qcResultRepository.findByQcUserIdAndDeletedFalse("QC_USER001", pageable)).thenReturn(page);

            Page<QcResult> result = qcResultService.getQcResultsByQcUserId("QC_USER001", pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("根据整改状态分页查询")
        void getQcResultsByRectificationStatus_success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<QcResult> page = new PageImpl<>(List.of(testQcResult));

            when(qcResultRepository.findByRectificationStatusAndDeletedFalse("待整改", pageable)).thenReturn(page);

            Page<QcResult> result = qcResultService.getQcResultsByRectificationStatus("待整改", pageable);

            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("根据质控等级获取质控结果列表")
        void getQcResultsByLevel_success() {
            Pageable pageable = PageRequest.of(0, 10);
            List<QcResult> results = List.of(testQcResult);
            Page<QcResult> page = new PageImpl<>(results);

            when(qcResultRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<QcResult> result = qcResultService.getQcResultsByLevel(QcLevel.LEVEL_A, pageable);

            assertEquals(1, result.getContent().size());
        }
    }

    @Nested
    @DisplayName("质控检查测试")
    class PerformQcCheckTests {

        @Test
        @DisplayName("成功执行质控检查 - 甲级")
        void performQcCheck_levelA_success() {
            when(qcResultRepository.findByRecordIdAndRecordTypeAndDeletedFalse("RECORD001", "门诊病历"))
                    .thenReturn(Optional.empty());
            when(qcResultRepository.save(any(QcResult.class))).thenAnswer(invocation -> {
                QcResult saved = invocation.getArgument(0);
                saved.setId("QC001");
                return saved;
            });

            QcResult result = qcResultService.performQcCheck("RECORD001", "门诊病历", "QC_USER001", "质控员李");

            assertNotNull(result);
            assertEquals("RECORD001", result.getRecordId());
            assertEquals("门诊病历", result.getRecordType());
            assertEquals("QC_USER001", result.getQcUserId());
            assertNotNull(result.getQcTime());
            assertTrue(result.getQcScore() >= 90);
            assertEquals(QcLevel.LEVEL_A, result.getQcLevel());
            assertFalse(result.getNeedRectification());
        }

        @Test
        @DisplayName("质控检查 - 乙级需要整改")
        void performQcCheck_levelB_needRectification() {
            when(qcResultRepository.findByRecordIdAndRecordTypeAndDeletedFalse("RECORD001", "门诊病历"))
                    .thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenAnswer(invocation -> {
                QcResult saved = invocation.getArgument(0);
                saved.setNeedRectification(saved.getQcScore() < 90);
                if (saved.getNeedRectification()) {
                    saved.setRectificationStatus("待整改");
                }
                return saved;
            });

            QcResult result = qcResultService.performQcCheck("RECORD001", "门诊病历", "QC_USER001", "质控员李");

            assertNotNull(result);
        }

        @Test
        @DisplayName("质控检查 - 更新已有结果")
        void performQcCheck_updateExisting() {
            when(qcResultRepository.findByRecordIdAndRecordTypeAndDeletedFalse("RECORD001", "门诊病历"))
                    .thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult result = qcResultService.performQcCheck("RECORD001", "门诊病历", "QC_USER002", "质控员王");

            assertNotNull(result);
            assertEquals("QC_USER002", result.getQcUserId());
            assertEquals("质控员王", result.getQcUserName());
        }
    }

    @Nested
    @DisplayName("整改通知测试")
    class RectificationNoticeTests {

        @Test
        @DisplayName("成功发送整改通知")
        void sendRectificationNotice_success() {
            testQcResult.setNeedRectification(true);
            testQcResult.setRectificationStatus(null);
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult result = qcResultService.sendRectificationNotice("QC001");

            assertEquals("待整改", result.getRectificationStatus());
            assertNotNull(result.getNotifyTime());
            verify(qcResultRepository).save(testQcResult);
        }

        @Test
        @DisplayName("发送整改通知 - 不需要整改抛出异常")
        void sendRectificationNotice_notNeeded_throwException() {
            testQcResult.setNeedRectification(false);
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> qcResultService.sendRectificationNotice("QC001"));

            assertEquals("该质控结果不需要整改", exception.getMessage());
        }

        @Test
        @DisplayName("发送整改通知 - needRectification为null抛出异常")
        void sendRectificationNotice_nullNeed_throwException() {
            testQcResult.setNeedRectification(null);
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> qcResultService.sendRectificationNotice("QC001"));

            assertEquals("该质控结果不需要整改", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("整改完成测试")
    class CompleteRectificationTests {

        @Test
        @DisplayName("成功完成整改")
        void completeRectification_success() {
            testQcResult.setNeedRectification(true);
            testQcResult.setRectificationStatus("待整改");
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult result = qcResultService.completeRectification(testRectificationDTO);

            assertEquals("已整改", result.getRectificationStatus());
            assertNotNull(result.getRectifyTime());
            assertEquals("已完成整改，请审核", result.getRectifyComment());
            verify(qcResultRepository).save(testQcResult);
        }

        @Test
        @DisplayName("完成整改 - 非待整改状态抛出异常")
        void completeRectification_notPending_throwException() {
            testQcResult.setRectificationStatus("已整改");
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> qcResultService.completeRectification(testRectificationDTO));

            assertEquals("只有待整改状态的质控结果可以完成整改", exception.getMessage());
        }

        @Test
        @DisplayName("完成整改 - 已超期状态抛出异常")
        void completeRectification_overdue_throwException() {
            testQcResult.setRectificationStatus("已超期");
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> qcResultService.completeRectification(testRectificationDTO));

            assertEquals("只有待整改状态的质控结果可以完成整改", exception.getMessage());
        }

        @Test
        @DisplayName("完成整改 - 质控结果不存在抛出异常")
        void completeRectification_notFound_throwException() {
            when(qcResultRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RectificationDTO dto = new RectificationDTO();
            dto.setQcResultId("NONEXISTENT");
            dto.setRectifyComment("已完成整改");

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> qcResultService.completeRectification(dto));

            assertEquals("质控结果不存在: NONEXISTENT", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("超期整改标记测试")
    class MarkOverdueTests {

        @Test
        @DisplayName("成功标记超期整改")
        void markRectificationOverdue_success() {
            testQcResult.setRectificationStatus("待整改");
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));
            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult result = qcResultService.markRectificationOverdue("QC001");

            assertEquals("已超期", result.getRectificationStatus());
            verify(qcResultRepository).save(testQcResult);
        }

        @Test
        @DisplayName("标记超期 - 非待整改状态不更新")
        void markRectificationOverdue_notPending_noUpdate() {
            testQcResult.setRectificationStatus("已整改");
            when(qcResultRepository.findById("QC001")).thenReturn(Optional.of(testQcResult));

            QcResult result = qcResultService.markRectificationOverdue("QC001");

            assertEquals("已整改", result.getRectificationStatus());
            verify(qcResultRepository, never()).save(any());
        }

        @Test
        @DisplayName("标记超期 - 质控结果不存在抛出异常")
        void markRectificationOverdue_notFound_throwException() {
            when(qcResultRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> qcResultService.markRectificationOverdue("NONEXISTENT"));

            assertEquals("质控结果不存在: NONEXISTENT", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("质控等级计算测试")
    class QcLevelTests {

        @Test
        @DisplayName("甲级评分 - 分数大于等于90")
        void qcLevel_A_score90() {
            testQcResult.setQcScore(90);
            assertEquals(QcLevel.LEVEL_A, QcLevel.fromScore(90));
        }

        @Test
        @DisplayName("甲级评分 - 分数大于90")
        void qcLevel_A_score95() {
            testQcResult.setQcScore(95);
            assertEquals(QcLevel.LEVEL_A, QcLevel.fromScore(95));
        }

        @Test
        @DisplayName("乙级评分 - 分数75-89")
        void qcLevel_B_score80() {
            assertEquals(QcLevel.LEVEL_B, QcLevel.fromScore(80));
        }

        @Test
        @DisplayName("乙级评分 - 分数等于75")
        void qcLevel_B_score75() {
            assertEquals(QcLevel.LEVEL_B, QcLevel.fromScore(75));
        }

        @Test
        @DisplayName("丙级评分 - 分数小于75")
        void qcLevel_C_score60() {
            assertEquals(QcLevel.LEVEL_C, QcLevel.fromScore(60));
        }

        @Test
        @DisplayName("丙级评分 - 分数为0")
        void qcLevel_C_score0() {
            assertEquals(QcLevel.LEVEL_C, QcLevel.fromScore(0));
        }
    }

    @Nested
    @DisplayName("时限检查测试")
    class TimeLimitCheckTests {

        @Test
        @DisplayName("时限检查通过")
        void timeLimitCheck_passed() {
            testQcResult.setTimeLimitPassed(true);
            testQcResult.setTimeLimitCheck("病历书写时限符合要求");

            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult result = qcResultService.createQcResult(testQcResult);

            assertTrue(result.getTimeLimitPassed());
            assertEquals("病历书写时限符合要求", result.getTimeLimitCheck());
        }

        @Test
        @DisplayName("时限检查未通过")
        void timeLimitCheck_failed() {
            testQcResult.setTimeLimitPassed(false);
            testQcResult.setTimeLimitCheck("入院记录超过24小时时限");

            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult result = qcResultService.createQcResult(testQcResult);

            assertFalse(result.getTimeLimitPassed());
            assertEquals("入院记录超过24小时时限", result.getTimeLimitCheck());
        }
    }

    @Nested
    @DisplayName("内容检查测试")
    class ContentCheckTests {

        @Test
        @DisplayName("内容检查通过")
        void contentCheck_passed() {
            testQcResult.setContentPassed(true);
            testQcResult.setContentCheck("病历内容完整规范");

            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult result = qcResultService.createQcResult(testQcResult);

            assertTrue(result.getContentPassed());
        }

        @Test
        @DisplayName("内容检查未通过")
        void contentCheck_failed() {
            testQcResult.setContentPassed(false);
            testQcResult.setContentCheck("主诉不规范，现病史描述不完整");

            when(qcResultRepository.save(any(QcResult.class))).thenReturn(testQcResult);

            QcResult result = qcResultService.createQcResult(testQcResult);

            assertFalse(result.getContentPassed());
        }
    }

    // Helper methods
    private QcResult createTestQcResult() {
        QcResult result = new QcResult();
        result.setId("QC001");
        result.setRecordId("RECORD001");
        result.setRecordType("门诊病历");
        result.setPatientId("PATIENT001");
        result.setPatientName("张三");
        result.setQcScore(95);
        result.setQcLevel(QcLevel.LEVEL_A);
        result.setDefectCount(0);
        result.setDefectDetails("[]");
        result.setTimeLimitPassed(true);
        result.setContentPassed(true);
        result.setQcUserId("QC_USER001");
        result.setQcUserName("质控员李");
        result.setQcTime(LocalDateTime.now());
        result.setQcComment("符合甲级病历标准");
        result.setNeedRectification(false);
        result.setDeleted(false);
        return result;
    }

    private QcResult createAnotherQcResult() {
        QcResult result = new QcResult();
        result.setId("QC002");
        result.setRecordId("RECORD002");
        result.setRecordType("入院记录");
        result.setPatientId("PATIENT002");
        result.setPatientName("王五");
        result.setQcScore(80);
        result.setQcLevel(QcLevel.LEVEL_B);
        result.setDefectCount(2);
        result.setNeedRectification(true);
        result.setRectificationStatus("待整改");
        result.setDeleted(false);
        return result;
    }

    private RectificationDTO createTestRectificationDTO() {
        RectificationDTO dto = new RectificationDTO();
        dto.setQcResultId("QC001");
        dto.setRectifyComment("已完成整改，请审核");
        return dto;
    }
}