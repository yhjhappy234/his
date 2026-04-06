package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.pharmacy.dto.DrugCreateDTO;
import com.yhj.his.module.pharmacy.dto.DrugQueryDTO;
import com.yhj.his.module.pharmacy.dto.DrugUpdateDTO;
import com.yhj.his.module.pharmacy.entity.Drug;
import com.yhj.his.module.pharmacy.enums.DrugCategory;
import com.yhj.his.module.pharmacy.enums.DrugStatus;
import com.yhj.his.module.pharmacy.repository.DrugRepository;
import com.yhj.his.module.pharmacy.service.impl.DrugServiceImpl;
import com.yhj.his.module.pharmacy.vo.DrugVO;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DrugService Unit Tests
 * Covers Drug CRUD operations and business logic
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DrugService Unit Tests")
class DrugServiceTest {

    @Mock
    private DrugRepository drugRepository;

    @InjectMocks
    private DrugServiceImpl drugService;

    private Drug testDrug;
    private DrugCreateDTO createDTO;
    private DrugUpdateDTO updateDTO;
    private DrugQueryDTO queryDTO;

    @BeforeEach
    void setUp() {
        testDrug = createTestDrug();
        createDTO = createTestCreateDTO();
        updateDTO = createTestUpdateDTO();
        queryDTO = createTestQueryDTO();
    }

    private Drug createTestDrug() {
        Drug drug = new Drug();
        drug.setId("drug-001");
        drug.setDrugCode("DRUG001");
        drug.setDrugName("Aspirin");
        drug.setGenericName("Acetylsalicylic acid");
        drug.setTradeName("Bayer Aspirin");
        drug.setPinyinCode("ASP");
        drug.setCustomCode("C001");
        drug.setDrugCategory(DrugCategory.WESTERN);
        drug.setDrugForm("Tablet");
        drug.setDrugSpec("100mg");
        drug.setDrugUnit("tablet");
        drug.setPackageUnit("box");
        drug.setPackageQuantity(30);
        drug.setManufacturer("Bayer");
        drug.setOrigin("Germany");
        drug.setApprovalNo("HN123456");
        drug.setPurchasePrice(new BigDecimal("5.00"));
        drug.setRetailPrice(new BigDecimal("10.00"));
        drug.setIsPrescription(false);
        drug.setIsOtc(true);
        drug.setIsEssential(true);
        drug.setIsInsurance(true);
        drug.setInsuranceCode("INS001");
        drug.setInsuranceType("甲类");
        drug.setStorageCondition("Room temperature");
        drug.setShelfLife(24);
        drug.setAlertDays(180);
        drug.setMinStock(new BigDecimal("100"));
        drug.setMaxStock(new BigDecimal("500"));
        drug.setStatus(DrugStatus.NORMAL);
        drug.setDeleted(false);
        drug.setCreateTime(LocalDateTime.now());
        drug.setUpdateTime(LocalDateTime.now());
        return drug;
    }

    private DrugCreateDTO createTestCreateDTO() {
        DrugCreateDTO dto = new DrugCreateDTO();
        dto.setDrugCode("DRUG001");
        dto.setDrugName("Aspirin");
        dto.setGenericName("Acetylsalicylic acid");
        dto.setTradeName("Bayer Aspirin");
        dto.setDrugCategory(DrugCategory.WESTERN);
        dto.setDrugForm("Tablet");
        dto.setDrugSpec("100mg");
        dto.setDrugUnit("tablet");
        dto.setRetailPrice(new BigDecimal("10.00"));
        dto.setIsPrescription(false);
        dto.setIsOtc(true);
        dto.setStatus(DrugStatus.NORMAL);
        return dto;
    }

    private DrugUpdateDTO createTestUpdateDTO() {
        DrugUpdateDTO dto = new DrugUpdateDTO();
        dto.setDrugName("Aspirin Updated");
        dto.setRetailPrice(new BigDecimal("12.00"));
        dto.setIsPrescription(true);
        return dto;
    }

    private DrugQueryDTO createTestQueryDTO() {
        DrugQueryDTO dto = new DrugQueryDTO();
        dto.setKeyword("Aspirin");
        dto.setDrugCategory(DrugCategory.WESTERN.name());
        dto.setPageNum(1);
        dto.setPageSize(10);
        return dto;
    }

    @Nested
    @DisplayName("Create Drug Tests")
    class CreateDrugTests {

        @Test
        @DisplayName("Should create drug successfully when drug code does not exist")
        void shouldCreateDrugSuccessfully() {
            // Arrange
            when(drugRepository.existsByDrugCode(anyString())).thenReturn(false);
            when(drugRepository.save(any(Drug.class))).thenReturn(testDrug);

            // Act
            Result<DrugVO> result = drugService.createDrug(createDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertEquals("成功", result.getMessage());
            assertNotNull(result.getData());
            assertEquals("drug-001", result.getData().getDrugId());
            assertEquals("DRUG001", result.getData().getDrugCode());
            assertEquals("Aspirin", result.getData().getDrugName());

            verify(drugRepository).existsByDrugCode("DRUG001");
            verify(drugRepository).save(any(Drug.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when drug code already exists")
        void shouldThrowExceptionWhenDrugCodeExists() {
            // Arrange
            when(drugRepository.existsByDrugCode(anyString())).thenReturn(true);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> drugService.createDrug(createDTO));
            assertTrue(exception.getMessage().contains("药品编码已存在"));

            verify(drugRepository).existsByDrugCode("DRUG001");
            verify(drugRepository, never()).save(any(Drug.class));
        }

        @Test
        @DisplayName("Should generate pinyin code when not provided")
        void shouldGeneratePinyinCodeWhenNotProvided() {
            // Arrange
            createDTO.setPinyinCode(null);
            when(drugRepository.existsByDrugCode(anyString())).thenReturn(false);
            when(drugRepository.save(any(Drug.class))).thenReturn(testDrug);

            // Act
            Result<DrugVO> result = drugService.createDrug(createDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            verify(drugRepository).save(any(Drug.class));
        }
    }

    @Nested
    @DisplayName("Update Drug Tests")
    class UpdateDrugTests {

        @Test
        @DisplayName("Should update drug successfully when drug exists")
        void shouldUpdateDrugSuccessfully() {
            // Arrange
            when(drugRepository.findById(anyString())).thenReturn(Optional.of(testDrug));
            when(drugRepository.save(any(Drug.class))).thenReturn(testDrug);

            // Act
            Result<DrugVO> result = drugService.updateDrug("drug-001", updateDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(drugRepository).findById("drug-001");
            verify(drugRepository).save(any(Drug.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when drug not found")
        void shouldThrowExceptionWhenDrugNotFound() {
            // Arrange
            when(drugRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> drugService.updateDrug("non-existent", updateDTO));
            assertTrue(exception.getMessage().contains("药品不存在"));

            verify(drugRepository).findById("non-existent");
            verify(drugRepository, never()).save(any(Drug.class));
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Arrange
            DrugUpdateDTO partialUpdate = new DrugUpdateDTO();
            partialUpdate.setRetailPrice(new BigDecimal("15.00"));

            when(drugRepository.findById(anyString())).thenReturn(Optional.of(testDrug));
            when(drugRepository.save(any(Drug.class))).thenReturn(testDrug);

            // Act
            Result<DrugVO> result = drugService.updateDrug("drug-001", partialUpdate);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            verify(drugRepository).save(any(Drug.class));
        }
    }

    @Nested
    @DisplayName("Delete Drug Tests")
    class DeleteDrugTests {

        @Test
        @DisplayName("Should soft delete drug successfully when drug exists")
        void shouldDeleteDrugSuccessfully() {
            // Arrange
            when(drugRepository.findById(anyString())).thenReturn(Optional.of(testDrug));
            when(drugRepository.save(any(Drug.class))).thenReturn(testDrug);

            // Act
            Result<Void> result = drugService.deleteDrug("drug-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(drugRepository).findById("drug-001");
            verify(drugRepository).save(any(Drug.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when drug not found for deletion")
        void shouldThrowExceptionWhenDrugNotFoundForDeletion() {
            // Arrange
            when(drugRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> drugService.deleteDrug("non-existent"));
            assertTrue(exception.getMessage().contains("药品不存在"));

            verify(drugRepository).findById("non-existent");
            verify(drugRepository, never()).save(any(Drug.class));
        }
    }

    @Nested
    @DisplayName("Get Drug Tests")
    class GetDrugTests {

        @Test
        @DisplayName("Should get drug by ID successfully when drug exists")
        void shouldGetDrugByIdSuccessfully() {
            // Arrange
            when(drugRepository.findById(anyString())).thenReturn(Optional.of(testDrug));

            // Act
            Result<DrugVO> result = drugService.getDrugById("drug-001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("drug-001", result.getData().getDrugId());

            verify(drugRepository).findById("drug-001");
        }

        @Test
        @DisplayName("Should throw BusinessException when drug not found by ID")
        void shouldThrowExceptionWhenDrugNotFoundById() {
            // Arrange
            when(drugRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> drugService.getDrugById("non-existent"));
            assertTrue(exception.getMessage().contains("药品不存在"));

            verify(drugRepository).findById("non-existent");
        }

        @Test
        @DisplayName("Should get drug by code successfully when drug exists")
        void shouldGetDrugByCodeSuccessfully() {
            // Arrange
            when(drugRepository.findByDrugCode(anyString())).thenReturn(Optional.of(testDrug));

            // Act
            Result<DrugVO> result = drugService.getDrugByCode("DRUG001");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals("DRUG001", result.getData().getDrugCode());

            verify(drugRepository).findByDrugCode("DRUG001");
        }

        @Test
        @DisplayName("Should throw BusinessException when drug not found by code")
        void shouldThrowExceptionWhenDrugNotFoundByCode() {
            // Arrange
            when(drugRepository.findByDrugCode(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> drugService.getDrugByCode("INVALID"));
            assertTrue(exception.getMessage().contains("药品不存在"));

            verify(drugRepository).findByDrugCode("INVALID");
        }
    }

    @Nested
    @DisplayName("Query Drugs Tests")
    class QueryDrugsTests {

        @Test
        @DisplayName("Should query drugs with pagination successfully")
        void shouldQueryDrugsSuccessfully() {
            // Arrange
            List<Drug> drugs = Arrays.asList(testDrug);
            Page<Drug> page = new PageImpl<>(drugs);
            when(drugRepository.queryDrugs(anyString(), any(DrugCategory.class), any(),
                    any(), any(), any(), any(), any(DrugStatus.class),
                    any(Pageable.class))).thenReturn(page);

            // Act
            Result<PageResult<DrugVO>> result = drugService.queryDrugs(queryDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().getTotal());
            assertEquals(1, result.getData().getList().size());

            verify(drugRepository).queryDrugs(anyString(), any(DrugCategory.class), any(),
                    any(), any(), any(), any(), any(DrugStatus.class),
                    any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when no drugs found")
        void shouldReturnEmptyPageWhenNoDrugsFound() {
            // Arrange
            Page<Drug> emptyPage = new PageImpl<>(Collections.emptyList());
            when(drugRepository.queryDrugs(anyString(), any(DrugCategory.class), any(),
                    any(), any(), any(), any(), any(DrugStatus.class),
                    any(Pageable.class))).thenReturn(emptyPage);

            // Act
            Result<PageResult<DrugVO>> result = drugService.queryDrugs(queryDTO);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(0, result.getData().getTotal());
            assertTrue(result.getData().getList().isEmpty());
        }
    }

    @Nested
    @DisplayName("Search Drugs Tests")
    class SearchDrugsTests {

        @Test
        @DisplayName("Should search drugs by name successfully")
        void shouldSearchDrugsByNameSuccessfully() {
            // Arrange
            when(drugRepository.findByDrugNameContaining(anyString()))
                    .thenReturn(Arrays.asList(testDrug));

            // Act
            Result<List<DrugVO>> result = drugService.searchDrugs("Aspirin");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(drugRepository).findByDrugNameContaining("Aspirin");
        }

        @Test
        @DisplayName("Should search drugs by pinyin code when name search returns empty")
        void shouldSearchDrugsByPinyinCode() {
            // Arrange
            when(drugRepository.findByDrugNameContaining(anyString()))
                    .thenReturn(Collections.emptyList());
            when(drugRepository.findByPinyinCodeContaining(anyString()))
                    .thenReturn(Arrays.asList(testDrug));

            // Act
            Result<List<DrugVO>> result = drugService.searchDrugs("ASP");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(drugRepository).findByDrugNameContaining("ASP");
            verify(drugRepository).findByPinyinCodeContaining("ASP");
        }

        @Test
        @DisplayName("Should return empty list when no drugs found by search")
        void shouldReturnEmptyListWhenNoDrugsFoundBySearch() {
            // Arrange
            when(drugRepository.findByDrugNameContaining(anyString()))
                    .thenReturn(Collections.emptyList());
            when(drugRepository.findByPinyinCodeContaining(anyString()))
                    .thenReturn(Collections.emptyList());

            // Act
            Result<List<DrugVO>> result = drugService.searchDrugs("NONEXISTENT");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Drugs By Category Tests")
    class GetDrugsByCategoryTests {

        @Test
        @DisplayName("Should get drug categories successfully")
        void shouldGetDrugCategoriesSuccessfully() {
            // Act
            Result<List<String>> result = drugService.getDrugCategories();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(4, result.getData().size());
            assertTrue(result.getData().contains("西药"));
            assertTrue(result.getData().contains("中成药"));
        }

        @Test
        @DisplayName("Should get drugs by category successfully")
        void shouldGetDrugsByCategorySuccessfully() {
            // Arrange
            when(drugRepository.findByDrugCategoryAndStatus(any(DrugCategory.class),
                    any(DrugStatus.class))).thenReturn(Arrays.asList(testDrug));

            // Act
            Result<List<DrugVO>> result = drugService.getDrugsByCategory("WESTERN");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(drugRepository).findByDrugCategoryAndStatus(DrugCategory.WESTERN, DrugStatus.NORMAL);
        }
    }

    @Nested
    @DisplayName("Get Special Drug Types Tests")
    class GetSpecialDrugTypesTests {

        @Test
        @DisplayName("Should get prescription drugs successfully")
        void shouldGetPrescriptionDrugsSuccessfully() {
            // Arrange
            when(drugRepository.findPrescriptionDrugs()).thenReturn(Arrays.asList(testDrug));

            // Act
            Result<List<DrugVO>> result = drugService.getPrescriptionDrugs();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(drugRepository).findPrescriptionDrugs();
        }

        @Test
        @DisplayName("Should get OTC drugs successfully")
        void shouldGetOtcDrugsSuccessfully() {
            // Arrange
            when(drugRepository.findOtcDrugs()).thenReturn(Arrays.asList(testDrug));

            // Act
            Result<List<DrugVO>> result = drugService.getOtcDrugs();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(drugRepository).findOtcDrugs();
        }

        @Test
        @DisplayName("Should get insurance drugs successfully")
        void shouldGetInsuranceDrugsSuccessfully() {
            // Arrange
            when(drugRepository.findInsuranceDrugs()).thenReturn(Arrays.asList(testDrug));

            // Act
            Result<List<DrugVO>> result = drugService.getInsuranceDrugs();

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());
            assertNotNull(result.getData());

            verify(drugRepository).findInsuranceDrugs();
        }
    }

    @Nested
    @DisplayName("Toggle Drug Status Tests")
    class ToggleDrugStatusTests {

        @Test
        @DisplayName("Should toggle drug status successfully")
        void shouldToggleDrugStatusSuccessfully() {
            // Arrange
            when(drugRepository.findById(anyString())).thenReturn(Optional.of(testDrug));
            when(drugRepository.save(any(Drug.class))).thenReturn(testDrug);

            // Act
            Result<Void> result = drugService.toggleDrugStatus("drug-001", "DISABLED");

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getCode());

            verify(drugRepository).findById("drug-001");
            verify(drugRepository).save(any(Drug.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when drug not found for status toggle")
        void shouldThrowExceptionWhenDrugNotFoundForStatusToggle() {
            // Arrange
            when(drugRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> drugService.toggleDrugStatus("non-existent", "DISABLED"));
            assertTrue(exception.getMessage().contains("药品不存在"));

            verify(drugRepository).findById("non-existent");
            verify(drugRepository, never()).save(any(Drug.class));
        }
    }
}