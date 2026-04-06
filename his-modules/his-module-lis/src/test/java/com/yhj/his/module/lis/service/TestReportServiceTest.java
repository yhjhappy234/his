package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestReportAuditDTO;
import com.yhj.his.module.lis.dto.TestReportGenerateDTO;
import com.yhj.his.module.lis.dto.TestReportPublishDTO;
import com.yhj.his.module.lis.entity.TestReport;
import com.yhj.his.module.lis.entity.TestRequest;
import com.yhj.his.module.lis.entity.TestResult;
import com.yhj.his.module.lis.enums.TestReportStatus;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.enums.ResultFlag;
import com.yhj.his.module.lis.enums.VisitType;
import com.yhj.his.module.lis.repository.TestReportRepository;
import com.yhj.his.module.lis.repository.TestRequestRepository;
import com.yhj.his.module.lis.repository.TestResultRepository;
import com.yhj.his.module.lis.service.impl.TestReportServiceImpl;
import com.yhj.his.module.lis.vo.TestReportVO;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * TestReportService单元测试
 * 测试检验报告生成和发布功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("检验报告服务测试")
class TestReportServiceTest {

    @Mock
    private TestReportRepository testReportRepository;

    @Mock
    private TestRequestRepository testRequestRepository;

    @Mock
    private TestResultRepository testResultRepository;

    @InjectMocks
    private TestReportServiceImpl testReportService;

    private TestReport testReport;
    private TestRequest testRequest;
    private TestResult testResult;
    private TestReportGenerateDTO generateDTO;
    private TestReportAuditDTO auditDTO;
    private TestReportPublishDTO publishDTO;

    @BeforeEach
    void setUp() {
        // 初始化检验申请
        testRequest = new TestRequest();
        testRequest.setId("request-001");
        testRequest.setRequestNo("LIS20260406120000");
        testRequest.setPatientId("patient-001");
        testRequest.setPatientName("张三");
        testRequest.setVisitType(VisitType.OUTPATIENT);
        testRequest.setStatus(TestRequestStatus.AUDITED);
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
        testResult.setAuditorId("auditor-001");
        testResult.setTesterId("tester-001");
        testResult.setTesterName("检验员李");
        testResult.setTestTime(LocalDateTime.now());
        testResult.setCreateTime(LocalDateTime.now());

        // 初始化检验报告
        testReport = new TestReport();
        testReport.setId("report-001");
        testReport.setReportNo("RPT20260406120000");
        testReport.setRequestId("request-001");
        testReport.setPatientId("patient-001");
        testReport.setPatientName("张三");
        testReport.setSampleId("sample-001");
        testReport.setReportType("常规");
        testReport.setReportCategory("生化");
        testReport.setReportTime(LocalDateTime.now());
        testReport.setTesterId("tester-001");
        testReport.setTesterName("检验员李");
        testReport.setStatus(TestReportStatus.PENDING_AUDIT);
        testReport.setCriticalReport(false);
        testReport.setPrintCount(0);
        testReport.setCreateTime(LocalDateTime.now());

        // 初始化生成DTO
        generateDTO = new TestReportGenerateDTO();
        generateDTO.setRequestId("request-001");
        generateDTO.setSampleId("sample-001");
        generateDTO.setTesterId("tester-001");
        generateDTO.setTesterName("检验员李");

        // 初始化审核DTO
        auditDTO = new TestReportAuditDTO();
        auditDTO.setReportId("report-001");
        auditDTO.setAuditorId("auditor-001");
        auditDTO.setAuditorName("审核员王");
        auditDTO.setApproved(true);

        // 初始化发布DTO
        publishDTO = new TestReportPublishDTO();
        publishDTO.setReportId("report-001");
        publishDTO.setPublisherId("publisher-001");
        publishDTO.setPublisherName("发布员赵");
    }

    @Nested
    @DisplayName("生成检验报告测试")
    class GenerateReportTests {

        @Test
        @DisplayName("成功生成检验报告")
        void generateSuccessfully() {
            // Given
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testReportRepository.findByRequestId("request-001")).thenReturn(Optional.empty());
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));
            when(testReportRepository.save(any(TestReport.class))).thenReturn(testReport);

            // When
            TestReportVO result = testReportService.generate(generateDTO);

            // Then
            assertNotNull(result);
            assertEquals("patient-001", result.getPatientId());
            assertEquals("张三", result.getPatientName());
            assertEquals("PENDING_AUDIT", result.getStatus());
            assertNotNull(result.getResults());
            assertEquals(1, result.getResults().size());

            verify(testRequestRepository, atLeast(1)).findById("request-001");
            verify(testReportRepository).findByRequestId("request-001");
            verify(testReportRepository).save(any(TestReport.class));
        }

        @Test
        @DisplayName("生成失败-申请不存在")
        void generateFailedWithRequestNotFound() {
            // Given
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testReportService.generate(generateDTO)
            );
            assertEquals("检验申请不存在: request-001", exception.getMessage());

            verify(testReportRepository, never()).save(any(TestReport.class));
        }

        @Test
        @DisplayName("生成失败-报告已存在")
        void generateFailedWithReportExists() {
            // Given
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testReportRepository.findByRequestId("request-001")).thenReturn(Optional.of(testReport));

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testReportService.generate(generateDTO)
            );
            assertEquals("该申请已生成报告", exception.getMessage());

            verify(testReportRepository, never()).save(any(TestReport.class));
        }

        @Test
        @DisplayName("生成失败-部分结果未审核")
        void generateFailedWithUnauditedResults() {
            // Given
            TestResult unauditedResult = new TestResult();
            unauditedResult.setId("result-002");
            unauditedResult.setAuditorId(null); // 未审核

            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testReportRepository.findByRequestId("request-001")).thenReturn(Optional.empty());
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(unauditedResult));

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testReportService.generate(generateDTO)
            );
            assertEquals("部分结果尚未审核，无法生成报告", exception.getMessage());
        }

        @Test
        @DisplayName("生成危急值报告")
        void generateCriticalReport() {
            // Given
            TestResult criticalResult = new TestResult();
            criticalResult.setId("result-001");
            criticalResult.setCriticalFlag(true);
            criticalResult.setAuditorId("auditor-001");

            TestReport criticalReport = new TestReport();
            criticalReport.setId("report-002");
            criticalReport.setCriticalReport(true);
            criticalReport.setStatus(TestReportStatus.PENDING_AUDIT);
            criticalReport.setCreateTime(LocalDateTime.now());

            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testReportRepository.findByRequestId("request-001")).thenReturn(Optional.empty());
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(criticalResult));
            when(testReportRepository.save(any(TestReport.class))).thenReturn(criticalReport);

            // When
            TestReportVO result = testReportService.generate(generateDTO);

            // Then
            assertNotNull(result);
            assertTrue(result.getCriticalReport());
        }

        @Test
        @DisplayName("生成报告-更新申请报告状态")
        void generateUpdatesRequestReportStatus() {
            // Given
            TestRequest requestToUpdate = new TestRequest();
            requestToUpdate.setId("request-001");
            requestToUpdate.setCreateTime(LocalDateTime.now());

            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(requestToUpdate));
            when(testReportRepository.findByRequestId("request-001")).thenReturn(Optional.empty());
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));
            when(testReportRepository.save(any(TestReport.class))).thenReturn(testReport);
            when(testRequestRepository.save(any(TestRequest.class))).thenAnswer(invocation -> {
                TestRequest saved = invocation.getArgument(0);
                assertEquals("PENDING_AUDIT", saved.getReportStatus());
                return saved;
            });

            // When
            testReportService.generate(generateDTO);

            // Then
            verify(testRequestRepository).save(any(TestRequest.class));
        }
    }

    @Nested
    @DisplayName("审核检验报告测试")
    class AuditReportTests {

        @Test
        @DisplayName("成功审核报告-通过")
        void auditApprovedSuccessfully() {
            // Given
            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));
            when(testReportRepository.save(any(TestReport.class))).thenReturn(testReport);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));

            // When
            TestReportVO result = testReportService.audit(auditDTO);

            // Then
            assertNotNull(result);
            assertEquals("AUDITED", result.getStatus());
            assertEquals("auditor-001", result.getAuditorId());
            assertNotNull(result.getAuditTime());

            verify(testReportRepository).findById("report-001");
            verify(testReportRepository).save(any(TestReport.class));
        }

        @Test
        @DisplayName("成功审核报告-退回")
        void auditRejectedSuccessfully() {
            // Given
            auditDTO.setApproved(false);
            auditDTO.setReturnReason("结果异常需复核");

            TestReport returnedReport = new TestReport();
            returnedReport.setId("report-001");
            returnedReport.setRequestId("request-001");
            returnedReport.setPatientId("patient-001");
            returnedReport.setPatientName("张三");
            returnedReport.setStatus(TestReportStatus.RETURNED);
            returnedReport.setReturnReason("结果异常需复核");
            returnedReport.setCreateTime(LocalDateTime.now());

            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));
            when(testReportRepository.save(any(TestReport.class))).thenReturn(returnedReport);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testResult));

            // When
            TestReportVO result = testReportService.audit(auditDTO);

            // Then
            assertNotNull(result);
            assertEquals("RETURNED", result.getStatus());
            assertEquals("结果异常需复核", result.getReturnReason());
        }

        @Test
        @DisplayName("审核失败-报告不存在")
        void auditFailedWithReportNotFound() {
            // Given
            when(testReportRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class,
                () -> {
                    auditDTO.setReportId("non-existent");
                    testReportService.audit(auditDTO);
                });
        }

        @Test
        @DisplayName("审核失败-报告状态不是待审核")
        void auditFailedWithWrongStatus() {
            // Given
            testReport.setStatus(TestReportStatus.PUBLISHED);
            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testReportService.audit(auditDTO)
            );
            assertEquals("只有待审核的报告才能审核", exception.getMessage());

            verify(testReportRepository, never()).save(any(TestReport.class));
        }

        @Test
        @DisplayName("审核报告-更新申请报告状态为已审核")
        void auditApprovedUpdatesRequestReportStatus() {
            // Given
            TestRequest requestToUpdate = new TestRequest();
            requestToUpdate.setId("request-001");
            requestToUpdate.setCreateTime(LocalDateTime.now());

            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));
            when(testReportRepository.save(any(TestReport.class))).thenReturn(testReport);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(requestToUpdate));
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));
            when(testRequestRepository.save(any(TestRequest.class))).thenAnswer(invocation -> {
                TestRequest saved = invocation.getArgument(0);
                assertEquals("AUDITED", saved.getReportStatus());
                return saved;
            });

            // When
            testReportService.audit(auditDTO);

            // Then
            verify(testRequestRepository).save(any(TestRequest.class));
        }

        @Test
        @DisplayName("审核报告-退回时更新申请报告状态")
        void auditRejectedUpdatesRequestReportStatus() {
            // Given
            auditDTO.setApproved(false);
            auditDTO.setReturnReason("需复核");

            TestRequest requestToUpdate = new TestRequest();
            requestToUpdate.setId("request-001");
            requestToUpdate.setCreateTime(LocalDateTime.now());

            TestReport returnedReport = new TestReport();
            returnedReport.setId("report-001");
            returnedReport.setRequestId("request-001");
            returnedReport.setPatientId("patient-001");
            returnedReport.setPatientName("张三");
            returnedReport.setStatus(TestReportStatus.RETURNED);
            returnedReport.setCreateTime(LocalDateTime.now());

            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));
            when(testReportRepository.save(any(TestReport.class))).thenReturn(returnedReport);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(requestToUpdate));
            when(testResultRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testResult));
            when(testRequestRepository.save(any(TestRequest.class))).thenAnswer(invocation -> {
                TestRequest saved = invocation.getArgument(0);
                assertEquals("RETURNED", saved.getReportStatus());
                return saved;
            });

            // When
            testReportService.audit(auditDTO);

            // Then
            verify(testRequestRepository).save(any(TestRequest.class));
        }
    }

    @Nested
    @DisplayName("发布检验报告测试")
    class PublishReportTests {

        @Test
        @DisplayName("成功发布检验报告")
        void publishSuccessfully() {
            // Given
            testReport.setStatus(TestReportStatus.AUDITED);
            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));
            when(testReportRepository.save(any(TestReport.class))).thenReturn(testReport);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));

            // When
            TestReportVO result = testReportService.publish(publishDTO);

            // Then
            assertNotNull(result);
            assertEquals("PUBLISHED", result.getStatus());
            assertEquals("publisher-001", result.getPublisherId());
            assertNotNull(result.getPublishTime());

            verify(testReportRepository).findById("report-001");
            verify(testReportRepository).save(any(TestReport.class));
        }

        @Test
        @DisplayName("发布失败-报告不存在")
        void publishFailedWithReportNotFound() {
            // Given
            when(testReportRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class,
                () -> {
                    publishDTO.setReportId("non-existent");
                    testReportService.publish(publishDTO);
                });
        }

        @Test
        @DisplayName("发布失败-报告状态不是已审核")
        void publishFailedWithWrongStatus() {
            // Given
            testReport.setStatus(TestReportStatus.PENDING_AUDIT);
            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testReportService.publish(publishDTO)
            );
            assertEquals("只有已审核的报告才能发布", exception.getMessage());

            verify(testReportRepository, never()).save(any(TestReport.class));
        }

        @Test
        @DisplayName("发布报告-更新申请状态为已发布")
        void publishUpdatesRequestStatus() {
            // Given
            testReport.setStatus(TestReportStatus.AUDITED);
            TestRequest requestToUpdate = new TestRequest();
            requestToUpdate.setId("request-001");
            requestToUpdate.setCreateTime(LocalDateTime.now());

            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));
            when(testReportRepository.save(any(TestReport.class))).thenReturn(testReport);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(requestToUpdate));
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));
            when(testRequestRepository.save(any(TestRequest.class))).thenAnswer(invocation -> {
                TestRequest saved = invocation.getArgument(0);
                assertEquals(TestRequestStatus.PUBLISHED, saved.getStatus());
                assertEquals("PUBLISHED", saved.getReportStatus());
                return saved;
            });

            // When
            testReportService.publish(publishDTO);

            // Then
            verify(testRequestRepository).save(any(TestRequest.class));
        }
    }

    @Nested
    @DisplayName("打印检验报告测试")
    class PrintReportTests {

        @Test
        @DisplayName("成功打印检验报告")
        void printSuccessfully() {
            // Given
            testReport.setStatus(TestReportStatus.PUBLISHED);
            testReport.setPrintCount(0);

            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));
            when(testReportRepository.save(any(TestReport.class))).thenAnswer(invocation -> {
                TestReport saved = invocation.getArgument(0);
                assertEquals(1, saved.getPrintCount());
                assertNotNull(saved.getLastPrintTime());
                return saved;
            });
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));

            // When
            TestReportVO result = testReportService.print("report-001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.getPrintCount());
            assertNotNull(result.getLastPrintTime());
        }

        @Test
        @DisplayName("打印失败-报告不存在")
        void printFailedWithReportNotFound() {
            // Given
            when(testReportRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> testReportService.print("non-existent"));
        }

        @Test
        @DisplayName("打印失败-报告状态不是已发布")
        void printFailedWithWrongStatus() {
            // Given
            testReport.setStatus(TestReportStatus.PENDING_AUDIT);
            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testReportService.print("report-001")
            );
            assertEquals("只有已发布的报告才能打印", exception.getMessage());
        }

        @Test
        @DisplayName("多次打印-累加打印次数")
        void printMultipleTimes() {
            // Given
            testReport.setStatus(TestReportStatus.PUBLISHED);
            testReport.setPrintCount(2);

            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));
            when(testReportRepository.save(any(TestReport.class))).thenAnswer(invocation -> {
                TestReport saved = invocation.getArgument(0);
                assertEquals(3, saved.getPrintCount());
                return saved;
            });
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));

            // When
            TestReportVO result = testReportService.print("report-001");

            // Then
            assertEquals(3, result.getPrintCount());
        }
    }

    @Nested
    @DisplayName("查询检验报告测试")
    class QueryReportTests {

        @Test
        @DisplayName("根据ID查询检验报告")
        void getByIdSuccessfully() {
            // Given
            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));

            // When
            TestReportVO result = testReportService.getById("report-001");

            // Then
            assertNotNull(result);
            assertEquals("report-001", result.getId());
            assertEquals("张三", result.getPatientName());
        }

        @Test
        @DisplayName("根据报告编号查询检验报告")
        void getByReportNoSuccessfully() {
            // Given
            when(testReportRepository.findByReportNo("RPT20260406120000")).thenReturn(Optional.of(testReport));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));

            // When
            TestReportVO result = testReportService.getByReportNo("RPT20260406120000");

            // Then
            assertNotNull(result);
            assertEquals("RPT20260406120000", result.getReportNo());
        }

        @Test
        @DisplayName("根据申请ID查询检验报告")
        void getByRequestIdSuccessfully() {
            // Given
            when(testReportRepository.findByRequestId("request-001")).thenReturn(Optional.of(testReport));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testResult));

            // When
            TestReportVO result = testReportService.getByRequestId("request-001");

            // Then
            assertNotNull(result);
            assertEquals("request-001", result.getRequestId());
        }

        @Test
        @DisplayName("分页查询检验报告")
        void listWithPageable() {
            // Given
            List<TestReport> reports = Arrays.asList(testReport);
            Page<TestReport> page = new PageImpl<>(reports);
            Pageable pageable = PageRequest.of(0, 10);

            when(testReportRepository.findAll(pageable)).thenReturn(page);
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testResult));

            // When
            PageResult<TestReportVO> result = testReportService.list(pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("根据患者ID查询检验报告")
        void listByPatientId() {
            // Given
            when(testReportRepository.findByPatientIdOrderByReportTimeDesc("patient-001"))
                .thenReturn(Arrays.asList(testReport));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testResult));

            // When
            List<TestReportVO> result = testReportService.listByPatientId("patient-001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("patient-001", result.get(0).getPatientId());
        }

        @Test
        @DisplayName("根据状态查询检验报告")
        void listByStatus() {
            // Given
            when(testReportRepository.findByStatus(TestReportStatus.PENDING_AUDIT))
                .thenReturn(Arrays.asList(testReport));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testResult));

            // When
            List<TestReportVO> result = testReportService.listByStatus(TestReportStatus.PENDING_AUDIT);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("PENDING_AUDIT", result.get(0).getStatus());
        }

        @Test
        @DisplayName("查询待审核报告")
        void listPendingAuditReports() {
            // Given
            when(testReportRepository.findPendingAuditReports()).thenReturn(Arrays.asList(testReport));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testResult));

            // When
            List<TestReportVO> result = testReportService.listPendingAuditReports();

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("查询危急值报告")
        void listCriticalReports() {
            // Given
            testReport.setCriticalReport(true);
            when(testReportRepository.findCriticalReports(anyList())).thenReturn(Arrays.asList(testReport));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testResult));

            // When
            List<TestReportVO> result = testReportService.listCriticalReports();

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("根据报告时间范围查询")
        void listByReportTime() {
            // Given
            LocalDateTime startTime = LocalDateTime.now().minusDays(1);
            LocalDateTime endTime = LocalDateTime.now();
            Pageable pageable = PageRequest.of(0, 10);
            Page<TestReport> page = new PageImpl<>(Arrays.asList(testReport));

            when(testReportRepository.findByReportTimeBetween(startTime, endTime, pageable)).thenReturn(page);
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));
            when(testResultRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testResult));

            // When
            PageResult<TestReportVO> result = testReportService.listByReportTime(startTime, endTime, pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }
    }

    @Nested
    @DisplayName("删除检验报告测试")
    class DeleteReportTests {

        @Test
        @DisplayName("成功删除检验报告")
        void deleteSuccessfully() {
            // Given
            when(testReportRepository.findById("report-001")).thenReturn(Optional.of(testReport));
            when(testReportRepository.save(any(TestReport.class))).thenReturn(testReport);

            // When
            testReportService.delete("report-001");

            // Then
            verify(testReportRepository).findById("report-001");
            verify(testReportRepository).save(any(TestReport.class));
        }

        @Test
        @DisplayName("删除失败-报告不存在")
        void deleteFailedWithNotFound() {
            // Given
            when(testReportRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> testReportService.delete("non-existent"));
        }
    }

    @Nested
    @DisplayName("统计报告数量测试")
    class CountTests {

        @Test
        @DisplayName("统计某状态的报告数量")
        void countByStatus() {
            // Given
            when(testReportRepository.countByStatus(TestReportStatus.PUBLISHED)).thenReturn(10L);

            // When
            long count = testReportService.countByStatus(TestReportStatus.PUBLISHED);

            // Then
            assertEquals(10L, count);
        }
    }
}