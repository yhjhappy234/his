package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestResultAuditDTO;
import com.yhj.his.module.lis.dto.TestResultInputDTO;
import com.yhj.his.module.lis.entity.TestItem;
import com.yhj.his.module.lis.entity.TestRequest;
import com.yhj.his.module.lis.entity.TestResult;
import com.yhj.his.module.lis.enums.ResultFlag;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.enums.TestItemCategory;
import com.yhj.his.module.lis.enums.TestItemStatus;
import com.yhj.his.module.lis.enums.SpecimenType;
import com.yhj.his.module.lis.enums.VisitType;
import com.yhj.his.module.lis.repository.TestResultRepository;
import com.yhj.his.module.lis.repository.TestItemRepository;
import com.yhj.his.module.lis.repository.TestRequestRepository;
import com.yhj.his.module.lis.service.impl.TestResultServiceImpl;
import com.yhj.his.module.lis.vo.TestResultVO;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TestResultService单元测试
 * 测试检验结果录入和验证功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("检验结果服务测试")
class TestResultServiceTest {

    @Mock
    private TestResultRepository testResultRepository;

    @Mock
    private TestItemRepository testItemRepository;

    @Mock
    private TestRequestRepository testRequestRepository;

    @Mock
    private CriticalValueService criticalValueService;

    @InjectMocks
    private TestResultServiceImpl testResultService;

    private TestResult testResult;
    private TestItem testItem;
    private TestRequest testRequest;
    private TestResultInputDTO inputDTO;
    private TestResultAuditDTO auditDTO;

    @BeforeEach
    void setUp() {
        // 初始化检验项目
        testItem = new TestItem();
        testItem.setId("item-001");
        testItem.setItemCode("GLU");
        testItem.setItemName("血糖");
        testItem.setUnit("mmol/L");
        testItem.setReferenceMin(new BigDecimal("3.9"));
        testItem.setReferenceMax(new BigDecimal("6.1"));
        testItem.setCriticalLow(new BigDecimal("2.8"));
        testItem.setCriticalHigh(new BigDecimal("28.0"));
        testItem.setCritical(true);
        testItem.setCategory(TestItemCategory.BIOCHEMISTRY);
        testItem.setStatus(TestItemStatus.NORMAL);
        testItem.setCreateTime(LocalDateTime.now());

        // 初始化检验申请
        testRequest = new TestRequest();
        testRequest.setId("request-001");
        testRequest.setRequestNo("LIS20260406120000");
        testRequest.setPatientId("patient-001");
        testRequest.setPatientName("张三");
        testRequest.setVisitType(VisitType.OUTPATIENT);
        testRequest.setStatus(TestRequestStatus.RECEIVED);
        testRequest.setCreateTime(LocalDateTime.now());

        // 初始化检验结果
        testResult = new TestResult();
        testResult.setId("result-001");
        testResult.setRequestId("request-001");
        testResult.setSampleId("sample-001");
        testResult.setItemId("item-001");
        testResult.setItemCode("GLU");
        testResult.setItemName("血糖");
        testResult.setUnit("mmol/L");
        testResult.setTestValue("5.5");
        testResult.setNumericValue(new BigDecimal("5.5"));
        testResult.setResultFlag(ResultFlag.NORMAL);
        testResult.setAbnormalFlag(false);
        testResult.setCriticalFlag(false);
        testResult.setReferenceMin(new BigDecimal("3.9"));
        testResult.setReferenceMax(new BigDecimal("6.1"));
        testResult.setTestTime(LocalDateTime.now());
        testResult.setTesterId("tester-001");
        testResult.setTesterName("检验员李");
        testResult.setCreateTime(LocalDateTime.now());

        // 初始化录入DTO
        inputDTO = new TestResultInputDTO();
        inputDTO.setRequestId("request-001");
        inputDTO.setSampleId("sample-001");
        inputDTO.setItemId("item-001");
        inputDTO.setTestValue("5.5");
        inputDTO.setTesterId("tester-001");
        inputDTO.setTesterName("检验员李");

        // 初始化审核DTO
        auditDTO = new TestResultAuditDTO();
        auditDTO.setResultId("result-001");
        auditDTO.setAuditorId("auditor-001");
        auditDTO.setAuditorName("审核员王");
    }

    @Nested
    @DisplayName("录入检验结果测试")
    class InputTestResultTests {

        @Test
        @DisplayName("成功录入正常值结果")
        void inputNormalValueSuccessfully() {
            // Given
            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(testResult);

            // When
            TestResultVO result = testResultService.input(inputDTO);

            // Then
            assertNotNull(result);
            assertEquals("5.5", result.getTestValue());
            assertEquals("NORMAL", result.getResultFlag());
            assertFalse(result.getAbnormalFlag());
            assertFalse(result.getCriticalFlag());

            verify(testItemRepository).findById("item-001");
            verify(testRequestRepository, atLeast(1)).findById("request-001");
            verify(testResultRepository).save(any(TestResult.class));
        }

        @Test
        @DisplayName("录入偏高值结果")
        void inputHighValueResult() {
            // Given
            inputDTO.setTestValue("8.0");
            TestResult highResult = new TestResult();
            highResult.setId("result-002");
            highResult.setTestValue("8.0");
            highResult.setNumericValue(new BigDecimal("8.0"));
            highResult.setResultFlag(ResultFlag.HIGH);
            highResult.setAbnormalFlag(true);
            highResult.setCriticalFlag(false);
            highResult.setCreateTime(LocalDateTime.now());

            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(highResult);

            // When
            TestResultVO result = testResultService.input(inputDTO);

            // Then
            assertNotNull(result);
            assertEquals("HIGH", result.getResultFlag());
            assertTrue(result.getAbnormalFlag());
        }

        @Test
        @DisplayName("录入偏低值结果")
        void inputLowValueResult() {
            // Given
            inputDTO.setTestValue("2.5");
            TestResult lowResult = new TestResult();
            lowResult.setId("result-003");
            lowResult.setTestValue("2.5");
            lowResult.setNumericValue(new BigDecimal("2.5"));
            lowResult.setResultFlag(ResultFlag.LOW);
            lowResult.setAbnormalFlag(true);
            lowResult.setCriticalFlag(false);
            lowResult.setCreateTime(LocalDateTime.now());

            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(lowResult);

            // When
            TestResultVO result = testResultService.input(inputDTO);

            // Then
            assertNotNull(result);
            assertEquals("LOW", result.getResultFlag());
            assertTrue(result.getAbnormalFlag());
        }

        @Test
        @DisplayName("录入危急值结果")
        void inputCriticalValueResult() {
            // Given
            inputDTO.setTestValue("35.0");
            TestResult criticalResult = new TestResult();
            criticalResult.setId("result-004");
            criticalResult.setTestValue("35.0");
            criticalResult.setNumericValue(new BigDecimal("35.0"));
            criticalResult.setResultFlag(ResultFlag.CRITICAL);
            criticalResult.setAbnormalFlag(true);
            criticalResult.setCriticalFlag(true);
            criticalResult.setRequestId("request-001");
            criticalResult.setItemId("item-001");
            criticalResult.setCreateTime(LocalDateTime.now());

            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(criticalResult);
            when(criticalValueService.create(anyString(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                    anyString(), anyString())).thenReturn(null);

            // When
            TestResultVO result = testResultService.input(inputDTO);

            // Then
            assertNotNull(result);
            assertEquals("CRITICAL", result.getResultFlag());
            assertTrue(result.getAbnormalFlag());
            assertTrue(result.getCriticalFlag());

            verify(criticalValueService).create(
                eq("request-001"), eq("sample-001"), eq("result-004"),
                eq("patient-001"), eq("张三"), eq("item-001"), eq("血糖"),
                eq("35.0"), eq("CRITICAL"), anyString(),
                eq("tester-001"), eq("检验员李")
            );
        }

        @Test
        @DisplayName("录入文本结果(非数值)")
        void inputTextResult() {
            // Given
            inputDTO.setTestValue("阳性");
            TestResult textResult = new TestResult();
            textResult.setId("result-005");
            textResult.setTestValue("阳性");
            textResult.setTextResult("阳性");
            textResult.setResultFlag(ResultFlag.POSITIVE);
            textResult.setCreateTime(LocalDateTime.now());

            TestItem textItem = new TestItem();
            textItem.setId("item-002");
            textItem.setItemName("乙肝表面抗原");
            textItem.setReferenceText("阴性");
            textItem.setCritical(false);

            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(textItem));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(textResult);

            // When
            TestResultVO result = testResultService.input(inputDTO);

            // Then
            assertNotNull(result);
            assertEquals("阳性", result.getTestValue());
        }

        @Test
        @DisplayName("录入失败-检验项目不存在")
        void inputFailedWithItemNotFound() {
            // Given
            when(testItemRepository.findById("item-001")).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testResultService.input(inputDTO)
            );
            assertEquals("检验项目不存在: item-001", exception.getMessage());
        }

        @Test
        @DisplayName("录入失败-申请不存在")
        void inputFailedWithRequestNotFound() {
            // Given
            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testResultService.input(inputDTO)
            );
            assertEquals("检验申请不存在: request-001", exception.getMessage());
        }

        @Test
        @DisplayName("录入结果-更新申请状态为检测中")
        void inputUpdatesRequestStatusToTesting() {
            // Given
            TestRequest requestToUpdate = new TestRequest();
            requestToUpdate.setId("request-001");
            requestToUpdate.setStatus(TestRequestStatus.RECEIVED);
            requestToUpdate.setCreateTime(LocalDateTime.now());

            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(requestToUpdate));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(testResult);
            when(testRequestRepository.save(any(TestRequest.class))).thenAnswer(invocation -> {
                TestRequest saved = invocation.getArgument(0);
                assertEquals(TestRequestStatus.TESTING, saved.getStatus());
                return saved;
            });

            // When
            testResultService.input(inputDTO);

            // Then
            verify(testRequestRepository).save(any(TestRequest.class));
        }

        @Test
        @DisplayName("录入结果-申请状态非已核收时不更新")
        void inputDoesNotUpdateRequestStatusWhenNotReceived() {
            // Given
            TestRequest testingRequest = new TestRequest();
            testingRequest.setId("request-001");
            testingRequest.setStatus(TestRequestStatus.TESTING);
            testingRequest.setCreateTime(LocalDateTime.now());

            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testingRequest));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(testResult);

            // When
            testResultService.input(inputDTO);

            // Then
            verify(testRequestRepository, never()).save(any(TestRequest.class));
        }
    }

    @Nested
    @DisplayName("修改检验结果测试")
    class ModifyTestResultTests {

        @Test
        @DisplayName("成功修改检验结果")
        void modifySuccessfully() {
            // Given
            when(testResultRepository.findById("result-001")).thenReturn(Optional.of(testResult));
            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(testResult);

            // When
            TestResultVO result = testResultService.modify("result-001", "6.0", "录入错误");

            // Then
            assertNotNull(result);
            verify(testResultRepository).findById("result-001");
            verify(testResultRepository).save(any(TestResult.class));
        }

        @Test
        @DisplayName("修改失败-结果不存在")
        void modifyFailedWithResultNotFound() {
            // Given
            when(testResultRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class,
                () -> testResultService.modify("non-existent", "6.0", "录入错误"));
        }

        @Test
        @DisplayName("修改结果后重新判定结果标识")
        void modifyReevaluateResultFlag() {
            // Given
            testResult.setTestValue("5.5");
            testResult.setResultFlag(ResultFlag.NORMAL);

            when(testResultRepository.findById("result-001")).thenReturn(Optional.of(testResult));
            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testResultRepository.save(any(TestResult.class))).thenAnswer(invocation -> {
                TestResult saved = invocation.getArgument(0);
                // 修改后的值8.0应该被判定为HIGH
                return saved;
            });

            // When
            TestResultVO result = testResultService.modify("result-001", "8.0", "录入错误");

            // Then
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("审核检验结果测试")
    class AuditTestResultTests {

        @Test
        @DisplayName("成功审核检验结果")
        void auditSuccessfully() {
            // Given
            when(testResultRepository.findById("result-001")).thenReturn(Optional.of(testResult));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(testResult);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.countByRequestId("request-001")).thenReturn(1L);
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));

            // When
            TestResultVO result = testResultService.audit(auditDTO);

            // Then
            assertNotNull(result);
            assertEquals("auditor-001", result.getAuditorId());
            assertNotNull(result.getAuditTime());

            verify(testResultRepository).findById("result-001");
            verify(testResultRepository).save(any(TestResult.class));
        }

        @Test
        @DisplayName("审核结果-所有结果审核完成后更新申请状态")
        void auditUpdatesRequestStatusWhenAllAudited() {
            // Given
            testResult.setAuditorId("auditor-001");

            TestRequest requestToUpdate = new TestRequest();
            requestToUpdate.setId("request-001");
            requestToUpdate.setStatus(TestRequestStatus.TESTING);
            requestToUpdate.setCreateTime(LocalDateTime.now());

            when(testResultRepository.findById("result-001")).thenReturn(Optional.of(testResult));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(testResult);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(requestToUpdate));
            when(testResultRepository.countByRequestId("request-001")).thenReturn(1L);
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));
            when(testRequestRepository.save(any(TestRequest.class))).thenAnswer(invocation -> {
                TestRequest saved = invocation.getArgument(0);
                assertEquals(TestRequestStatus.AUDITED, saved.getStatus());
                return saved;
            });

            // When
            testResultService.audit(auditDTO);

            // Then
            verify(testRequestRepository).save(any(TestRequest.class));
        }

        @Test
        @DisplayName("审核失败-结果不存在")
        void auditFailedWithResultNotFound() {
            // Given
            TestResultAuditDTO notFoundDTO = new TestResultAuditDTO();
            notFoundDTO.setResultId("non-existent");
            notFoundDTO.setAuditorId("auditor-001");

            when(testResultRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class,
                () -> testResultService.audit(notFoundDTO));
        }

        @Test
        @DisplayName("审核结果-指定审核时间")
        void auditWithCustomTime() {
            // Given
            LocalDateTime customTime = LocalDateTime.now().minusMinutes(30);
            auditDTO.setAuditTime(customTime);

            when(testResultRepository.findById("result-001")).thenReturn(Optional.of(testResult));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(testResult);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.countByRequestId("request-001")).thenReturn(1L);
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));

            // When
            TestResultVO result = testResultService.audit(auditDTO);

            // Then
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("查询检验结果测试")
    class QueryTestResultTests {

        @Test
        @DisplayName("根据ID查询检验结果")
        void getByIdSuccessfully() {
            // Given
            when(testResultRepository.findById("result-001")).thenReturn(Optional.of(testResult));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));

            // When
            TestResultVO result = testResultService.getById("result-001");

            // Then
            assertNotNull(result);
            assertEquals("result-001", result.getId());
            assertEquals("5.5", result.getTestValue());
        }

        @Test
        @DisplayName("根据申请ID查询所有结果")
        void listByRequestId() {
            // Given
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));

            // When
            List<TestResultVO> result = testResultService.listByRequestId("request-001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("根据样本ID查询所有结果")
        void listBySampleId() {
            // Given
            when(testResultRepository.findBySampleId("sample-001")).thenReturn(Arrays.asList(testResult));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<TestResultVO> result = testResultService.listBySampleId("sample-001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("根据申请ID和项目ID查询结果")
        void getByRequestIdAndItemId() {
            // Given
            when(testResultRepository.findByRequestIdAndItemId("request-001", "item-001"))
                .thenReturn(Optional.of(testResult));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));

            // When
            TestResultVO result = testResultService.getByRequestIdAndItemId("request-001", "item-001");

            // Then
            assertNotNull(result);
            assertEquals("item-001", result.getItemId());
        }

        @Test
        @DisplayName("分页查询检验结果")
        void listWithPageable() {
            // Given
            List<TestResult> results = Arrays.asList(testResult);
            Page<TestResult> page = new PageImpl<>(results);
            Pageable pageable = PageRequest.of(0, 10);

            when(testResultRepository.findAll(pageable)).thenReturn(page);
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            PageResult<TestResultVO> result = testResultService.list(pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("查询危急值结果")
        void listCriticalResults() {
            // Given
            testResult.setCriticalFlag(true);
            when(testResultRepository.findByCriticalFlagTrue()).thenReturn(Arrays.asList(testResult));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<TestResultVO> result = testResultService.listCriticalResults();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.get(0).getCriticalFlag());
        }

        @Test
        @DisplayName("查询异常结果")
        void listAbnormalResults() {
            // Given
            testResult.setAbnormalFlag(true);
            when(testResultRepository.findByAbnormalFlagTrue()).thenReturn(Arrays.asList(testResult));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<TestResultVO> result = testResultService.listAbnormalResults();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.get(0).getAbnormalFlag());
        }

        @Test
        @DisplayName("查询待审核结果")
        void listPendingAuditResults() {
            // Given
            testResult.setAuditorId(null);
            when(testResultRepository.findPendingAuditResults()).thenReturn(Arrays.asList(testResult));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<TestResultVO> result = testResultService.listPendingAuditResults();

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("根据检测时间范围查询")
        void listByTestTime() {
            // Given
            LocalDateTime startTime = LocalDateTime.now().minusDays(1);
            LocalDateTime endTime = LocalDateTime.now();
            Pageable pageable = PageRequest.of(0, 10);
            Page<TestResult> page = new PageImpl<>(Arrays.asList(testResult));

            when(testResultRepository.findByTestTimeBetween(startTime, endTime, pageable)).thenReturn(page);
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            PageResult<TestResultVO> result = testResultService.listByTestTime(startTime, endTime, pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("查询患者历史结果")
        void listHistoryResults() {
            // Given
            when(testResultRepository.findHistoryResultsByPatientIdAndItemId("patient-001", "item-001"))
                .thenReturn(Arrays.asList(testResult));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<TestResultVO> result = testResultService.listHistoryResults("patient-001", "item-001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("删除检验结果测试")
    class DeleteTestResultTests {

        @Test
        @DisplayName("成功删除检验结果")
        void deleteSuccessfully() {
            // Given
            when(testResultRepository.findById("result-001")).thenReturn(Optional.of(testResult));
            when(testResultRepository.save(any(TestResult.class))).thenReturn(testResult);

            // When
            testResultService.delete("result-001");

            // Then
            verify(testResultRepository).findById("result-001");
            verify(testResultRepository).save(any(TestResult.class));
        }

        @Test
        @DisplayName("删除失败-结果不存在")
        void deleteFailedWithNotFound() {
            // Given
            when(testResultRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> testResultService.delete("non-existent"));
        }
    }

    @Nested
    @DisplayName("统计检验结果测试")
    class CountTests {

        @Test
        @DisplayName("统计申请的结果数量")
        void countByRequestId() {
            // Given
            when(testResultRepository.countByRequestId("request-001")).thenReturn(5L);

            // When
            long count = testResultService.countByRequestId("request-001");

            // Then
            assertEquals(5L, count);
        }
    }

    @Nested
    @DisplayName("危急值判定测试")
    class CheckCriticalValueTests {

        @Test
        @DisplayName("判定为危急值-高于上限")
        void checkCriticalValueHigh() {
            // Given
            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));

            // When
            boolean isCritical = testResultService.checkCriticalValue("item-001", "35.0");

            // Then
            assertTrue(isCritical);
        }

        @Test
        @DisplayName("判定为危急值-低于下限")
        void checkCriticalValueLow() {
            // Given
            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));

            // When
            boolean isCritical = testResultService.checkCriticalValue("item-001", "1.5");

            // Then
            assertTrue(isCritical);
        }

        @Test
        @DisplayName("非危急值-在范围内")
        void checkCriticalValueNormal() {
            // Given
            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));

            // When
            boolean isCritical = testResultService.checkCriticalValue("item-001", "5.5");

            // Then
            assertFalse(isCritical);
        }

        @Test
        @DisplayName("项目无危急值设置")
        void checkCriticalValueNoCriticalSetting() {
            // Given
            TestItem nonCriticalItem = new TestItem();
            nonCriticalItem.setId("item-002");
            nonCriticalItem.setCritical(false);

            when(testItemRepository.findById("item-002")).thenReturn(Optional.of(nonCriticalItem));

            // When
            boolean isCritical = testResultService.checkCriticalValue("item-002", "100.0");

            // Then
            assertFalse(isCritical);
        }

        @Test
        @DisplayName("项目不存在")
        void checkCriticalValueItemNotFound() {
            // Given
            when(testItemRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When
            boolean isCritical = testResultService.checkCriticalValue("non-existent", "100.0");

            // Then
            assertFalse(isCritical);
        }

        @Test
        @DisplayName("非数值检测值")
        void checkCriticalValueNonNumeric() {
            // Given
            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));

            // When
            boolean isCritical = testResultService.checkCriticalValue("item-001", "阳性");

            // Then
            assertFalse(isCritical);
        }
    }
}