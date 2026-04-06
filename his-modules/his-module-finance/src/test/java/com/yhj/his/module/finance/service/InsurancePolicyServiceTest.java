package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.InsurancePolicyCreateDTO;
import com.yhj.his.module.finance.dto.InsurancePolicyUpdateDTO;
import com.yhj.his.module.finance.entity.InsurancePolicy;
import com.yhj.his.module.finance.repository.InsurancePolicyRepository;
import com.yhj.his.module.finance.service.impl.InsurancePolicyServiceImpl;
import com.yhj.his.module.finance.vo.InsurancePolicyVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * InsurancePolicyService Unit Tests
 *
 * Tests cover insurance policy operations including:
 * - Create, update, delete insurance policies
 * - Query operations (by ID, type)
 * - Insurance amount calculation
 * - Settlement calculation
 * - Status management
 *
 * Target coverage: 90%+
 */
@ExtendWith(MockitoExtension.class)
class InsurancePolicyServiceTest {

    @Mock
    private InsurancePolicyRepository insurancePolicyRepository;

    @InjectMocks
    private InsurancePolicyServiceImpl insurancePolicyService;

    private InsurancePolicyCreateDTO createDTO;
    private InsurancePolicyUpdateDTO updateDTO;
    private InsurancePolicy policy;

    @BeforeEach
    void setUp() {
        // Setup create DTO
        createDTO = new InsurancePolicyCreateDTO();
        createDTO.setPolicyName("城镇职工医保政策");
        createDTO.setInsuranceType("URBAN_EMPLOYEE");
        createDTO.setDeductibleLine(BigDecimal.valueOf(800));
        createDTO.setCapLine(BigDecimal.valueOf(50000));
        createDTO.setClassARatio(BigDecimal.valueOf(90));
        createDTO.setClassBRatio(BigDecimal.valueOf(80));
        createDTO.setClassCRatio(BigDecimal.valueOf(0));
        createDTO.setOutpatientRatio(BigDecimal.valueOf(70));
        createDTO.setInpatientRatio(BigDecimal.valueOf(85));
        createDTO.setRemark("城镇职工基本医疗保险政策");

        // Setup update DTO
        updateDTO = new InsurancePolicyUpdateDTO();
        updateDTO.setId("policy-123");
        updateDTO.setPolicyName("Updated Policy Name");
        updateDTO.setDeductibleLine(BigDecimal.valueOf(1000));
        updateDTO.setClassARatio(BigDecimal.valueOf(95));

        // Setup entity
        policy = new InsurancePolicy();
        policy.setId("policy-123");
        policy.setPolicyName("城镇职工医保政策");
        policy.setInsuranceType(InsurancePolicy.InsuranceTypeEnum.URBAN_EMPLOYEE);
        policy.setDeductibleLine(BigDecimal.valueOf(800));
        policy.setCapLine(BigDecimal.valueOf(50000));
        policy.setClassARatio(BigDecimal.valueOf(90));
        policy.setClassBRatio(BigDecimal.valueOf(80));
        policy.setClassCRatio(BigDecimal.valueOf(0));
        policy.setOutpatientRatio(BigDecimal.valueOf(70));
        policy.setInpatientRatio(BigDecimal.valueOf(85));
        policy.setRemark("城镇职工基本医疗保险政策");
        policy.setStatus(InsurancePolicy.InsurancePolicyStatus.ACTIVE);
        policy.setDeleted(false);
    }

    @Nested
    @DisplayName("Create Insurance Policy Tests")
    class CreateInsurancePolicyTests {

        @Test
        @DisplayName("Should create insurance policy successfully")
        void shouldCreatePolicySuccessfully() {
            // Given
            when(insurancePolicyRepository.findByInsuranceType(any(InsurancePolicy.InsuranceTypeEnum.class)))
                    .thenReturn(Optional.empty());
            when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenReturn(policy);

            // When
            InsurancePolicyVO result = insurancePolicyService.create(createDTO);

            // Then
            assertNotNull(result);
            assertEquals("城镇职工医保政策", result.getPolicyName());
            assertEquals("URBAN_EMPLOYEE", result.getInsuranceType());
            assertEquals(BigDecimal.valueOf(800), result.getDeductibleLine());
            assertEquals(BigDecimal.valueOf(50000), result.getCapLine());
            assertEquals(BigDecimal.valueOf(90), result.getClassARatio());
            assertEquals("ACTIVE", result.getStatus());

            verify(insurancePolicyRepository).findByInsuranceType(InsurancePolicy.InsuranceTypeEnum.URBAN_EMPLOYEE);
            verify(insurancePolicyRepository).save(any(InsurancePolicy.class));
        }

        @Test
        @DisplayName("Should throw exception when insurance type already configured")
        void shouldThrowExceptionWhenTypeAlreadyConfigured() {
            // Given
            when(insurancePolicyRepository.findByInsuranceType(InsurancePolicy.InsuranceTypeEnum.URBAN_EMPLOYEE))
                    .thenReturn(Optional.of(policy));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> insurancePolicyService.create(createDTO));
            assertTrue(exception.getMessage().contains("该医保类型已配置政策"));

            verify(insurancePolicyRepository).findByInsuranceType(InsurancePolicy.InsuranceTypeEnum.URBAN_EMPLOYEE);
            verify(insurancePolicyRepository, never()).save(any(InsurancePolicy.class));
        }

        @Test
        @DisplayName("Should set default status to ACTIVE")
        void shouldSetDefaultStatusToActive() {
            // Given
            when(insurancePolicyRepository.findByInsuranceType(any())).thenReturn(Optional.empty());
            when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenAnswer(invocation -> {
                InsurancePolicy saved = invocation.getArgument(0);
                saved.setId("new-id");
                return saved;
            });

            // When
            InsurancePolicyVO result = insurancePolicyService.create(createDTO);

            // Then
            ArgumentCaptor<InsurancePolicy> captor = ArgumentCaptor.forClass(InsurancePolicy.class);
            verify(insurancePolicyRepository).save(captor.capture());
            assertEquals(InsurancePolicy.InsurancePolicyStatus.ACTIVE, captor.getValue().getStatus());
        }
    }

    @Nested
    @DisplayName("Update Insurance Policy Tests")
    class UpdateInsurancePolicyTests {

        @Test
        @DisplayName("Should update insurance policy successfully")
        void shouldUpdatePolicySuccessfully() {
            // Given
            when(insurancePolicyRepository.findById("policy-123")).thenReturn(Optional.of(policy));
            when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenReturn(policy);

            // When
            InsurancePolicyVO result = insurancePolicyService.update(updateDTO);

            // Then
            assertNotNull(result);
            assertEquals("Updated Policy Name", policy.getPolicyName());
            assertEquals(BigDecimal.valueOf(1000), policy.getDeductibleLine());
            assertEquals(BigDecimal.valueOf(95), policy.getClassARatio());

            verify(insurancePolicyRepository).findById("policy-123");
            verify(insurancePolicyRepository).save(any(InsurancePolicy.class));
        }

        @Test
        @DisplayName("Should throw exception when policy not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            when(insurancePolicyRepository.findById("policy-123")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> insurancePolicyService.update(updateDTO));
            assertTrue(exception.getMessage().contains("医保政策不存在"));

            verify(insurancePolicyRepository).findById("policy-123");
            verify(insurancePolicyRepository, never()).save(any(InsurancePolicy.class));
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Given
            InsurancePolicyUpdateDTO partialUpdate = new InsurancePolicyUpdateDTO();
            partialUpdate.setId("policy-123");
            partialUpdate.setRemark("Updated remark only");

            when(insurancePolicyRepository.findById("policy-123")).thenReturn(Optional.of(policy));
            when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenReturn(policy);

            // When
            InsurancePolicyVO result = insurancePolicyService.update(partialUpdate);

            // Then
            assertNotNull(result);
            assertEquals("Updated remark only", policy.getRemark());
            // Other fields should remain unchanged
            assertEquals("城镇职工医保政策", policy.getPolicyName());
            assertEquals(BigDecimal.valueOf(800), policy.getDeductibleLine());
        }

        @Test
        @DisplayName("Should update all fields when all provided")
        void shouldUpdateAllFields() {
            // Given
            InsurancePolicyUpdateDTO fullUpdate = new InsurancePolicyUpdateDTO();
            fullUpdate.setId("policy-123");
            fullUpdate.setPolicyName("Full Update Policy");
            fullUpdate.setDeductibleLine(BigDecimal.valueOf(1200));
            fullUpdate.setCapLine(BigDecimal.valueOf(60000));
            fullUpdate.setClassARatio(BigDecimal.valueOf(92));
            fullUpdate.setClassBRatio(BigDecimal.valueOf(82));
            fullUpdate.setClassCRatio(BigDecimal.valueOf(5));
            fullUpdate.setOutpatientRatio(BigDecimal.valueOf(75));
            fullUpdate.setInpatientRatio(BigDecimal.valueOf(88));
            fullUpdate.setRemark("Fully updated");
            fullUpdate.setStatus("INACTIVE");

            when(insurancePolicyRepository.findById("policy-123")).thenReturn(Optional.of(policy));
            when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenReturn(policy);

            // When
            InsurancePolicyVO result = insurancePolicyService.update(fullUpdate);

            // Then
            assertNotNull(result);
            assertEquals("Full Update Policy", policy.getPolicyName());
            assertEquals(BigDecimal.valueOf(1200), policy.getDeductibleLine());
            assertEquals(BigDecimal.valueOf(60000), policy.getCapLine());
            assertEquals(BigDecimal.valueOf(92), policy.getClassARatio());
            assertEquals(InsurancePolicy.InsurancePolicyStatus.INACTIVE, policy.getStatus());
        }
    }

    @Nested
    @DisplayName("Delete Insurance Policy Tests")
    class DeleteInsurancePolicyTests {

        @Test
        @DisplayName("Should delete insurance policy (logical delete) successfully")
        void shouldDeletePolicySuccessfully() {
            // Given
            when(insurancePolicyRepository.findById("policy-123")).thenReturn(Optional.of(policy));
            when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenReturn(policy);

            // When
            insurancePolicyService.delete("policy-123");

            // Then
            assertTrue(policy.getDeleted());
            verify(insurancePolicyRepository).findById("policy-123");
            verify(insurancePolicyRepository).save(any(InsurancePolicy.class));
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent policy")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            // Given
            when(insurancePolicyRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> insurancePolicyService.delete("non-existent"));
            assertTrue(exception.getMessage().contains("医保政策不存在"));

            verify(insurancePolicyRepository).findById("non-existent");
            verify(insurancePolicyRepository, never()).save(any(InsurancePolicy.class));
        }
    }

    @Nested
    @DisplayName("Query Insurance Policy Tests")
    class QueryInsurancePolicyTests {

        @Test
        @DisplayName("Should get insurance policy by ID successfully")
        void shouldGetByIdSuccessfully() {
            // Given
            when(insurancePolicyRepository.findById("policy-123")).thenReturn(Optional.of(policy));

            // When
            InsurancePolicyVO result = insurancePolicyService.getById("policy-123");

            // Then
            assertNotNull(result);
            assertEquals("policy-123", result.getId());
            assertEquals("城镇职工医保政策", result.getPolicyName());
            verify(insurancePolicyRepository).findById("policy-123");
        }

        @Test
        @DisplayName("Should throw exception when getting non-existent policy by ID")
        void shouldThrowExceptionWhenGetByIdNotFound() {
            // Given
            when(insurancePolicyRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class, () -> insurancePolicyService.getById("non-existent"));
        }

        @Test
        @DisplayName("Should get insurance policy by type successfully")
        void shouldGetByInsuranceTypeSuccessfully() {
            // Given
            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(
                    InsurancePolicy.InsuranceTypeEnum.URBAN_EMPLOYEE,
                    InsurancePolicy.InsurancePolicyStatus.ACTIVE))
                    .thenReturn(Optional.of(policy));

            // When
            InsurancePolicyVO result = insurancePolicyService.getByInsuranceType("URBAN_EMPLOYEE");

            // Then
            assertNotNull(result);
            assertEquals("URBAN_EMPLOYEE", result.getInsuranceType());
            verify(insurancePolicyRepository).findByInsuranceTypeAndStatus(
                    InsurancePolicy.InsuranceTypeEnum.URBAN_EMPLOYEE,
                    InsurancePolicy.InsurancePolicyStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should throw exception when getting non-existent policy by type")
        void shouldThrowExceptionWhenGetByTypeNotFound() {
            // Given
            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(
                    any(InsurancePolicy.InsuranceTypeEnum.class),
                    any(InsurancePolicy.InsurancePolicyStatus.class)))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> insurancePolicyService.getByInsuranceType("URBAN_EMPLOYEE"));
        }

        @Test
        @DisplayName("Should return paginated list successfully")
        void shouldReturnPaginatedList() {
            // Given
            List<InsurancePolicy> policies = Arrays.asList(policy);
            Page<InsurancePolicy> page = new PageImpl<>(policies);
            when(insurancePolicyRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            // When
            PageResult<InsurancePolicyVO> result = insurancePolicyService.pageList("城镇职工", "ACTIVE", 1, 10);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("Should return empty page when no policies match criteria")
        void shouldReturnEmptyPageWhenNoMatch() {
            // Given
            Page<InsurancePolicy> emptyPage = new PageImpl<>(Collections.emptyList());
            when(insurancePolicyRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

            // When
            PageResult<InsurancePolicyVO> result = insurancePolicyService.pageList("NonExistent", "ACTIVE", 1, 10);

            // Then
            assertNotNull(result);
            assertTrue(result.getList().isEmpty());
            assertEquals(0L, result.getTotal());
        }

        @Test
        @DisplayName("Should return list of all active policies")
        void shouldReturnAllActivePolicies() {
            // Given
            List<InsurancePolicy> policies = Arrays.asList(policy);
            when(insurancePolicyRepository.findAllActive()).thenReturn(policies);

            // When
            List<InsurancePolicyVO> result = insurancePolicyService.listAllActive();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("ACTIVE", result.get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("Status Management Tests")
    class StatusManagementTests {

        @Test
        @DisplayName("Should update status to ACTIVE successfully")
        void shouldUpdateStatusToActive() {
            // Given
            policy.setStatus(InsurancePolicy.InsurancePolicyStatus.INACTIVE);
            when(insurancePolicyRepository.findById("policy-123")).thenReturn(Optional.of(policy));
            when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenReturn(policy);

            // When
            InsurancePolicyVO result = insurancePolicyService.updateStatus("policy-123", "ACTIVE");

            // Then
            assertNotNull(result);
            assertEquals("ACTIVE", result.getStatus());
            assertEquals(InsurancePolicy.InsurancePolicyStatus.ACTIVE, policy.getStatus());
        }

        @Test
        @DisplayName("Should update status to INACTIVE successfully")
        void shouldUpdateStatusToInactive() {
            // Given
            when(insurancePolicyRepository.findById("policy-123")).thenReturn(Optional.of(policy));
            when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenReturn(policy);

            // When
            InsurancePolicyVO result = insurancePolicyService.updateStatus("policy-123", "INACTIVE");

            // Then
            assertNotNull(result);
            assertEquals("INACTIVE", result.getStatus());
            assertEquals(InsurancePolicy.InsurancePolicyStatus.INACTIVE, policy.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when updating status of non-existent policy")
        void shouldThrowExceptionWhenUpdatingStatusNonExistent() {
            // Given
            when(insurancePolicyRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> insurancePolicyService.updateStatus("non-existent", "ACTIVE"));
        }
    }

    @Nested
    @DisplayName("Insurance Amount Calculation Tests")
    class InsuranceAmountCalculationTests {

        @Test
        @DisplayName("Should calculate insurance amount for Class A item")
        void shouldCalculateClassAAmount() {
            // Given
            BigDecimal amount = BigDecimal.valueOf(100);
            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(
                    InsurancePolicy.InsuranceTypeEnum.URBAN_EMPLOYEE,
                    InsurancePolicy.InsurancePolicyStatus.ACTIVE))
                    .thenReturn(Optional.of(policy));

            // When
            BigDecimal result = insurancePolicyService.calculateInsuranceAmount("URBAN_EMPLOYEE", "A", amount);

            // Then
            // 100 * 90% = 90
            assertEquals(BigDecimal.valueOf(90.00).setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
        }

        @Test
        @DisplayName("Should calculate insurance amount for Class B item")
        void shouldCalculateClassBAmount() {
            // Given
            BigDecimal amount = BigDecimal.valueOf(100);
            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(
                    InsurancePolicy.InsuranceTypeEnum.URBAN_EMPLOYEE,
                    InsurancePolicy.InsurancePolicyStatus.ACTIVE))
                    .thenReturn(Optional.of(policy));

            // When
            BigDecimal result = insurancePolicyService.calculateInsuranceAmount("URBAN_EMPLOYEE", "B", amount);

            // Then
            // 100 * 80% = 80
            assertEquals(BigDecimal.valueOf(80.00).setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP));
        }

        @Test
        @DisplayName("Should return zero for Class C/self-pay items")
        void shouldReturnZeroForClassCAndSelfPay() {
            // Given
            BigDecimal amount = BigDecimal.valueOf(100);

            // When
            BigDecimal resultC = insurancePolicyService.calculateInsuranceAmount("URBAN_EMPLOYEE", "C", amount);
            BigDecimal resultSelf = insurancePolicyService.calculateInsuranceAmount("URBAN_EMPLOYEE", "SELF", amount);

            // Then
            assertEquals(BigDecimal.ZERO, resultC);
            assertEquals(BigDecimal.ZERO, resultSelf);
        }

        @Test
        @DisplayName("Should return zero when policy not found")
        void shouldReturnZeroWhenPolicyNotFound() {
            // Given
            BigDecimal amount = BigDecimal.valueOf(100);
            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(
                    any(), any()))
                    .thenReturn(Optional.empty());

            // When
            BigDecimal result = insurancePolicyService.calculateInsuranceAmount("URBAN_EMPLOYEE", "A", amount);

            // Then
            assertEquals(BigDecimal.ZERO, result);
        }

        @Test
        @DisplayName("Should return zero when amount is null or negative")
        void shouldReturnZeroWhenAmountInvalid() {
            // When
            BigDecimal resultNull = insurancePolicyService.calculateInsuranceAmount("URBAN_EMPLOYEE", "A", null);
            BigDecimal resultZero = insurancePolicyService.calculateInsuranceAmount("URBAN_EMPLOYEE", "A", BigDecimal.ZERO);
            BigDecimal resultNegative = insurancePolicyService.calculateInsuranceAmount("URBAN_EMPLOYEE", "A", BigDecimal.valueOf(-10));

            // Then
            assertEquals(BigDecimal.ZERO, resultNull);
            assertEquals(BigDecimal.ZERO, resultZero);
            assertEquals(BigDecimal.ZERO, resultNegative);
        }

        @Test
        @DisplayName("Should return zero when ratio is null or zero")
        void shouldReturnZeroWhenRatioInvalid() {
            // Given
            policy.setClassARatio(null);
            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(any(), any()))
                    .thenReturn(Optional.of(policy));

            // When
            BigDecimal result = insurancePolicyService.calculateInsuranceAmount("URBAN_EMPLOYEE", "A", BigDecimal.valueOf(100));

            // Then
            assertEquals(BigDecimal.ZERO, result);
        }

        @Test
        @DisplayName("Should return zero for unknown insurance type")
        void shouldReturnZeroForUnknownInsuranceType() {
            // Given
            BigDecimal amount = BigDecimal.valueOf(100);
            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(any(), any()))
                    .thenReturn(Optional.of(policy));

            // When
            BigDecimal result = insurancePolicyService.calculateInsuranceAmount("URBAN_EMPLOYEE", "UNKNOWN", amount);

            // Then
            assertEquals(BigDecimal.ZERO, result);
        }
    }

    @Nested
    @DisplayName("Insurance Settlement Calculation Tests")
    class InsuranceSettlementCalculationTests {

        @Test
        @DisplayName("Should calculate settlement correctly with deductible")
        void shouldCalculateSettlementCorrectly() {
            // Given
            BigDecimal totalAmount = BigDecimal.valueOf(1000);
            BigDecimal classAAmount = BigDecimal.valueOf(500);
            BigDecimal classBAmount = BigDecimal.valueOf(300);
            BigDecimal classCAmount = BigDecimal.valueOf(200);

            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(
                    InsurancePolicy.InsuranceTypeEnum.URBAN_EMPLOYEE,
                    InsurancePolicy.InsurancePolicyStatus.ACTIVE))
                    .thenReturn(Optional.of(policy));

            // When
            InsurancePolicyService.InsuranceSettlementResult result = insurancePolicyService.calculateSettlement(
                    "URBAN_EMPLOYEE", totalAmount, classAAmount, classBAmount, classCAmount);

            // Then
            assertNotNull(result);
            // Class A reimbursement: 500 * 90% = 450
            // Class B reimbursement: 300 * 80% = 240
            // Class C reimbursement: 200 * 0% = 0
            // Total before deductible: 450 + 240 + 0 = 690
            // Deductible: 800
            // Amount above deductible: 690 - 800 = 0 (negative, so 0)
            assertEquals(BigDecimal.valueOf(800), result.deductibleLine());
            assertEquals(BigDecimal.ZERO, result.amountAboveDeductible());
            assertEquals(BigDecimal.valueOf(450.00).setScale(2, RoundingMode.HALF_UP), result.classAAmount().setScale(2, RoundingMode.HALF_UP));
            assertEquals(BigDecimal.valueOf(240.00).setScale(2, RoundingMode.HALF_UP), result.classBAmount().setScale(2, RoundingMode.HALF_UP));
            assertEquals(BigDecimal.ZERO, result.classCAmount());
            assertEquals(BigDecimal.ZERO, result.totalInsuranceAmount());
            assertEquals(BigDecimal.valueOf(1000), result.selfPayAmount());
        }

        @Test
        @DisplayName("Should calculate settlement with amount above deductible")
        void shouldCalculateSettlementWithAmountAboveDeductible() {
            // Given
            BigDecimal totalAmount = BigDecimal.valueOf(3000);
            BigDecimal classAAmount = BigDecimal.valueOf(2000);
            BigDecimal classBAmount = BigDecimal.valueOf(800);
            BigDecimal classCAmount = BigDecimal.valueOf(200);

            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(
                    InsurancePolicy.InsuranceTypeEnum.URBAN_EMPLOYEE,
                    InsurancePolicy.InsurancePolicyStatus.ACTIVE))
                    .thenReturn(Optional.of(policy));

            // When
            InsurancePolicyService.InsuranceSettlementResult result = insurancePolicyService.calculateSettlement(
                    "URBAN_EMPLOYEE", totalAmount, classAAmount, classBAmount, classCAmount);

            // Then
            // Class A reimbursement: 2000 * 90% = 1800
            // Class B reimbursement: 800 * 80% = 640
            // Total before deductible: 1800 + 640 = 2440
            // Deductible: 800
            // Amount above deductible: 2440 - 800 = 1640
            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(800), result.deductibleLine());
            assertEquals(BigDecimal.valueOf(1640).setScale(2, RoundingMode.HALF_UP), result.amountAboveDeductible().setScale(2, RoundingMode.HALF_UP));
            assertEquals(BigDecimal.valueOf(1640).setScale(2, RoundingMode.HALF_UP), result.totalInsuranceAmount().setScale(2, RoundingMode.HALF_UP));
            assertEquals(BigDecimal.valueOf(1360).setScale(2, RoundingMode.HALF_UP), result.selfPayAmount().setScale(2, RoundingMode.HALF_UP));
        }

        @Test
        @DisplayName("Should throw exception when policy not found for settlement")
        void shouldThrowExceptionWhenPolicyNotFoundForSettlement() {
            // Given
            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(any(), any()))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThrows(BusinessException.class,
                    () -> insurancePolicyService.calculateSettlement(
                            "URBAN_EMPLOYEE", BigDecimal.valueOf(1000),
                            BigDecimal.valueOf(500), BigDecimal.valueOf(300), BigDecimal.valueOf(200)));
        }

        @Test
        @DisplayName("Should handle null deductible line")
        void shouldHandleNullDeductibleLine() {
            // Given
            policy.setDeductibleLine(null);
            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(any(), any()))
                    .thenReturn(Optional.of(policy));

            // When
            InsurancePolicyService.InsuranceSettlementResult result = insurancePolicyService.calculateSettlement(
                    "URBAN_EMPLOYEE", BigDecimal.valueOf(1000),
                    BigDecimal.valueOf(500), BigDecimal.valueOf(300), BigDecimal.valueOf(200));

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.deductibleLine());
        }

        @Test
        @DisplayName("Should handle null ratios in settlement")
        void shouldHandleNullRatiosInSettlement() {
            // Given
            policy.setClassARatio(null);
            policy.setClassBRatio(null);
            when(insurancePolicyRepository.findByInsuranceTypeAndStatus(any(), any()))
                    .thenReturn(Optional.of(policy));

            // When
            InsurancePolicyService.InsuranceSettlementResult result = insurancePolicyService.calculateSettlement(
                    "URBAN_EMPLOYEE", BigDecimal.valueOf(1000),
                    BigDecimal.valueOf(500), BigDecimal.valueOf(300), BigDecimal.valueOf(200));

            // Then
            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.classAAmount());
            assertEquals(BigDecimal.ZERO, result.classBAmount());
        }
    }

    @Nested
    @DisplayName("VO Conversion Tests")
    class VOConversionTests {

        @Test
        @DisplayName("Should correctly convert entity to VO with all fields")
        void shouldConvertEntityToVOCorrectly() {
            // Given
            when(insurancePolicyRepository.findById("policy-123")).thenReturn(Optional.of(policy));

            // When
            InsurancePolicyVO vo = insurancePolicyService.getById("policy-123");

            // Then
            assertNotNull(vo);
            assertEquals("policy-123", vo.getId());
            assertEquals("城镇职工医保政策", vo.getPolicyName());
            assertEquals("URBAN_EMPLOYEE", vo.getInsuranceType());
            assertEquals("城镇职工医保", vo.getInsuranceTypeDesc());
            assertEquals(BigDecimal.valueOf(800), vo.getDeductibleLine());
            assertEquals(BigDecimal.valueOf(50000), vo.getCapLine());
            assertEquals(BigDecimal.valueOf(90), vo.getClassARatio());
            assertEquals(BigDecimal.valueOf(80), vo.getClassBRatio());
            assertEquals(BigDecimal.valueOf(0), vo.getClassCRatio());
            assertEquals(BigDecimal.valueOf(70), vo.getOutpatientRatio());
            assertEquals(BigDecimal.valueOf(85), vo.getInpatientRatio());
            assertEquals("ACTIVE", vo.getStatus());
            assertEquals("启用", vo.getStatusDesc());
        }

        @Test
        @DisplayName("Should handle null enum values in VO conversion")
        void shouldHandleNullEnumValues() {
            // Given
            policy.setInsuranceType(null);
            policy.setStatus(null);
            when(insurancePolicyRepository.findById("policy-123")).thenReturn(Optional.of(policy));

            // When
            InsurancePolicyVO vo = insurancePolicyService.getById("policy-123");

            // Then
            assertNotNull(vo);
            assertNull(vo.getInsuranceType());
            assertNull(vo.getInsuranceTypeDesc());
            assertNull(vo.getStatus());
            assertNull(vo.getStatusDesc());
        }
    }
}