package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.outpatient.dto.ScheduleCreateRequest;
import com.yhj.his.module.outpatient.entity.Schedule;
import com.yhj.his.module.outpatient.repository.ScheduleRepository;
import com.yhj.his.module.outpatient.service.impl.ScheduleServiceImpl;
import com.yhj.his.module.outpatient.vo.ScheduleVO;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ScheduleService单元测试
 *
 * 测试范围：
 * - 排班创建
 * - 排班更新
 * - 排班查询
 * - 排班删除
 * - 停诊/恢复
 * - 号源管理
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("排班服务测试")
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private Schedule testSchedule;
    private ScheduleCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试排班
        testSchedule = new Schedule();
        testSchedule.setId("schedule-id-001");
        testSchedule.setDeptId("DEPT001");
        testSchedule.setDeptName("内科");
        testSchedule.setDoctorId("DOC001");
        testSchedule.setDoctorName("李医生");
        testSchedule.setDoctorTitle("主任医师");
        testSchedule.setScheduleDate(LocalDate.now());
        testSchedule.setTimePeriod("上午");
        testSchedule.setStartTime(LocalTime.of(8, 0));
        testSchedule.setEndTime(LocalTime.of(12, 0));
        testSchedule.setTotalQuota(20);
        testSchedule.setBookedQuota(5);
        testSchedule.setAvailableQuota(15);
        testSchedule.setRegistrationType("专家");
        testSchedule.setRegistrationFee(BigDecimal.valueOf(50));
        testSchedule.setDiagnosisFee(BigDecimal.valueOf(100));
        testSchedule.setClinicRoom("诊室1");
        testSchedule.setStatus("正常");
        testSchedule.setDeleted(false);

        // 初始化创建请求
        createRequest = new ScheduleCreateRequest();
        createRequest.setDeptId("DEPT001");
        createRequest.setDeptName("内科");
        createRequest.setDoctorId("DOC001");
        createRequest.setDoctorName("李医生");
        createRequest.setDoctorTitle("主任医师");
        createRequest.setScheduleDate(LocalDate.now());
        createRequest.setTimePeriod("上午");
        createRequest.setStartTime(LocalTime.of(8, 0));
        createRequest.setEndTime(LocalTime.of(12, 0));
        createRequest.setTotalQuota(20);
        createRequest.setRegistrationType("专家");
        createRequest.setRegistrationFee(BigDecimal.valueOf(50));
        createRequest.setDiagnosisFee(BigDecimal.valueOf(100));
        createRequest.setClinicRoom("诊室1");
    }

    @Nested
    @DisplayName("排班创建测试")
    class CreateScheduleTests {

        @Test
        @DisplayName("成功创建排班")
        void shouldCreateScheduleSuccessfully() {
            // Given
            when(scheduleRepository.findByDoctorIdAndScheduleDateAndTimePeriod(anyString(), any(), anyString()))
                    .thenReturn(Optional.empty());
            when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> {
                Schedule schedule = invocation.getArgument(0);
                // Return the saved schedule with the values set by the implementation
                testSchedule.setBookedQuota(schedule.getBookedQuota());
                testSchedule.setAvailableQuota(schedule.getAvailableQuota());
                testSchedule.setStatus(schedule.getStatus());
                return testSchedule;
            });

            // When
            ScheduleVO result = scheduleService.createSchedule(createRequest);

            // Then
            assertNotNull(result);
            assertEquals("DOC001", result.getDoctorId());
            assertEquals("李医生", result.getDoctorName());
            assertEquals(20, result.getTotalQuota());
            assertEquals(0, result.getBookedQuota());
            assertEquals(20, result.getAvailableQuota());
            assertEquals("正常", result.getStatus());

            verify(scheduleRepository).save(any(Schedule.class));
        }

        @Test
        @DisplayName("排班已存在时抛出异常")
        void shouldThrowExceptionWhenScheduleExists() {
            // Given
            when(scheduleRepository.findByDoctorIdAndScheduleDateAndTimePeriod(anyString(), any(), anyString()))
                    .thenReturn(Optional.of(testSchedule));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> scheduleService.createSchedule(createRequest));

            assertEquals("该医生在该时间段已有排班", exception.getMessage());
            verify(scheduleRepository, never()).save(any(Schedule.class));
        }

        @Test
        @DisplayName("创建排班时自动初始化号源")
        void shouldInitializeQuotaWhenCreatingSchedule() {
            // Given
            when(scheduleRepository.findByDoctorIdAndScheduleDateAndTimePeriod(anyString(), any(), anyString()))
                    .thenReturn(Optional.empty());
            when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> {
                Schedule schedule = invocation.getArgument(0);
                assertEquals(0, schedule.getBookedQuota());
                assertEquals(20, schedule.getAvailableQuota());
                assertEquals("正常", schedule.getStatus());
                return schedule;
            });

            // When
            ScheduleVO result = scheduleService.createSchedule(createRequest);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getBookedQuota());
            assertEquals(20, result.getAvailableQuota());
        }
    }

    @Nested
    @DisplayName("排班更新测试")
    class UpdateScheduleTests {

        @Test
        @DisplayName("成功更新排班")
        void shouldUpdateScheduleSuccessfully() {
            // Given
            testSchedule.setBookedQuota(0);
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));
            when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

            // When
            ScheduleVO result = scheduleService.updateSchedule("schedule-id-001", createRequest);

            // Then
            assertNotNull(result);
            verify(scheduleRepository).save(any(Schedule.class));
        }

        @Test
        @DisplayName("排班不存在时抛出异常")
        void shouldThrowExceptionWhenScheduleNotFound() {
            // Given
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> scheduleService.updateSchedule("non-existent-id", createRequest));

            assertEquals("排班不存在", exception.getMessage());
        }

        @Test
        @DisplayName("已有预约的排班无法修改")
        void shouldThrowExceptionWhenScheduleHasBookings() {
            // Given
            testSchedule.setBookedQuota(5);
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> scheduleService.updateSchedule("schedule-id-001", createRequest));

            assertEquals("已有患者预约，无法修改排班", exception.getMessage());
            verify(scheduleRepository, never()).save(any(Schedule.class));
        }

        @Test
        @DisplayName("更新排班时重新计算可用号源")
        void shouldRecalculateAvailableQuotaWhenUpdating() {
            // Given
            testSchedule.setBookedQuota(0);
            createRequest.setTotalQuota(30);
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));
            when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> {
                Schedule schedule = invocation.getArgument(0);
                assertEquals(30, schedule.getAvailableQuota());
                return schedule;
            });

            // When
            ScheduleVO result = scheduleService.updateSchedule("schedule-id-001", createRequest);

            // Then
            assertEquals(30, result.getAvailableQuota());
        }
    }

    @Nested
    @DisplayName("排班删除测试")
    class DeleteScheduleTests {

        @Test
        @DisplayName("成功删除排班（逻辑删除）")
        void shouldDeleteScheduleSuccessfully() {
            // Given
            testSchedule.setBookedQuota(0);
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));
            when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

            // When
            scheduleService.deleteSchedule("schedule-id-001");

            // Then
            assertTrue(testSchedule.getDeleted());
            verify(scheduleRepository).save(any(Schedule.class));
        }

        @Test
        @DisplayName("排班不存在时抛出异常")
        void shouldThrowExceptionWhenDeletingNonExistentSchedule() {
            // Given
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> scheduleService.deleteSchedule("non-existent-id"));

            assertEquals("排班不存在", exception.getMessage());
        }

        @Test
        @DisplayName("已有预约的排班无法删除")
        void shouldThrowExceptionWhenDeletingScheduleWithBookings() {
            // Given
            testSchedule.setBookedQuota(5);
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> scheduleService.deleteSchedule("schedule-id-001"));

            assertEquals("已有患者预约，无法删除排班", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("停诊/恢复测试")
    class StopRestoreTests {

        @Test
        @DisplayName("成功停诊")
        void shouldStopScheduleSuccessfully() {
            // Given
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));
            when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

            // When
            ScheduleVO result = scheduleService.stopSchedule("schedule-id-001", "医生请假");

            // Then
            assertNotNull(result);
            assertEquals("停诊", result.getStatus());
            assertEquals("医生请假", testSchedule.getStopReason());
            verify(scheduleRepository).save(any(Schedule.class));
        }

        @Test
        @DisplayName("停诊排班不存在时抛出异常")
        void shouldThrowExceptionWhenStoppingNonExistentSchedule() {
            // Given
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> scheduleService.stopSchedule("non-existent-id", "原因"));

            assertEquals("排班不存在", exception.getMessage());
        }

        @Test
        @DisplayName("成功恢复排班")
        void shouldRestoreScheduleSuccessfully() {
            // Given
            testSchedule.setStatus("停诊");
            testSchedule.setStopReason("医生请假");
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));
            when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

            // When
            ScheduleVO result = scheduleService.restoreSchedule("schedule-id-001");

            // Then
            assertNotNull(result);
            assertEquals("正常", result.getStatus());
            assertNull(testSchedule.getStopReason());
            verify(scheduleRepository).save(any(Schedule.class));
        }

        @Test
        @DisplayName("恢复排班不存在时抛出异常")
        void shouldThrowExceptionWhenRestoringNonExistentSchedule() {
            // Given
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> scheduleService.restoreSchedule("non-existent-id"));

            assertEquals("排班不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("号源管理测试")
    class QuotaManagementTests {

        @Test
        @DisplayName("成功更新号源数量")
        void shouldUpdateQuotaSuccessfully() {
            // Given
            testSchedule.setBookedQuota(5);
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));
            when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

            // When
            boolean result = scheduleService.updateQuota("schedule-id-001", 30);

            // Then
            assertTrue(result);
            assertEquals(30, testSchedule.getTotalQuota());
            assertEquals(25, testSchedule.getAvailableQuota()); // 30 - 5
            verify(scheduleRepository).save(any(Schedule.class));
        }

        @Test
        @DisplayName("号源数小于已预约数时抛出异常")
        void shouldThrowExceptionWhenQuotaLessThanBooked() {
            // Given
            testSchedule.setBookedQuota(15);
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> scheduleService.updateQuota("schedule-id-001", 10));

            assertEquals("总号源数不能小于已预约数", exception.getMessage());
        }

        @Test
        @DisplayName("更新不存在排班号源时抛出异常")
        void shouldThrowExceptionWhenUpdatingQuotaForNonExistentSchedule() {
            // Given
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> scheduleService.updateQuota("non-existent-id", 30));

            assertEquals("排班不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("排班查询测试")
    class QueryTests {

        @Test
        @DisplayName("按ID查询排班")
        void shouldFindScheduleById() {
            // Given
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));

            // When
            Optional<Schedule> result = scheduleService.findById("schedule-id-001");

            // Then
            assertTrue(result.isPresent());
            assertEquals("DOC001", result.get().getDoctorId());
        }

        @Test
        @DisplayName("获取排班详情")
        void shouldGetScheduleDetail() {
            // Given
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));

            // When
            ScheduleVO result = scheduleService.getScheduleDetail("schedule-id-001");

            // Then
            assertNotNull(result);
            assertEquals("DOC001", result.getDoctorId());
            assertEquals("李医生", result.getDoctorName());
        }

        @Test
        @DisplayName("获取不存在排班详情时抛出异常")
        void shouldThrowExceptionWhenGettingNonExistentDetail() {
            // Given
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> scheduleService.getScheduleDetail("non-existent-id"));

            assertEquals("排班不存在", exception.getMessage());
        }

        @Test
        @DisplayName("分页查询排班列表")
        void shouldListSchedulesWithPagination() {
            // Given
            List<Schedule> schedules = Arrays.asList(testSchedule);
            Page<Schedule> page = new PageImpl<>(schedules);
            when(scheduleRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            // When
            Pageable pageable = PageRequest.of(0, 10);
            PageResult<ScheduleVO> result = scheduleService.listSchedules(
                    "DEPT001", "DOC001", LocalDate.now(), LocalDate.now().plusDays(7), pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("按日期查询排班")
        void shouldListSchedulesByDate() {
            // Given
            when(scheduleRepository.findByScheduleDate(any())).thenReturn(Arrays.asList(testSchedule));

            // When
            List<ScheduleVO> result = scheduleService.listSchedulesByDate(LocalDate.now());

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("按科室和日期查询排班")
        void shouldListSchedulesByDeptAndDate() {
            // Given
            when(scheduleRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(testSchedule));

            // When
            List<ScheduleVO> result = scheduleService.listSchedulesByDeptAndDate("DEPT001", LocalDate.now());

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("按医生和日期范围查询排班")
        void shouldListSchedulesByDoctor() {
            // Given
            when(scheduleRepository.findByDoctorIdAndScheduleDateBetween(anyString(), any(), any()))
                    .thenReturn(Arrays.asList(testSchedule));

            // When
            List<ScheduleVO> result = scheduleService.listSchedulesByDoctor(
                    "DOC001", LocalDate.now(), LocalDate.now().plusDays(7));

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("查询可预约排班")
        void shouldListAvailableSchedules() {
            // Given
            when(scheduleRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(testSchedule));

            // When
            List<ScheduleVO> result = scheduleService.listAvailableSchedules("DEPT001", LocalDate.now());

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.get(0).getAvailableQuota() > 0);
            assertEquals("正常", result.get(0).getStatus());
        }

        @Test
        @DisplayName("查询可预约排班-无可用排班")
        void shouldReturnEmptyWhenNoAvailableSchedules() {
            // Given
            testSchedule.setAvailableQuota(0);
            when(scheduleRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

            // When
            List<ScheduleVO> result = scheduleService.listAvailableSchedules("DEPT001", LocalDate.now());

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("VO转换测试")
    class VOConversionTests {

        @Test
        @DisplayName("正确转换排班VO")
        void shouldConvertToVOCorrectly() {
            // Given
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));

            // When
            ScheduleVO result = scheduleService.getScheduleDetail("schedule-id-001");

            // Then
            assertNotNull(result);
            assertEquals("schedule-id-001", result.getScheduleId());
            assertEquals("DEPT001", result.getDeptId());
            assertEquals("内科", result.getDeptName());
            assertEquals("DOC001", result.getDoctorId());
            assertEquals("李医生", result.getDoctorName());
            assertEquals("主任医师", result.getDoctorTitle());
            assertEquals(LocalDate.now(), result.getScheduleDate());
            assertEquals("上午", result.getTimePeriod());
            assertEquals(LocalTime.of(8, 0), result.getStartTime());
            assertEquals(LocalTime.of(12, 0), result.getEndTime());
            assertEquals(20, result.getTotalQuota());
            assertEquals(5, result.getBookedQuota());
            assertEquals(15, result.getAvailableQuota());
            assertEquals("专家", result.getRegistrationType());
            assertEquals(BigDecimal.valueOf(50), result.getRegistrationFee());
            assertEquals(BigDecimal.valueOf(100), result.getDiagnosisFee());
            assertEquals("正常", result.getStatus());
            assertEquals("诊室1", result.getClinicRoom());
        }
    }
}