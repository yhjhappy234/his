package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.PrescriptionCreateRequest;
import com.yhj.his.module.outpatient.entity.OutpatientPrescription;
import com.yhj.his.module.outpatient.entity.PrescriptionDetail;
import com.yhj.his.module.outpatient.entity.Registration;
import com.yhj.his.module.outpatient.repository.OutpatientPrescriptionRepository;
import com.yhj.his.module.outpatient.repository.PrescriptionDetailRepository;
import com.yhj.his.module.outpatient.repository.RegistrationRepository;
import com.yhj.his.module.outpatient.service.impl.OutpatientPrescriptionServiceImpl;
import com.yhj.his.module.outpatient.vo.PrescriptionResultVO;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * PrescriptionService单元测试
 *
 * 测试范围：
 * - 处方开立
 * - 处方审核
 * - 处方查询
 * - 处方作废
 * - 处方金额计算
 * - 处方更新
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("门诊处方服务测试")
class PrescriptionServiceTest {

    @Mock
    private OutpatientPrescriptionRepository prescriptionRepository;

    @Mock
    private PrescriptionDetailRepository detailRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private OutpatientPrescriptionServiceImpl prescriptionService;

    private Registration testRegistration;
    private OutpatientPrescription testPrescription;
    private PrescriptionDetail testDetail;
    private PrescriptionCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        // 初始化测试挂号记录
        testRegistration = new Registration();
        testRegistration.setId("registration-id-001");
        testRegistration.setPatientId("PAT20260406001");
        testRegistration.setPatientName("张三");
        testRegistration.setGender("男");
        testRegistration.setAge(36);
        testRegistration.setDeptId("DEPT001");
        testRegistration.setDeptName("内科");
        testRegistration.setDoctorId("DOC001");
        testRegistration.setDoctorName("李医生");

        // 初始化测试处方
        testPrescription = new OutpatientPrescription();
        testPrescription.setId("prescription-id-001");
        testPrescription.setPrescriptionNo("RX202604060001");
        testPrescription.setRegistrationId("registration-id-001");
        testPrescription.setPatientId("PAT20260406001");
        testPrescription.setPatientName("张三");
        testPrescription.setGender("男");
        testPrescription.setAge(36);
        testPrescription.setDeptId("DEPT001");
        testPrescription.setDeptName("内科");
        testPrescription.setDoctorId("DOC001");
        testPrescription.setDoctorName("李医生");
        testPrescription.setPrescriptionType("西药");
        testPrescription.setPrescriptionDate(LocalDate.now());
        testPrescription.setDiagnosisCode("J00");
        testPrescription.setDiagnosisName("急性上呼吸道感染");
        testPrescription.setTotalAmount(BigDecimal.valueOf(150));
        testPrescription.setPayStatus("未收费");
        testPrescription.setStatus("有效");
        testPrescription.setAuditStatus("审核通过");
        testPrescription.setDeleted(false);

        // 初始化测试处方明细
        testDetail = new PrescriptionDetail();
        testDetail.setId("detail-id-001");
        testDetail.setPrescriptionId("prescription-id-001");
        testDetail.setDrugId("DRUG001");
        testDetail.setDrugName("阿莫西林胶囊");
        testDetail.setDrugSpec("0.5g*24粒");
        testDetail.setDrugUnit("盒");
        testDetail.setDrugForm("胶囊");
        testDetail.setQuantity(BigDecimal.valueOf(2));
        testDetail.setDosage("每次0.5g");
        testDetail.setFrequency("每日3次");
        testDetail.setDays(7);
        testDetail.setRoute("口服");
        testDetail.setUnitPrice(BigDecimal.valueOf(25));
        testDetail.setAmount(BigDecimal.valueOf(50));
        testDetail.setSkinTest("不需要");
        testDetail.setIsEssential(true);
        testDetail.setIsMedicalInsurance(true);

        // 初始化创建请求
        PrescriptionCreateRequest.PrescriptionDetailRequest detailReq = new PrescriptionCreateRequest.PrescriptionDetailRequest();
        detailReq.setDrugId("DRUG001");
        detailReq.setDrugName("阿莫西林胶囊");
        detailReq.setDrugSpec("0.5g*24粒");
        detailReq.setDrugUnit("盒");
        detailReq.setDrugForm("胶囊");
        detailReq.setQuantity(BigDecimal.valueOf(2));
        detailReq.setDosage("每次0.5g");
        detailReq.setFrequency("每日3次");
        detailReq.setDays(7);
        detailReq.setRoute("口服");
        detailReq.setUnitPrice(BigDecimal.valueOf(25));
        detailReq.setSkinTest("不需要");
        detailReq.setIsEssential(true);
        detailReq.setIsMedicalInsurance(true);

        createRequest = new PrescriptionCreateRequest();
        createRequest.setRegistrationId("registration-id-001");
        createRequest.setPatientId("PAT20260406001");
        createRequest.setPrescriptionType("西药");
        createRequest.setDiagnosisCode("J00");
        createRequest.setDiagnosisName("急性上呼吸道感染");
        createRequest.setDetails(Arrays.asList(detailReq));
    }

    @Nested
    @DisplayName("处方开立测试")
    class CreatePrescriptionTests {

        @Test
        @DisplayName("成功开立处方")
        void shouldCreatePrescriptionSuccessfully() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenReturn(testPrescription);
            when(detailRepository.saveAll(anyList())).thenReturn(Arrays.asList(testDetail));

            // When
            PrescriptionResultVO result = prescriptionService.createPrescription(createRequest);

            // Then
            assertNotNull(result);
            assertEquals("prescription-id-001", result.getPrescriptionId());
            assertEquals("RX202604060001", result.getPrescriptionNo());
            assertNotNull(result.getTotalAmount());
            assertNotNull(result.getDetails());
            assertEquals(1, result.getDetails().size());

            verify(prescriptionRepository).save(any(OutpatientPrescription.class));
            verify(detailRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("开立处方时自动生成处方号")
        void shouldGeneratePrescriptionNo() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenAnswer(invocation -> {
                OutpatientPrescription presc = invocation.getArgument(0);
                presc.setId("prescription-id-001");
                assertNotNull(presc.getPrescriptionNo());
                assertTrue(presc.getPrescriptionNo().startsWith("RX"));
                return presc;
            });
            when(detailRepository.saveAll(anyList())).thenReturn(Arrays.asList(testDetail));

            // When
            PrescriptionResultVO result = prescriptionService.createPrescription(createRequest);

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("正确计算处方金额")
        void shouldCalculateTotalAmountCorrectly() {
            // Given
            PrescriptionCreateRequest.PrescriptionDetailRequest detail1 = new PrescriptionCreateRequest.PrescriptionDetailRequest();
            detail1.setDrugId("DRUG001");
            detail1.setDrugName("阿莫西林");
            detail1.setQuantity(BigDecimal.valueOf(2));
            detail1.setUnitPrice(BigDecimal.valueOf(50));

            PrescriptionCreateRequest.PrescriptionDetailRequest detail2 = new PrescriptionCreateRequest.PrescriptionDetailRequest();
            detail2.setDrugId("DRUG002");
            detail2.setDrugName("布洛芬");
            detail2.setQuantity(BigDecimal.valueOf(3));
            detail2.setUnitPrice(BigDecimal.valueOf(30));

            createRequest.setDetails(Arrays.asList(detail1, detail2));

            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenAnswer(invocation -> {
                OutpatientPrescription presc = invocation.getArgument(0);
                // 总金额 = 2*50 + 3*30 = 100 + 90 = 190
                assertEquals(BigDecimal.valueOf(190), presc.getTotalAmount());
                presc.setId("prescription-id-001");
                return presc;
            });
            when(detailRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

            // When
            PrescriptionResultVO result = prescriptionService.createPrescription(createRequest);

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("需要皮试的药品应生成警告")
        void shouldGenerateWarningForSkinTestDrugs() {
            // Given
            PrescriptionCreateRequest.PrescriptionDetailRequest detailReq = new PrescriptionCreateRequest.PrescriptionDetailRequest();
            detailReq.setDrugId("DRUG001");
            detailReq.setDrugName("青霉素注射液");
            detailReq.setQuantity(BigDecimal.valueOf(1));
            detailReq.setUnitPrice(BigDecimal.valueOf(50));
            detailReq.setSkinTest("需要");

            createRequest.setDetails(Arrays.asList(detailReq));

            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenReturn(testPrescription);
            when(detailRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

            // When
            PrescriptionResultVO result = prescriptionService.createPrescription(createRequest);

            // Then
            assertNotNull(result);
            assertNotNull(result.getWarnings());
            assertTrue(result.getWarnings().size() > 0);
            assertTrue(result.getWarnings().get(0).contains("皮试"));
        }

        @Test
        @DisplayName("挂号不存在时抛出异常")
        void shouldThrowExceptionWhenRegistrationNotFound() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> prescriptionService.createPrescription(createRequest));

            assertEquals("挂号记录不存在", exception.getMessage());
        }

        @Test
        @DisplayName("初始处方状态应为有效")
        void shouldInitializeDefaultStatus() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenAnswer(invocation -> {
                OutpatientPrescription presc = invocation.getArgument(0);
                assertEquals("有效", presc.getStatus());
                assertEquals("未收费", presc.getPayStatus());
                assertEquals("待审核", presc.getAuditStatus());
                presc.setId("prescription-id-001");
                return presc;
            });
            when(detailRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

            // When
            prescriptionService.createPrescription(createRequest);

            // Then
            verify(prescriptionRepository).save(any(OutpatientPrescription.class));
        }
    }

    @Nested
    @DisplayName("处方审核测试")
    class AuditPrescriptionTests {

        @Test
        @DisplayName("审核通过处方")
        void shouldAuditPrescriptionApproved() {
            // Given
            testPrescription.setAuditStatus("待审核");
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenReturn(testPrescription);

            // When
            OutpatientPrescription result = prescriptionService.auditPrescription(
                    "prescription-id-001", true, "AUDITOR001", "审核员张", "处方合规");

            // Then
            assertEquals("审核通过", result.getAuditStatus());
            assertEquals("AUDITOR001", result.getAuditorId());
            assertEquals("审核员张", result.getAuditorName());
            assertNotNull(result.getAuditTime());
            verify(prescriptionRepository).save(any(OutpatientPrescription.class));
        }

        @Test
        @DisplayName("审核不通过处方")
        void shouldAuditPrescriptionRejected() {
            // Given
            testPrescription.setAuditStatus("待审核");
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenReturn(testPrescription);

            // When
            OutpatientPrescription result = prescriptionService.auditPrescription(
                    "prescription-id-001", false, "AUDITOR001", "审核员张", "剂量超标");

            // Then
            assertEquals("审核不通过", result.getAuditStatus());
            assertEquals("已作废", result.getStatus());
            verify(prescriptionRepository).save(any(OutpatientPrescription.class));
        }

        @Test
        @DisplayName("审核不存在处方时抛出异常")
        void shouldThrowExceptionWhenAuditingNonExistentPrescription() {
            // Given
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> prescriptionService.auditPrescription("non-existent-id", true, "AUDITOR001", "审核员", "备注"));

            assertEquals("处方不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("处方查询测试")
    class QueryTests {

        @Test
        @DisplayName("按ID查询处方")
        void shouldFindPrescriptionById() {
            // Given
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));

            // When
            Optional<OutpatientPrescription> result = prescriptionService.findById("prescription-id-001");

            // Then
            assertTrue(result.isPresent());
            assertEquals("RX202604060001", result.get().getPrescriptionNo());
        }

        @Test
        @DisplayName("按处方号查询")
        void shouldFindByPrescriptionNo() {
            // Given
            when(prescriptionRepository.findByPrescriptionNo(anyString())).thenReturn(Optional.of(testPrescription));

            // When
            Optional<OutpatientPrescription> result = prescriptionService.findByPrescriptionNo("RX202604060001");

            // Then
            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("获取处方详情")
        void shouldGetPrescriptionDetail() {
            // Given
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));
            when(detailRepository.findByPrescriptionId(anyString())).thenReturn(Arrays.asList(testDetail));

            // When
            PrescriptionResultVO result = prescriptionService.getPrescriptionDetail("prescription-id-001");

            // Then
            assertNotNull(result);
            assertEquals("prescription-id-001", result.getPrescriptionId());
            assertEquals(1, result.getDetails().size());
            assertEquals("阿莫西林胶囊", result.getDetails().get(0).getDrugName());
        }

        @Test
        @DisplayName("获取不存在处方详情时抛出异常")
        void shouldThrowExceptionWhenPrescriptionNotFound() {
            // Given
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> prescriptionService.getPrescriptionDetail("non-existent-id"));

            assertEquals("处方不存在", exception.getMessage());
        }

        @Test
        @DisplayName("分页查询处方列表")
        void shouldListPrescriptionsWithPagination() {
            // Given
            List<OutpatientPrescription> prescriptions = Arrays.asList(testPrescription);
            Page<OutpatientPrescription> page = new PageImpl<>(prescriptions);
            when(prescriptionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            // When
            Pageable pageable = PageRequest.of(0, 10);
            PageResult<OutpatientPrescription> result = prescriptionService.listPrescriptions(
                    "PAT20260406001", "DOC001", "未收费", "有效",
                    LocalDate.now().minusDays(7), LocalDate.now(), pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("按挂号ID查询处方列表")
        void shouldListPrescriptionsByRegistration() {
            // Given
            when(prescriptionRepository.findByRegistrationId(anyString())).thenReturn(Arrays.asList(testPrescription));

            // When
            List<OutpatientPrescription> result = prescriptionService.listPrescriptionsByRegistration("registration-id-001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("按患者ID查询处方列表")
        void shouldListPatientPrescriptions() {
            // Given
            when(prescriptionRepository.findByPatientIdOrderByPrescriptionDateDesc(anyString()))
                    .thenReturn(Arrays.asList(testPrescription));

            // When
            List<OutpatientPrescription> result = prescriptionService.listPatientPrescriptions("PAT20260406001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("处方作废测试")
    class VoidPrescriptionTests {

        @Test
        @DisplayName("成功作废处方")
        void shouldVoidPrescriptionSuccessfully() {
            // Given
            testPrescription.setPayStatus("未收费");
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenReturn(testPrescription);
            doNothing().when(detailRepository).deleteByPrescriptionId(anyString());

            // When
            prescriptionService.voidPrescription("prescription-id-001", "患者取消用药");

            // Then
            assertEquals("已作废", testPrescription.getStatus());
            verify(detailRepository).deleteByPrescriptionId("prescription-id-001");
            verify(prescriptionRepository).save(any(OutpatientPrescription.class));
        }

        @Test
        @DisplayName("处方不存在时抛出异常")
        void shouldThrowExceptionWhenVoidingNonExistentPrescription() {
            // Given
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> prescriptionService.voidPrescription("non-existent-id", "原因"));

            assertEquals("处方不存在", exception.getMessage());
        }

        @Test
        @DisplayName("已收费处方无法作废")
        void shouldThrowExceptionWhenVoidingPaidPrescription() {
            // Given
            testPrescription.setPayStatus("已收费");
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> prescriptionService.voidPrescription("prescription-id-001", "原因"));

            assertEquals("已收费处方无法作废，请先退费", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("处方金额计算测试")
    class CalculateAmountTests {

        @Test
        @DisplayName("成功计算处方金额")
        void shouldCalculateAmountSuccessfully() {
            // Given
            PrescriptionDetail detail1 = new PrescriptionDetail();
            detail1.setAmount(BigDecimal.valueOf(50));

            PrescriptionDetail detail2 = new PrescriptionDetail();
            detail2.setAmount(BigDecimal.valueOf(100));

            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));
            when(detailRepository.findByPrescriptionId(anyString())).thenReturn(Arrays.asList(detail1, detail2));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenReturn(testPrescription);

            // When
            PrescriptionResultVO result = prescriptionService.calculateAmount("prescription-id-001");

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(150), testPrescription.getTotalAmount());
            verify(prescriptionRepository).save(any(OutpatientPrescription.class));
        }

        @Test
        @DisplayName("处方不存在时抛出异常")
        void shouldThrowExceptionWhenCalculatingNonExistentPrescription() {
            // Given
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> prescriptionService.calculateAmount("non-existent-id"));

            assertEquals("处方不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("处方更新测试")
    class UpdatePrescriptionTests {

        @Test
        @DisplayName("成功更新处方")
        void shouldUpdatePrescriptionSuccessfully() {
            // Given
            testPrescription.setStatus("有效");
            testPrescription.setPayStatus("未收费");

            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));
            doNothing().when(detailRepository).deleteByPrescriptionId(anyString());
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenReturn(testPrescription);
            when(detailRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

            // When
            PrescriptionResultVO result = prescriptionService.updatePrescription("prescription-id-001", createRequest);

            // Then
            assertNotNull(result);
            verify(detailRepository).deleteByPrescriptionId("prescription-id-001");
            verify(prescriptionRepository).save(any(OutpatientPrescription.class));
        }

        @Test
        @DisplayName("处方不存在时抛出异常")
        void shouldThrowExceptionWhenUpdatingNonExistentPrescription() {
            // Given
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> prescriptionService.updatePrescription("non-existent-id", createRequest));

            assertEquals("处方不存在", exception.getMessage());
        }

        @Test
        @DisplayName("非有效状态处方无法修改")
        void shouldThrowExceptionWhenUpdatingInvalidPrescription() {
            // Given
            testPrescription.setStatus("已作废");
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> prescriptionService.updatePrescription("prescription-id-001", createRequest));

            assertEquals("只能修改有效状态的处方", exception.getMessage());
        }

        @Test
        @DisplayName("已收费处方无法修改")
        void shouldThrowExceptionWhenUpdatingPaidPrescription() {
            // Given
            testPrescription.setStatus("有效");
            testPrescription.setPayStatus("已收费");
            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> prescriptionService.updatePrescription("prescription-id-001", createRequest));

            assertEquals("已收费处方无法修改", exception.getMessage());
        }

        @Test
        @DisplayName("更新处方时删除旧明细并创建新明细")
        void shouldDeleteOldDetailsAndCreateNew() {
            // Given
            testPrescription.setStatus("有效");
            testPrescription.setPayStatus("未收费");

            when(prescriptionRepository.findById(anyString())).thenReturn(Optional.of(testPrescription));
            doNothing().when(detailRepository).deleteByPrescriptionId(anyString());
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenReturn(testPrescription);
            when(detailRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

            // When
            prescriptionService.updatePrescription("prescription-id-001", createRequest);

            // Then
            verify(detailRepository).deleteByPrescriptionId("prescription-id-001");
            verify(detailRepository).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("处方明细测试")
    class PrescriptionDetailTests {

        @Test
        @DisplayName("处方明细正确保存")
        void shouldSavePrescriptionDetails() {
            // Given
            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenReturn(testPrescription);
            when(detailRepository.saveAll(anyList())).thenAnswer(invocation -> {
                List<PrescriptionDetail> details = invocation.getArgument(0);
                for (PrescriptionDetail detail : details) {
                    assertEquals("prescription-id-001", detail.getPrescriptionId());
                }
                return details;
            });

            // When
            PrescriptionResultVO result = prescriptionService.createPrescription(createRequest);

            // Then
            assertNotNull(result);
            verify(detailRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("正确计算明细金额")
        void shouldCalculateDetailAmountCorrectly() {
            // Given
            PrescriptionCreateRequest.PrescriptionDetailRequest detailReq = new PrescriptionCreateRequest.PrescriptionDetailRequest();
            detailReq.setDrugId("DRUG001");
            detailReq.setDrugName("测试药品");
            detailReq.setQuantity(BigDecimal.valueOf(3));
            detailReq.setUnitPrice(BigDecimal.valueOf(20));

            createRequest.setDetails(Arrays.asList(detailReq));

            when(registrationRepository.findById(anyString())).thenReturn(Optional.of(testRegistration));
            when(prescriptionRepository.save(any(OutpatientPrescription.class))).thenReturn(testPrescription);
            when(detailRepository.saveAll(anyList())).thenAnswer(invocation -> {
                List<PrescriptionDetail> details = invocation.getArgument(0);
                assertEquals(1, details.size());
                assertEquals(BigDecimal.valueOf(60), details.get(0).getAmount()); // 3 * 20
                return details;
            });

            // When
            prescriptionService.createPrescription(createRequest);

            // Then
            verify(detailRepository).saveAll(anyList());
        }
    }
}