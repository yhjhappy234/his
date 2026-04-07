package com.yhj.his.module.pacs.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.pacs.dto.CheckInDTO;
import com.yhj.his.module.pacs.dto.ExamQueryDTO;
import com.yhj.his.module.pacs.dto.ExamRequestDTO;
import com.yhj.his.module.pacs.dto.ScheduleDTO;
import com.yhj.his.module.pacs.entity.ExamItem;
import com.yhj.his.module.pacs.entity.ExamRecord;
import com.yhj.his.module.pacs.entity.ExamRequest;
import com.yhj.his.module.pacs.entity.RoomSchedule;
import com.yhj.his.module.pacs.repository.ExamItemRepository;
import com.yhj.his.module.pacs.repository.ExamRecordRepository;
import com.yhj.his.module.pacs.repository.ExamRequestRepository;
import com.yhj.his.module.pacs.repository.RoomScheduleRepository;
import com.yhj.his.module.pacs.service.impl.ExamRequestServiceImpl;
import com.yhj.his.module.pacs.vo.ExamRecordVO;
import com.yhj.his.module.pacs.vo.ExamRequestVO;
import com.yhj.his.module.pacs.vo.RoomScheduleVO;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ExamRequestService Unit Tests
 * Tests for exam request workflow including creation, scheduling, check-in, and cancellation
 */
@ExtendWith(MockitoExtension.class)
class ExamRequestServiceTest {

    @Mock
    private ExamRequestRepository examRequestRepository;

    @Mock
    private ExamRecordRepository examRecordRepository;

    @Mock
    private RoomScheduleRepository roomScheduleRepository;

    @Mock
    private ExamItemRepository examItemRepository;

    @InjectMocks
    private ExamRequestServiceImpl examRequestService;

    private ExamRequestDTO examRequestDTO;
    private ExamItem examItem;
    private ExamRequest examRequest;
    private ScheduleDTO scheduleDTO;
    private CheckInDTO checkInDTO;

    @BeforeEach
    void setUp() {
        // Setup ExamItem
        examItem = new ExamItem();
        examItem.setId("item-001");
        examItem.setItemCode("CT001");
        examItem.setItemName("胸部CT");
        examItem.setExamType("CT");
        examItem.setExamPart("胸部");
        examItem.setExamMethod("平扫");
        examItem.setPrice(new BigDecimal("500.00"));
        examItem.setStatus("启用");

        // Setup ExamRequestDTO
        examRequestDTO = new ExamRequestDTO();
        examRequestDTO.setPatientId("P001");
        examRequestDTO.setPatientName("测试患者");
        examRequestDTO.setGender("M");
        examRequestDTO.setAge(35);
        examRequestDTO.setIdCardNo("123456789012345678");
        examRequestDTO.setVisitType("门诊");
        examRequestDTO.setVisitId("V001");
        examRequestDTO.setDeptId("D001");
        examRequestDTO.setDeptName("内科");
        examRequestDTO.setDoctorId("DOC001");
        examRequestDTO.setDoctorName("测试医生");
        examRequestDTO.setClinicalDiagnosis("肺炎");
        examRequestDTO.setClinicalInfo("咳嗽、发热三天");
        examRequestDTO.setExamPurpose("排查肺部病变");
        examRequestDTO.setItemId("item-001");
        examRequestDTO.setIsEmergency(false);

        // Setup ExamRequest
        examRequest = new ExamRequest();
        examRequest.setId("request-001");
        examRequest.setRequestNo("CT20260406001");
        examRequest.setPatientId("P001");
        examRequest.setPatientName("测试患者");
        examRequest.setGender("M");
        examRequest.setAge(35);
        examRequest.setVisitType("门诊");
        examRequest.setDeptId("D001");
        examRequest.setItemId("item-001");
        examRequest.setItemCode("CT001");
        examRequest.setItemName("胸部CT");
        examRequest.setExamType("CT");
        examRequest.setExamPart("胸部");
        examRequest.setRequestTime(LocalDateTime.now());
        examRequest.setStatus("待预约");
        examRequest.setTotalAmount(new BigDecimal("500.00"));

        // Setup ScheduleDTO
        scheduleDTO = new ScheduleDTO();
        scheduleDTO.setRequestId("request-001");
        scheduleDTO.setScheduleTime(LocalDateTime.of(2026, 4, 7, 10, 0));
        scheduleDTO.setRoomNo("R001");

        // Setup CheckInDTO
        checkInDTO = new CheckInDTO();
        checkInDTO.setRequestId("request-001");
        checkInDTO.setEquipmentId("EQ001");
        checkInDTO.setEquipmentName("CT设备1");
        checkInDTO.setRoomNo("R001");
        checkInDTO.setTechnicianId("TECH001");
        checkInDTO.setTechnicianName("技师张");
    }

    @Nested
    @DisplayName("Create Request Tests")
    class CreateRequestTests {

        @Test
        @DisplayName("Should create exam request successfully when item exists")
        void createRequest_Success() {
            // Arrange
            when(examItemRepository.findById(anyString())).thenReturn(Optional.of(examItem));
            when(examRequestRepository.save(any(ExamRequest.class))).thenReturn(examRequest);

            // Act
            ExamRequestVO result = examRequestService.createRequest(examRequestDTO);

            // Assert
            assertNotNull(result);
            assertEquals("request-001", result.getId());
            assertEquals("CT20260406001", result.getRequestNo());
            assertEquals("P001", result.getPatientId());
            assertEquals("测试患者", result.getPatientName());
            assertEquals("待预约", result.getStatus());
            assertEquals("胸部CT", result.getItemName());
            assertEquals("CT", result.getExamType());

            verify(examItemRepository).findById("item-001");
            verify(examRequestRepository).save(any(ExamRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when exam item not found")
        void createRequest_ItemNotFound() {
            // Arrange
            when(examItemRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.createRequest(examRequestDTO));

            assertEquals("检查项目不存在", exception.getMessage());
            verify(examItemRepository).findById("item-001");
            verify(examRequestRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should use item defaults when DTO fields are null")
        void createRequest_UseItemDefaults() {
            // Arrange
            examRequestDTO.setExamPart(null);
            examRequestDTO.setExamMethod(null);
            examRequestDTO.setTotalAmount(null);

            when(examItemRepository.findById(anyString())).thenReturn(Optional.of(examItem));
            when(examRequestRepository.save(any(ExamRequest.class))).thenAnswer(invocation -> {
                ExamRequest savedRequest = invocation.getArgument(0);
                savedRequest.setId("new-request-id");
                return savedRequest;
            });

            // Act
            ExamRequestVO result = examRequestService.createRequest(examRequestDTO);

            // Assert
            assertNotNull(result);
            verify(examRequestRepository).save(any(ExamRequest.class));
        }

        @Test
        @DisplayName("Should create emergency request with emergency level")
        void createRequest_EmergencyRequest() {
            // Arrange
            examRequestDTO.setIsEmergency(true);
            examRequestDTO.setEmergencyLevel("一级");

            when(examItemRepository.findById(anyString())).thenReturn(Optional.of(examItem));
            when(examRequestRepository.save(any(ExamRequest.class))).thenAnswer(invocation -> {
                ExamRequest savedRequest = invocation.getArgument(0);
                savedRequest.setId("emergency-request-id");
                return savedRequest;
            });

            // Act
            ExamRequestVO result = examRequestService.createRequest(examRequestDTO);

            // Assert
            assertNotNull(result);
            verify(examRequestRepository).save(any(ExamRequest.class));
        }
    }

    @Nested
    @DisplayName("Schedule Tests")
    class ScheduleTests {

        @Test
        @DisplayName("Should schedule exam request successfully")
        void schedule_Success() {
            // Arrange
            examRequest.setStatus("待预约");
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));
            when(roomScheduleRepository.findByRoomNoAndScheduleDate(anyString(), any(LocalDate.class)))
                    .thenReturn(Collections.emptyList());
            when(examRequestRepository.save(any(ExamRequest.class))).thenAnswer(invocation -> {
                ExamRequest saved = invocation.getArgument(0);
                saved.setStatus("已预约");
                return saved;
            });

            // Act
            ExamRequestVO result = examRequestService.schedule(scheduleDTO);

            // Assert
            assertNotNull(result);
            assertEquals("已预约", result.getStatus());
            assertEquals(LocalDateTime.of(2026, 4, 7, 10, 0), result.getScheduleTime());

            verify(examRequestRepository).findById("request-001");
            verify(examRequestRepository).save(any(ExamRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when request not found for scheduling")
        void schedule_RequestNotFound() {
            // Arrange
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.schedule(scheduleDTO));

            assertEquals("申请不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when request status is not pending")
        void schedule_InvalidStatus() {
            // Arrange
            examRequest.setStatus("已预约");
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.schedule(scheduleDTO));

            assertEquals("申请状态不正确，无法预约", exception.getMessage());
        }

        @Test
        @DisplayName("Should increment schedule count when room schedule available")
        void schedule_WithAvailableRoomSchedule() {
            // Arrange
            RoomSchedule roomSchedule = new RoomSchedule();
            roomSchedule.setId("schedule-001");
            roomSchedule.setRoomNo("R001");
            roomSchedule.setStartTime(LocalTime.of(8, 0));
            roomSchedule.setEndTime(LocalTime.of(12, 0));
            roomSchedule.setTotalQuota(10);
            roomSchedule.setScheduledCount(5);
            roomSchedule.setAvailableQuota(5);

            examRequest.setStatus("待预约");
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));
            when(roomScheduleRepository.findByRoomNoAndScheduleDate(anyString(), any(LocalDate.class)))
                    .thenReturn(List.of(roomSchedule));
            when(roomScheduleRepository.incrementScheduledCount(anyString())).thenReturn(1);
            when(examRequestRepository.save(any(ExamRequest.class))).thenReturn(examRequest);

            // Act
            ExamRequestVO result = examRequestService.schedule(scheduleDTO);

            // Assert
            assertNotNull(result);
            verify(roomScheduleRepository).incrementScheduledCount("schedule-001");
        }
    }

    @Nested
    @DisplayName("Check-In Tests")
    class CheckInTests {

        @Test
        @DisplayName("Should check-in successfully when request is scheduled")
        void checkIn_Success() {
            // Arrange
            examRequest.setStatus("已预约");
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));
            when(examRecordRepository.save(any(ExamRecord.class))).thenAnswer(invocation -> {
                ExamRecord record = invocation.getArgument(0);
                record.setId("record-001");
                return record;
            });
            when(examRequestRepository.save(any(ExamRequest.class))).thenReturn(examRequest);
            when(examRequestRepository.countByRequestDate(any(LocalDateTime.class))).thenReturn(0L);

            // Act
            ExamRecordVO result = examRequestService.checkIn(checkInDTO);

            // Assert
            assertNotNull(result);
            assertEquals("record-001", result.getId());
            assertEquals("P001", result.getPatientId());
            assertEquals("检查中", result.getExamStatus());
            assertEquals("待报告", result.getReportStatus());

            verify(examRecordRepository).save(any(ExamRecord.class));
            verify(examRequestRepository, times(1)).save(any(ExamRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when request not found for check-in")
        void checkIn_RequestNotFound() {
            // Arrange
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.checkIn(checkInDTO));

            assertEquals("申请不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when request status is not scheduled")
        void checkIn_InvalidStatus() {
            // Arrange
            examRequest.setStatus("待预约");
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.checkIn(checkInDTO));

            assertEquals("申请状态不正确，无法登记", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Cancel Request Tests")
    class CancelRequestTests {

        @Test
        @DisplayName("Should cancel request successfully when pending")
        void cancelRequest_Success() {
            // Arrange
            examRequest.setStatus("待预约");
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));
            when(examRequestRepository.save(any(ExamRequest.class))).thenAnswer(invocation -> {
                ExamRequest saved = invocation.getArgument(0);
                saved.setStatus("已取消");
                return saved;
            });

            // Act
            ExamRequestVO result = examRequestService.cancelRequest("request-001", "患者取消");

            // Assert
            assertNotNull(result);
            assertEquals("已取消", result.getStatus());
            assertEquals("患者取消", result.getRemark());

            verify(examRequestRepository).save(any(ExamRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when request not found for cancellation")
        void cancelRequest_RequestNotFound() {
            // Arrange
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.cancelRequest("request-001", "取消原因"));

            assertEquals("申请不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when request already cancelled")
        void cancelRequest_AlreadyCancelled() {
            // Arrange
            examRequest.setStatus("已取消");
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.cancelRequest("request-001", "取消原因"));

            assertEquals("申请状态不正确，无法取消", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when request already reported")
        void cancelRequest_AlreadyReported() {
            // Arrange
            examRequest.setStatus("已报告");
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.cancelRequest("request-001", "取消原因"));

            assertEquals("申请状态不正确，无法取消", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Request Tests")
    class GetRequestTests {

        @Test
        @DisplayName("Should get request by ID successfully")
        void getRequestById_Success() {
            // Arrange
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));

            // Act
            ExamRequestVO result = examRequestService.getRequestById("request-001");

            // Assert
            assertNotNull(result);
            assertEquals("request-001", result.getId());
            assertEquals("CT20260406001", result.getRequestNo());
        }

        @Test
        @DisplayName("Should throw exception when request not found by ID")
        void getRequestById_NotFound() {
            // Arrange
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.getRequestById("non-existent-id"));

            assertEquals("申请不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should get request by request number successfully")
        void getRequestByNo_Success() {
            // Arrange
            when(examRequestRepository.findByRequestNo(anyString())).thenReturn(Optional.of(examRequest));

            // Act
            ExamRequestVO result = examRequestService.getRequestByNo("CT20260406001");

            // Assert
            assertNotNull(result);
            assertEquals("CT20260406001", result.getRequestNo());
        }

        @Test
        @DisplayName("Should throw exception when request not found by number")
        void getRequestByNo_NotFound() {
            // Arrange
            when(examRequestRepository.findByRequestNo(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.getRequestByNo("non-existent-no"));

            assertEquals("申请不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should get requests by patient ID")
        void getRequestsByPatientId_Success() {
            // Arrange
            when(examRequestRepository.findByPatientId(anyString())).thenReturn(List.of(examRequest));

            // Act
            List<ExamRequestVO> results = examRequestService.getRequestsByPatientId("P001");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("P001", results.get(0).getPatientId());
        }

        @Test
        @DisplayName("Should return empty list when patient has no requests")
        void getRequestsByPatientId_EmptyList() {
            // Arrange
            when(examRequestRepository.findByPatientId(anyString())).thenReturn(Collections.emptyList());

            // Act
            List<ExamRequestVO> results = examRequestService.getRequestsByPatientId("P002");

            // Assert
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }
    }

    @Nested
    @DisplayName("Query Tests")
    class QueryTests {

        @Test
        @DisplayName("Should query requests with pagination")
        void queryRequests_Success() {
            // Arrange
            ExamQueryDTO queryDTO = new ExamQueryDTO();
            queryDTO.setPatientId("P001");
            queryDTO.setStatus("待预约");
            queryDTO.setPageNum(1);
            queryDTO.setPageSize(10);

            Page<ExamRequest> page = new PageImpl<>(List.of(examRequest));
            when(examRequestRepository.findByConditions(
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // Act
            PageResult<ExamRequestVO> result = examRequestService.queryRequests(queryDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("Should get pending requests")
        void getPendingRequests_Success() {
            // Arrange
            when(examRequestRepository.findByStatus("待预约")).thenReturn(List.of(examRequest));

            // Act
            List<ExamRequestVO> results = examRequestService.getPendingRequests();

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("待预约", results.get(0).getStatus());
        }

        @Test
        @DisplayName("Should get available schedules")
        void getAvailableSchedules_Success() {
            // Arrange
            RoomSchedule roomSchedule = new RoomSchedule();
            roomSchedule.setId("schedule-001");
            roomSchedule.setRoomNo("R001");
            roomSchedule.setScheduleDate(LocalDate.of(2026, 4, 7));
            roomSchedule.setAvailableQuota(10);

            when(roomScheduleRepository.findAvailableByDateAndExamType(any(LocalDate.class), anyString()))
                    .thenReturn(List.of(roomSchedule));

            // Act
            List<RoomScheduleVO> results = examRequestService.getAvailableSchedules("CT", "2026-04-07");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
        }
    }

    @Nested
    @DisplayName("Update Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update status successfully")
        void updateStatus_Success() {
            // Arrange
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.of(examRequest));
            when(examRequestRepository.save(any(ExamRequest.class))).thenAnswer(invocation -> {
                ExamRequest saved = invocation.getArgument(0);
                return saved;
            });

            // Act
            ExamRequestVO result = examRequestService.updateStatus("request-001", "已完成");

            // Assert
            assertNotNull(result);
            assertEquals("已完成", result.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when request not found for status update")
        void updateStatus_RequestNotFound() {
            // Arrange
            when(examRequestRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> examRequestService.updateStatus("non-existent-id", "已完成"));

            assertEquals("申请不存在", exception.getMessage());
        }
    }
}