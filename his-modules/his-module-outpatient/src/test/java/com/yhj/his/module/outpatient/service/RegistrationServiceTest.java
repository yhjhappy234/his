package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.outpatient.dto.AppointmentCancelRequest;
import com.yhj.his.module.outpatient.dto.AppointmentCreateRequest;
import com.yhj.his.module.outpatient.dto.CheckInRequest;
import com.yhj.his.module.outpatient.entity.Patient;
import com.yhj.his.module.outpatient.entity.Queue;
import com.yhj.his.module.outpatient.entity.Registration;
import com.yhj.his.module.outpatient.entity.Schedule;
import com.yhj.his.module.outpatient.repository.PatientRepository;
import com.yhj.his.module.outpatient.repository.QueueRepository;
import com.yhj.his.module.outpatient.repository.RegistrationRepository;
import com.yhj.his.module.outpatient.repository.OutpatientScheduleRepository;
import com.yhj.his.module.outpatient.service.impl.RegistrationServiceImpl;
import com.yhj.his.module.outpatient.vo.AppointmentResultVO;
import com.yhj.his.module.outpatient.vo.CheckInResultVO;

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
import java.time.LocalDateTime;
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
 * RegistrationService单元测试
 *
 * 测试范围：
 * - 预约挂号
 * - 取消预约
 * - 签到
 * - 排队管理
 * - 就诊流程控制
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("挂号服务测试")
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private OutpatientScheduleRepository scheduleRepository;

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private Patient testPatient;
    private Schedule testSchedule;
    private Registration testRegistration;
    private Queue testQueue;
    private AppointmentCreateRequest appointmentRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试患者
        testPatient = new Patient();
        testPatient.setId("patient-id-001");
        testPatient.setPatientId("PAT20260406001");
        testPatient.setName("张三");
        testPatient.setGender("男");
        testPatient.setIdCardNo("320123199001011234");
        testPatient.setBirthDate(LocalDate.of(1990, 1, 1));
        testPatient.setPhone("13800138000");
        testPatient.setIsBlacklist(false);

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

        // 初始化测试挂号记录
        testRegistration = new Registration();
        testRegistration.setId("registration-id-001");
        testRegistration.setPatientId("PAT20260406001");
        testRegistration.setPatientName("张三");
        testRegistration.setIdCardNo("320123199001011234");
        testRegistration.setGender("男");
        testRegistration.setAge(36);
        testRegistration.setPhone("13800138000");
        testRegistration.setDeptId("DEPT001");
        testRegistration.setDeptName("内科");
        testRegistration.setDoctorId("DOC001");
        testRegistration.setDoctorName("李医生");
        testRegistration.setScheduleId("schedule-id-001");
        testRegistration.setScheduleDate(LocalDate.now());
        testRegistration.setTimePeriod("上午");
        testRegistration.setQueueNo(6);
        testRegistration.setVisitNo("VIS20260406001");
        testRegistration.setRegistrationType("专家");
        testRegistration.setRegistrationFee(BigDecimal.valueOf(50));
        testRegistration.setDiagnosisFee(BigDecimal.valueOf(100));
        testRegistration.setTotalFee(BigDecimal.valueOf(150));
        testRegistration.setStatus("已预约");
        testRegistration.setVisitStatus("待诊");
        testRegistration.setClinicRoom("诊室1");
        testRegistration.setDeleted(false);

        // 初始化测试排队记录
        testQueue = new Queue();
        testQueue.setId("queue-id-001");
        testQueue.setRegistrationId("registration-id-001");
        testQueue.setPatientId("PAT20260406001");
        testQueue.setPatientName("张三");
        testQueue.setDeptId("DEPT001");
        testQueue.setDeptName("内科");
        testQueue.setDoctorId("DOC001");
        testQueue.setDoctorName("李医生");
        testQueue.setQueueNo(6);
        testQueue.setClinicRoom("诊室1");
        testQueue.setStatus("等候中");

        // 初始化预约请求
        appointmentRequest = new AppointmentCreateRequest();
        appointmentRequest.setPatientId("PAT20260406001");
        appointmentRequest.setDeptId("DEPT001");
        appointmentRequest.setScheduleId("schedule-id-001");
        appointmentRequest.setScheduleDate(LocalDate.now());
        appointmentRequest.setSource("微信");
    }

    @Nested
    @DisplayName("预约挂号测试")
    class CreateAppointmentTests {

        @Test
        @DisplayName("成功预约挂号")
        void shouldCreateAppointmentSuccessfully() {
            // Given
            when(patientRepository.findByPatientId(anyString())).thenReturn(Optional.of(testPatient));
            when(scheduleRepository.findById("schedule-id-001")).thenReturn(Optional.of(testSchedule));
            when(scheduleRepository.incrementBookedQuota(anyString())).thenReturn(1);
            when(registrationRepository.findMaxQueueNoByScheduleId(anyString())).thenReturn(Optional.of(5));
            when(sequenceGenerator.generate(anyString(), anyInt())).thenReturn("VIS20260406001");
            when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);

            // When
            AppointmentResultVO result = registrationService.createAppointment(appointmentRequest);

            // Then
            assertNotNull(result);
            assertEquals("VIS20260406001", result.getVisitNo());
            assertEquals(6, result.getQueueNo());
            assertEquals(BigDecimal.valueOf(150), result.getTotalFee());
            assertEquals("诊室1", result.getClinicRoom());
            assertEquals("李医生", result.getDoctorName());

            verify(scheduleRepository).incrementBookedQuota("schedule-id-001");
            verify(registrationRepository).save(any(Registration.class));
        }

        @Test
        @DisplayName("患者不存在时抛出异常")
        void shouldThrowExceptionWhenPatientNotFound() {
            // Given
            when(patientRepository.findByPatientId(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.createAppointment(appointmentRequest));

            assertEquals("患者不存在", exception.getMessage());
            verify(scheduleRepository, never()).incrementBookedQuota(anyString());
        }

        @Test
        @DisplayName("黑名单患者无法预约")
        void shouldThrowExceptionWhenPatientIsBlacklisted() {
            // Given
            testPatient.setIsBlacklist(true);
            when(patientRepository.findByPatientId(anyString())).thenReturn(Optional.of(testPatient));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.createAppointment(appointmentRequest));

            assertEquals("患者已在黑名单中，无法预约挂号", exception.getMessage());
        }

        @Test
        @DisplayName("排班不存在时抛出异常")
        void shouldThrowExceptionWhenScheduleNotFound() {
            // Given
            when(patientRepository.findByPatientId(anyString())).thenReturn(Optional.of(testPatient));
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.createAppointment(appointmentRequest));

            assertEquals("排班不存在", exception.getMessage());
        }

        @Test
        @DisplayName("排班已停诊时抛出异常")
        void shouldThrowExceptionWhenScheduleStopped() {
            // Given
            testSchedule.setStatus("停诊");
            when(patientRepository.findByPatientId(anyString())).thenReturn(Optional.of(testPatient));
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.createAppointment(appointmentRequest));

            assertEquals("排班已停诊", exception.getMessage());
        }

        @Test
        @DisplayName("号源已满时抛出异常")
        void shouldThrowExceptionWhenNoAvailableQuota() {
            // Given
            testSchedule.setAvailableQuota(0);
            when(patientRepository.findByPatientId(anyString())).thenReturn(Optional.of(testPatient));
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.createAppointment(appointmentRequest));

            assertEquals("号源已满", exception.getMessage());
        }

        @Test
        @DisplayName("并发预约失败时抛出异常")
        void shouldThrowExceptionWhenConcurrentBookingFailed() {
            // Given
            when(patientRepository.findByPatientId(anyString())).thenReturn(Optional.of(testPatient));
            when(scheduleRepository.findById(anyString())).thenReturn(Optional.of(testSchedule));
            when(scheduleRepository.incrementBookedQuota(anyString())).thenReturn(0); // 并发更新失败

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.createAppointment(appointmentRequest));

            assertEquals("预约失败，号源不足", exception.getMessage());
        }

        @Test
        @DisplayName("自动选择排班")
        void shouldAutoSelectSchedule() {
            // Given
            appointmentRequest.setScheduleId(null);
            List<Schedule> schedules = Arrays.asList(testSchedule);
            when(patientRepository.findByPatientId(anyString())).thenReturn(Optional.of(testPatient));
            when(scheduleRepository.findByDeptIdAndScheduleDateBetween(anyString(), any(), any()))
                    .thenReturn(schedules);
            when(scheduleRepository.incrementBookedQuota(anyString())).thenReturn(1);
            when(registrationRepository.findMaxQueueNoByScheduleId(anyString())).thenReturn(Optional.of(5));
            when(sequenceGenerator.generate(anyString(), anyInt())).thenReturn("VIS20260406001");
            when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);

            // When
            AppointmentResultVO result = registrationService.createAppointment(appointmentRequest);

            // Then
            assertNotNull(result);
            verify(scheduleRepository).findByDeptIdAndScheduleDateBetween(anyString(), any(), any());
        }

        @Test
        @DisplayName("无可用排班时抛出异常")
        void shouldThrowExceptionWhenNoAvailableSchedule() {
            // Given
            appointmentRequest.setScheduleId(null);
            testSchedule.setAvailableQuota(0);
            when(patientRepository.findByPatientId(anyString())).thenReturn(Optional.of(testPatient));
            when(scheduleRepository.findByDeptIdAndScheduleDateBetween(anyString(), any(), any()))
                    .thenReturn(Arrays.asList(testSchedule));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.createAppointment(appointmentRequest));

            assertEquals("无可预约排班", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("取消预约测试")
    class CancelAppointmentTests {

        @Test
        @DisplayName("成功取消预约")
        void shouldCancelAppointmentSuccessfully() {
            // Given
            AppointmentCancelRequest cancelRequest = new AppointmentCancelRequest();
            cancelRequest.setRegistrationId("registration-id-001");
            cancelRequest.setCancelReason("个人原因");

            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(scheduleRepository.decrementBookedQuota(anyString())).thenReturn(1);
            when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);
            when(queueRepository.findByRegistrationId(anyString())).thenReturn(Optional.empty());

            // When
            registrationService.cancelAppointment(cancelRequest);

            // Then
            verify(scheduleRepository).decrementBookedQuota("schedule-id-001");
            verify(registrationRepository).save(any(Registration.class));
            assertEquals("已退号", testRegistration.getStatus());
            assertNotNull(testRegistration.getCancelReason());
        }

        @Test
        @DisplayName("取消已签到的预约应删除排队记录")
        void shouldDeleteQueueWhenCancelCheckedInAppointment() {
            // Given
            testRegistration.setStatus("已签到");
            AppointmentCancelRequest cancelRequest = new AppointmentCancelRequest();
            cancelRequest.setRegistrationId("registration-id-001");

            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(scheduleRepository.decrementBookedQuota(anyString())).thenReturn(1);
            when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);
            when(queueRepository.findByRegistrationId(anyString())).thenReturn(Optional.of(testQueue));

            // When
            registrationService.cancelAppointment(cancelRequest);

            // Then
            verify(queueRepository).delete(testQueue);
        }

        @Test
        @DisplayName("挂号记录不存在时抛出异常")
        void shouldThrowExceptionWhenRegistrationNotFound() {
            // Given
            AppointmentCancelRequest cancelRequest = new AppointmentCancelRequest();
            cancelRequest.setRegistrationId("non-existent-id");

            when(registrationRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.cancelAppointment(cancelRequest));

            assertEquals("挂号记录不存在", exception.getMessage());
        }

        @Test
        @DisplayName("状态不允许取消时抛出异常")
        void shouldThrowExceptionWhenStatusNotAllowCancel() {
            // Given
            testRegistration.setStatus("已就诊");
            AppointmentCancelRequest cancelRequest = new AppointmentCancelRequest();
            cancelRequest.setRegistrationId("registration-id-001");

            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.cancelAppointment(cancelRequest));

            assertEquals("当前状态无法取消预约", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("签到测试")
    class CheckInTests {

        @Test
        @DisplayName("成功签到")
        void shouldCheckInSuccessfully() {
            // Given
            CheckInRequest checkInRequest = new CheckInRequest();
            checkInRequest.setRegistrationId("registration-id-001");
            checkInRequest.setPatientId("PAT20260406001");

            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);
            when(queueRepository.save(any(Queue.class))).thenReturn(testQueue);
            when(queueRepository.countWaiting(anyString(), any())).thenReturn(5);

            // When
            CheckInResultVO result = registrationService.checkIn(checkInRequest);

            // Then
            assertNotNull(result);
            assertEquals(6, result.getQueueNo());
            assertEquals(5, result.getWaitCount());
            assertEquals(50, result.getEstimatedWaitTime()); // 5 * 10分钟
            assertEquals("诊室1", result.getClinicRoom());

            verify(queueRepository).save(any(Queue.class));
        }

        @Test
        @DisplayName("患者信息不匹配时抛出异常")
        void shouldThrowExceptionWhenPatientMismatch() {
            // Given
            CheckInRequest checkInRequest = new CheckInRequest();
            checkInRequest.setRegistrationId("registration-id-001");
            checkInRequest.setPatientId("PAT99999");

            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.checkIn(checkInRequest));

            assertEquals("患者信息不匹配", exception.getMessage());
        }

        @Test
        @DisplayName("状态不允许签到时抛出异常")
        void shouldThrowExceptionWhenStatusNotAllowCheckIn() {
            // Given
            testRegistration.setStatus("已就诊");
            CheckInRequest checkInRequest = new CheckInRequest();
            checkInRequest.setRegistrationId("registration-id-001");
            checkInRequest.setPatientId("PAT20260406001");

            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.checkIn(checkInRequest));

            assertEquals("当前状态无法签到", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("就诊流程测试")
    class VisitProcessTests {

        @Test
        @DisplayName("开始就诊")
        void shouldStartVisitSuccessfully() {
            // Given
            testRegistration.setStatus("已签到");
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);
            when(queueRepository.findByRegistrationId(anyString())).thenReturn(Optional.of(testQueue));
            when(queueRepository.save(any(Queue.class))).thenReturn(testQueue);

            // When
            Registration result = registrationService.startVisit("registration-id-001", "DOC001");

            // Then
            assertEquals("就诊中", result.getVisitStatus());
            assertNotNull(result.getStartTime());
            verify(queueRepository).save(any(Queue.class));
        }

        @Test
        @DisplayName("非该医生挂号时抛出异常")
        void shouldThrowExceptionWhenNotSameDoctor() {
            // Given
            testRegistration.setStatus("已签到");
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.startVisit("registration-id-001", "DOC999"));

            assertEquals("非该医生的挂号记录", exception.getMessage());
        }

        @Test
        @DisplayName("患者未签到时抛出异常")
        void shouldThrowExceptionWhenNotCheckedIn() {
            // Given
            testRegistration.setStatus("已预约");
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.startVisit("registration-id-001", "DOC001"));

            assertEquals("患者未签到", exception.getMessage());
        }

        @Test
        @DisplayName("结束就诊")
        void shouldEndVisitSuccessfully() {
            // Given
            testRegistration.setVisitStatus("就诊中");
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);
            when(queueRepository.findByRegistrationId(anyString())).thenReturn(Optional.of(testQueue));
            when(queueRepository.save(any(Queue.class))).thenReturn(testQueue);

            // When
            Registration result = registrationService.endVisit("registration-id-001");

            // Then
            assertEquals("已完成", result.getVisitStatus());
            assertNotNull(result.getEndTime());
        }
    }

    @Nested
    @DisplayName("退号测试")
    class RefundTests {

        @Test
        @DisplayName("成功退号")
        void shouldRefundSuccessfully() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(scheduleRepository.decrementBookedQuota(anyString())).thenReturn(1);
            when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);
            when(queueRepository.findByRegistrationId(anyString())).thenReturn(Optional.of(testQueue));

            // When
            registrationService.refundRegistration("registration-id-001", "患者要求退号");

            // Then
            assertEquals("已退号", testRegistration.getStatus());
            verify(scheduleRepository).decrementBookedQuota("schedule-id-001");
            verify(queueRepository).delete(any(Queue.class));
        }

        @Test
        @DisplayName("已退号的挂号再次退号时抛出异常")
        void shouldThrowExceptionWhenAlreadyRefunded() {
            // Given
            testRegistration.setStatus("已退号");
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.refundRegistration("registration-id-001", "再次退号"));

            assertEquals("挂号已退", exception.getMessage());
        }

        @Test
        @DisplayName("已完成的就诊无法退号")
        void shouldThrowExceptionWhenVisitCompleted() {
            // Given
            testRegistration.setVisitStatus("已完成");
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.refundRegistration("registration-id-001", "退号"));

            assertEquals("就诊已完成，无法退号", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("挂号查询测试")
    class QueryTests {

        @Test
        @DisplayName("按ID查询挂号")
        void shouldFindRegistrationById() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));

            // When
            Optional<Registration> result = registrationService.findById("registration-id-001");

            // Then
            assertTrue(result.isPresent());
            assertEquals("张三", result.get().getPatientName());
        }

        @Test
        @DisplayName("按就诊序号查询")
        void shouldFindByVisitNo() {
            // Given
            when(registrationRepository.findByVisitNo(anyString())).thenReturn(Optional.of(testRegistration));

            // When
            Optional<Registration> result = registrationService.findByVisitNo("VIS20260406001");

            // Then
            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("获取挂号详情")
        void shouldGetRegistrationDetail() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));

            // When
            Registration result = registrationService.getRegistrationDetail("registration-id-001");

            // Then
            assertNotNull(result);
            assertEquals("张三", result.getPatientName());
        }

        @Test
        @DisplayName("获取不存在挂号详情时抛出异常")
        void shouldThrowExceptionWhenGetNonExistentDetail() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> registrationService.getRegistrationDetail("non-existent-id"));

            assertEquals("挂号记录不存在", exception.getMessage());
        }

        @Test
        @DisplayName("分页查询挂号列表")
        void shouldListRegistrationsWithPagination() {
            // Given
            List<Registration> registrations = Arrays.asList(testRegistration);
            Page<Registration> page = new PageImpl<>(registrations);
            when(registrationRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            // When
            Pageable pageable = PageRequest.of(0, 10);
            PageResult<Registration> result = registrationService.listRegistrations(
                    "PAT20260406001", "DEPT001", "DOC001", "已预约", LocalDate.now(), pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("查询患者挂号记录")
        void shouldListPatientRegistrations() {
            // Given
            when(registrationRepository.findByPatientIdOrderByScheduleDateDesc(anyString()))
                    .thenReturn(Arrays.asList(testRegistration));

            // When
            List<Registration> result = registrationService.listPatientRegistrations("PAT20260406001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("查询医生当日挂号记录")
        void shouldListDoctorRegistrations() {
            // Given
            when(registrationRepository.findByDoctorIdAndScheduleDate(anyString(), any()))
                    .thenReturn(Arrays.asList(testRegistration));

            // When
            List<Registration> result = registrationService.listDoctorRegistrations("DOC001", LocalDate.now());

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("查询待诊患者列表")
        void shouldListWaitingPatients() {
            // Given
            when(registrationRepository.findWaitingPatients(anyString(), any()))
                    .thenReturn(Arrays.asList(testRegistration));

            // When
            List<Registration> result = registrationService.listWaitingPatients("DOC001", LocalDate.now());

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("获取当前就诊患者")
        void shouldGetCurrentPatient() {
            // Given
            when(registrationRepository.findCurrentPatient(anyString(), any()))
                    .thenReturn(Optional.of(testRegistration));

            // When
            Optional<Registration> result = registrationService.getCurrentPatient("DOC001", LocalDate.now());

            // Then
            assertTrue(result.isPresent());
        }
    }
}