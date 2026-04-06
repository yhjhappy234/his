package com.yhj.his.module.pacs.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.pacs.dto.ReportQueryDTO;
import com.yhj.his.module.pacs.dto.ReportReviewDTO;
import com.yhj.his.module.pacs.dto.ReportWriteDTO;
import com.yhj.his.module.pacs.entity.ExamRecord;
import com.yhj.his.module.pacs.entity.ExamReport;
import com.yhj.his.module.pacs.entity.ExamRequest;
import com.yhj.his.module.pacs.repository.ExamRecordRepository;
import com.yhj.his.module.pacs.repository.ExamReportRepository;
import com.yhj.his.module.pacs.repository.ExamRequestRepository;
import com.yhj.his.module.pacs.service.impl.ReportServiceImpl;
import com.yhj.his.module.pacs.vo.ExamReportVO;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ExamReportService Unit Tests
 * Tests for report writing, review, and publishing workflow
 */
@ExtendWith(MockitoExtension.class)
class ExamReportServiceTest {

    @Mock
    private ExamReportRepository examReportRepository;

    @Mock
    private ExamRecordRepository examRecordRepository;

    @Mock
    private ExamRequestRepository examRequestRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private ReportWriteDTO reportWriteDTO;
    private ReportReviewDTO reportReviewDTO;
    private ExamRecord examRecord;
    private ExamReport examReport;
    private ExamRequest examRequest;

    @BeforeEach
    void setUp() {
        // Setup ExamRecord
        examRecord = new ExamRecord();
        examRecord.setId("exam-001");
        examRecord.setExamNo("CT20260406001");
        examRecord.setRequestId("request-001");
        examRecord.setPatientId("P001");
        examRecord.setPatientName("测试患者");
        examRecord.setExamType("CT");
        examRecord.setExamPart("胸部");
        examRecord.setExamStatus("检查完成");
        examRecord.setReportStatus("待报告");

        // Setup ExamReport
        examReport = new ExamReport();
        examReport.setId("report-001");
        examReport.setReportNo("RP20260406100000");
        examReport.setExamId("exam-001");
        examReport.setRequestId("request-001");
        examReport.setPatientId("P001");
        examReport.setPatientName("测试患者");
        examReport.setReportType("初步报告");
        examReport.setExamDescription("双肺纹理清晰，未见明显异常密度影");
        examReport.setDiagnosisResult("肺部未见明显异常");
        examReport.setDiagnosisCode("Z00.00");
        examReport.setDiagnosisName("正常");
        examReport.setReportStatus("草稿");
        examReport.setWriterId("DOC001");
        examReport.setWriterName("报告医生");
        examReport.setWriteTime(LocalDateTime.now());
        examReport.setPrintCount(0);

        // Setup ExamRequest
        examRequest = new ExamRequest();
        examRequest.setId("request-001");
        examRequest.setRequestNo("CT20260406001");
        examRequest.setStatus("已登记");

        // Setup ReportWriteDTO
        reportWriteDTO = new ReportWriteDTO();
        reportWriteDTO.setExamId("exam-001");
        reportWriteDTO.setRequestId("request-001");
        reportWriteDTO.setPatientId("P001");
        reportWriteDTO.setPatientName("测试患者");
        reportWriteDTO.setReportType("初步报告");
        reportWriteDTO.setReportTemplateId("template-001");
        reportWriteDTO.setExamDescription("双肺纹理清晰，未见明显异常密度影");
        reportWriteDTO.setDiagnosisResult("肺部未见明显异常");
        reportWriteDTO.setDiagnosisCode("Z00.00");
        reportWriteDTO.setDiagnosisName("正常");
        reportWriteDTO.setKeyImages("image1,image2");
        reportWriteDTO.setWriterId("DOC001");
        reportWriteDTO.setWriterName("报告医生");

        // Setup ReportReviewDTO
        reportReviewDTO = new ReportReviewDTO();
        reportReviewDTO.setReportId("report-001");
        reportReviewDTO.setApproved(true);
        reportReviewDTO.setReviewerId("DOC002");
        reportReviewDTO.setReviewerName("审核医生");
        reportReviewDTO.setReviewComment("报告准确，同意发布");
    }

    @Nested
    @DisplayName("Write Report Tests")
    class WriteReportTests {

        @Test
        @DisplayName("Should create new report successfully")
        void writeReport_CreateNew_Success() {
            // Arrange
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> {
                ExamReport saved = invocation.getArgument(0);
                saved.setId("new-report-id");
                return saved;
            });

            // Act
            ExamReportVO result = reportService.writeReport(reportWriteDTO);

            // Assert
            assertNotNull(result);
            assertEquals("new-report-id", result.getId());
            assertEquals("草稿", result.getReportStatus());
            assertEquals("测试患者", result.getPatientName());
            assertEquals("肺部未见明显异常", result.getDiagnosisResult());

            verify(examRecordRepository).findById("exam-001");
            verify(examReportRepository).save(any(ExamReport.class));
        }

        @Test
        @DisplayName("Should update existing report successfully")
        void writeReport_UpdateExisting_Success() {
            // Arrange
            reportWriteDTO.setReportId("report-001");
            reportWriteDTO.setDiagnosisResult("更新后的诊断结论");

            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> {
                ExamReport saved = invocation.getArgument(0);
                return saved;
            });

            // Act
            ExamReportVO result = reportService.writeReport(reportWriteDTO);

            // Assert
            assertNotNull(result);
            assertEquals("更新后的诊断结论", result.getDiagnosisResult());
            // Note: updateTime is set by JPA @UpdateTimestamp, not testable in unit tests

            verify(examReportRepository).findById("report-001");
            verify(examReportRepository).save(any(ExamReport.class));
        }

        @Test
        @DisplayName("Should throw exception when exam record not found")
        void writeReport_ExamNotFound() {
            // Arrange
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.writeReport(reportWriteDTO));

            assertEquals("检查记录不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when report not found for update")
        void writeReport_ReportNotFound() {
            // Arrange
            reportWriteDTO.setReportId("non-existent-report");
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examReportRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.writeReport(reportWriteDTO));

            assertEquals("报告不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should use exam record patient info when DTO patient info is null")
        void writeReport_UseExamRecordPatientInfo() {
            // Arrange
            reportWriteDTO.setPatientId(null);
            reportWriteDTO.setPatientName(null);

            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> {
                ExamReport saved = invocation.getArgument(0);
                saved.setId("new-report-id");
                return saved;
            });

            // Act
            ExamReportVO result = reportService.writeReport(reportWriteDTO);

            // Assert
            assertNotNull(result);
            assertEquals("P001", result.getPatientId());
            assertEquals("测试患者", result.getPatientName());
        }

        @Test
        @DisplayName("Should record modification history when updating report")
        void writeReport_RecordModificationHistory() {
            // Arrange
            reportWriteDTO.setReportId("report-001");
            examReport.setModifyHistory(null);

            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> {
                ExamReport saved = invocation.getArgument(0);
                assertNotNull(saved.getModifyHistory());
                return saved;
            });

            // Act
            reportService.writeReport(reportWriteDTO);

            // Assert
            verify(examReportRepository).save(any(ExamReport.class));
        }
    }

    @Nested
    @DisplayName("Submit for Review Tests")
    class SubmitForReviewTests {

        @Test
        @DisplayName("Should submit report for review successfully when draft")
        void submitForReview_Draft_Success() {
            // Arrange
            examReport.setReportStatus("草稿");
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> {
                ExamReport saved = invocation.getArgument(0);
                saved.setReportStatus("待审核");
                return saved;
            });
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examRecordRepository.save(any(ExamRecord.class))).thenReturn(examRecord);

            // Act
            ExamReportVO result = reportService.submitForReview("report-001");

            // Assert
            assertNotNull(result);
            assertEquals("待审核", result.getReportStatus());

            verify(examReportRepository).save(any(ExamReport.class));
            verify(examRecordRepository).save(any(ExamRecord.class));
        }

        @Test
        @DisplayName("Should submit report for review when rejected")
        void submitForReview_Rejected_Success() {
            // Arrange
            examReport.setReportStatus("审核驳回");
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> {
                ExamReport saved = invocation.getArgument(0);
                saved.setReportStatus("待审核");
                return saved;
            });
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examRecordRepository.save(any(ExamRecord.class))).thenReturn(examRecord);

            // Act
            ExamReportVO result = reportService.submitForReview("report-001");

            // Assert
            assertNotNull(result);
            assertEquals("待审核", result.getReportStatus());
        }

        @Test
        @DisplayName("Should throw exception when report not found for submission")
        void submitForReview_ReportNotFound() {
            // Arrange
            when(examReportRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.submitForReview("non-existent-id"));

            assertEquals("报告不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when report status invalid for submission")
        void submitForReview_InvalidStatus() {
            // Arrange
            examReport.setReportStatus("已审核");
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.submitForReview("report-001"));

            assertEquals("报告状态不正确，无法提交审核", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Review Report Tests")
    class ReviewReportTests {

        @Test
        @DisplayName("Should approve report successfully")
        void reviewReport_Approve_Success() {
            // Arrange
            examReport.setReportStatus("待审核");
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> {
                ExamReport saved = invocation.getArgument(0);
                return saved;
            });
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examRecordRepository.save(any(ExamRecord.class))).thenReturn(examRecord);

            // Act
            ExamReportVO result = reportService.reviewReport(reportReviewDTO);

            // Assert
            assertNotNull(result);
            assertEquals("已审核", result.getReportStatus());
            assertEquals("DOC002", result.getReviewerId());
            assertEquals("审核医生", result.getReviewerName());
            assertNotNull(result.getReviewTime());
            assertEquals("报告准确，同意发布", result.getReviewComment());

            verify(examReportRepository).save(any(ExamReport.class));
        }

        @Test
        @DisplayName("Should reject report successfully")
        void reviewReport_Reject_Success() {
            // Arrange
            examReport.setReportStatus("待审核");
            reportReviewDTO.setApproved(false);
            reportReviewDTO.setReviewComment("诊断描述不准确，需要修改");

            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> {
                ExamReport saved = invocation.getArgument(0);
                return saved;
            });
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examRecordRepository.save(any(ExamRecord.class))).thenReturn(examRecord);

            // Act
            ExamReportVO result = reportService.reviewReport(reportReviewDTO);

            // Assert
            assertNotNull(result);
            assertEquals("审核驳回", result.getReportStatus());
            assertEquals("诊断描述不准确，需要修改", result.getReviewComment());
        }

        @Test
        @DisplayName("Should throw exception when report not found for review")
        void reviewReport_ReportNotFound() {
            // Arrange
            when(examReportRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.reviewReport(reportReviewDTO));

            assertEquals("报告不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when report status invalid for review")
        void reviewReport_InvalidStatus() {
            // Arrange
            examReport.setReportStatus("草稿");
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.reviewReport(reportReviewDTO));

            assertEquals("报告状态不正确，无法审核", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Publish Report Tests")
    class PublishReportTests {

        @Test
        @DisplayName("Should publish report successfully")
        void publishReport_Success() {
            // Arrange
            examReport.setReportStatus("已审核");
            examReport.setExamId("exam-001");
            examReport.setRequestId("request-001");

            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> {
                ExamReport saved = invocation.getArgument(0);
                saved.setReportStatus("已发布");
                return saved;
            });
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examRecordRepository.save(any(ExamRecord.class))).thenReturn(examRecord);
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));
            when(examRequestRepository.save(any(ExamRequest.class))).thenReturn(examRequest);

            // Act
            ExamReportVO result = reportService.publishReport("report-001", "DOC003", "发布医生");

            // Assert
            assertNotNull(result);
            assertEquals("已发布", result.getReportStatus());
            assertEquals("DOC003", result.getPublisherId());
            assertEquals("发布医生", result.getPublisherName());
            assertNotNull(result.getPublishTime());

            verify(examReportRepository).save(any(ExamReport.class));
            verify(examRecordRepository).save(any(ExamRecord.class));
            verify(examRequestRepository).save(any(ExamRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when report not found for publish")
        void publishReport_ReportNotFound() {
            // Arrange
            when(examReportRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.publishReport("non-existent-id", "DOC003", "发布医生"));

            assertEquals("报告不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when report not reviewed for publish")
        void publishReport_NotReviewed() {
            // Arrange
            examReport.setReportStatus("待审核");
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.publishReport("report-001", "DOC003", "发布医生"));

            assertEquals("报告状态不正确，无法发布", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Print Report Tests")
    class PrintReportTests {

        @Test
        @DisplayName("Should print report and increment print count")
        void printReport_Success() {
            // Arrange
            examReport.setReportStatus("已发布");
            examReport.setPrintCount(0);

            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> {
                ExamReport saved = invocation.getArgument(0);
                return saved;
            });

            // Act
            ExamReportVO result = reportService.printReport("report-001");

            // Assert
            assertNotNull(result);
            assertEquals("已打印", result.getReportStatus());
            assertEquals(1, result.getPrintCount());
        }

        @Test
        @DisplayName("Should throw exception when report not found for print")
        void printReport_ReportNotFound() {
            // Arrange
            when(examReportRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.printReport("non-existent-id"));

            assertEquals("报告不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when report not published for print")
        void printReport_NotPublished() {
            // Arrange
            examReport.setReportStatus("草稿");
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.printReport("report-001"));

            assertEquals("报告未发布，无法打印", exception.getMessage());
        }

        @Test
        @DisplayName("Should increment print count on multiple prints")
        void printReport_MultiplePrints() {
            // Arrange
            examReport.setReportStatus("已发布");
            examReport.setPrintCount(2);

            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));
            when(examReportRepository.save(any(ExamReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            ExamReportVO result = reportService.printReport("report-001");

            // Assert
            assertEquals(3, result.getPrintCount());
        }
    }

    @Nested
    @DisplayName("Get Report Tests")
    class GetReportTests {

        @Test
        @DisplayName("Should get report by ID successfully")
        void getReportById_Success() {
            // Arrange
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));

            // Act
            ExamReportVO result = reportService.getReportById("report-001");

            // Assert
            assertNotNull(result);
            assertEquals("report-001", result.getId());
            assertEquals("RP20260406100000", result.getReportNo());
        }

        @Test
        @DisplayName("Should throw exception when report not found by ID")
        void getReportById_NotFound() {
            // Arrange
            when(examReportRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.getReportById("non-existent-id"));

            assertEquals("报告不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should get report by exam ID")
        void getReportByExamId_Success() {
            // Arrange
            when(examReportRepository.findByExamId(anyString())).thenReturn(Optional.of(examReport));

            // Act
            ExamReportVO result = reportService.getReportByExamId("exam-001");

            // Assert
            assertNotNull(result);
            assertEquals("exam-001", result.getExamId());
        }

        @Test
        @DisplayName("Should return null when report not found by exam ID")
        void getReportByExamId_NotFound() {
            // Arrange
            when(examReportRepository.findByExamId(anyString())).thenReturn(Optional.empty());

            // Act
            ExamReportVO result = reportService.getReportByExamId("non-existent-exam");

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("Should get report by request ID")
        void getReportByRequestId_Success() {
            // Arrange
            when(examReportRepository.findByRequestId(anyString())).thenReturn(Optional.of(examReport));

            // Act
            ExamReportVO result = reportService.getReportByRequestId("request-001");

            // Assert
            assertNotNull(result);
            assertEquals("request-001", result.getRequestId());
        }

        @Test
        @DisplayName("Should get report by report number")
        void getReportByNo_Success() {
            // Arrange
            when(examReportRepository.findByReportNo(anyString())).thenReturn(Optional.of(examReport));

            // Act
            ExamReportVO result = reportService.getReportByNo("RP20260406100000");

            // Assert
            assertNotNull(result);
            assertEquals("RP20260406100000", result.getReportNo());
        }

        @Test
        @DisplayName("Should throw exception when report not found by number")
        void getReportByNo_NotFound() {
            // Arrange
            when(examReportRepository.findByReportNo(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.getReportByNo("non-existent-no"));

            assertEquals("报告不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should get reports by patient ID")
        void getReportsByPatientId_Success() {
            // Arrange
            when(examReportRepository.findByPatientId(anyString())).thenReturn(List.of(examReport));

            // Act
            List<ExamReportVO> results = reportService.getReportsByPatientId("P001");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("P001", results.get(0).getPatientId());
        }
    }

    @Nested
    @DisplayName("Query Reports Tests")
    class QueryReportsTests {

        @Test
        @DisplayName("Should query reports with pagination")
        void queryReports_Success() {
            // Arrange
            ReportQueryDTO queryDTO = new ReportQueryDTO();
            queryDTO.setPatientId("P001");
            queryDTO.setReportStatus("已发布");
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(10);

            Page<ExamReport> page = new PageImpl<>(List.of(examReport));
            when(examReportRepository.findByConditions(
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // Act
            PageResult<ExamReportVO> result = reportService.queryReports(queryDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("Should get pending review reports")
        void getPendingReviewReports_Success() {
            // Arrange
            examReport.setReportStatus("待审核");
            when(examReportRepository.findPendingReview()).thenReturn(List.of(examReport));

            // Act
            List<ExamReportVO> results = reportService.getPendingReviewReports();

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("待审核", results.get(0).getReportStatus());
        }

        @Test
        @DisplayName("Should get my reports")
        void getMyReports_Success() {
            // Arrange
            when(examReportRepository.findByWriterId(anyString())).thenReturn(List.of(examReport));

            // Act
            List<ExamReportVO> results = reportService.getMyReports("DOC001");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("DOC001", results.get(0).getWriterId());
        }

        @Test
        @DisplayName("Should get my reviewed reports")
        void getMyReviewedReports_Success() {
            // Arrange
            examReport.setReviewerId("DOC002");
            when(examReportRepository.findByReviewerId(anyString())).thenReturn(List.of(examReport));

            // Act
            List<ExamReportVO> results = reportService.getMyReviewedReports("DOC002");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("DOC002", results.get(0).getReviewerId());
        }
    }

    @Nested
    @DisplayName("Modify History Tests")
    class ModifyHistoryTests {

        @Test
        @DisplayName("Should get modify history successfully")
        void getModifyHistory_Success() {
            // Arrange
            examReport.setModifyHistory("[{\"time\":\"20260406100000\",\"user\":\"医生A\",\"action\":\"创建\"}]");
            when(examReportRepository.findById(anyString())).thenReturn(Optional.of(examReport));

            // Act
            String history = reportService.getModifyHistory("report-001");

            // Assert
            assertNotNull(history);
            assertTrue(history.contains("创建"));
        }

        @Test
        @DisplayName("Should throw exception when report not found for history")
        void getModifyHistory_ReportNotFound() {
            // Arrange
            when(examReportRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> reportService.getModifyHistory("non-existent-id"));

            assertEquals("报告不存在", exception.getMessage());
        }
    }
}