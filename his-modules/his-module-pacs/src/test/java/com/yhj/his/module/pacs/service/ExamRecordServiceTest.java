package com.yhj.his.module.pacs.service;

import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.pacs.dto.ImageFileDTO;
import com.yhj.his.module.pacs.dto.ImageReceiveDTO;
import com.yhj.his.module.pacs.entity.ExamImage;
import com.yhj.his.module.pacs.entity.ExamRecord;
import com.yhj.his.module.pacs.entity.ExamSeries;
import com.yhj.his.module.pacs.repository.ExamImageRepository;
import com.yhj.his.module.pacs.repository.ExamRecordRepository;
import com.yhj.his.module.pacs.repository.ExamSeriesRepository;
import com.yhj.his.module.pacs.service.impl.ImageServiceImpl;
import com.yhj.his.module.pacs.vo.ExamImageVO;
import com.yhj.his.module.pacs.vo.ExamRecordVO;
import com.yhj.his.module.pacs.vo.ExamSeriesVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ImageService (ExamRecord) Unit Tests
 * Tests for image record management, series management, and image reception workflow
 */
@ExtendWith(MockitoExtension.class)
class ExamRecordServiceTest {

    @Mock
    private ExamRecordRepository examRecordRepository;

    @Mock
    private ExamSeriesRepository examSeriesRepository;

    @Mock
    private ExamImageRepository examImageRepository;

    @InjectMocks
    private ImageServiceImpl imageService;

    private ExamRecord examRecord;
    private ExamSeries examSeries;
    private ExamImage examImage;
    private ImageReceiveDTO imageReceiveDTO;
    private ImageFileDTO imageFileDTO;

    @BeforeEach
    void setUp() {
        // Setup ExamRecord
        examRecord = new ExamRecord();
        examRecord.setId("exam-001");
        examRecord.setExamNo("CT20260406001");
        examRecord.setRequestId("request-001");
        examRecord.setPatientId("P001");
        examRecord.setPatientName("测试患者");
        examRecord.setAccessionNo("ACC001");
        examRecord.setStudyId("STUDY001");
        examRecord.setExamType("CT");
        examRecord.setExamPart("胸部");
        examRecord.setModality("CT");
        examRecord.setEquipmentId("EQ001");
        examRecord.setEquipmentName("CT设备1");
        examRecord.setRoomNo("R001");
        examRecord.setTechnicianId("TECH001");
        examRecord.setTechnicianName("技师张");
        examRecord.setExamStatus("检查中");
        examRecord.setReportStatus("待报告");
        examRecord.setSeriesCount(0);
        examRecord.setImageCount(0);

        // Setup ExamSeries
        examSeries = new ExamSeries();
        examSeries.setId("series-001");
        examSeries.setExamId("exam-001");
        examSeries.setSeriesNo("1");
        examSeries.setSeriesUid("1.2.3.4.5.6");
        examSeries.setSeriesDescription("胸部平扫");
        examSeries.setModality("CT");
        examSeries.setBodyPart("CHEST");
        examSeries.setImageCount(100);
        examSeries.setStoragePath("/storage/series/001");
        examSeries.setScanDate(LocalDate.of(2026, 4, 6));
        examSeries.setScanTime(LocalTime.of(10, 30));
        examSeries.setKvp(new BigDecimal("120"));
        examSeries.setMas(new BigDecimal("200"));

        // Setup ExamImage
        examImage = new ExamImage();
        examImage.setId("image-001");
        examImage.setSeriesId("series-001");
        examImage.setExamId("exam-001");
        examImage.setImageNo(1);
        examImage.setImageUid("1.2.3.4.5.6.7");
        examImage.setSopUid("1.2.3.4.5.6.7.8");
        examImage.setImagePath("/storage/images/CT/2026/04/06/image001.dcm");
        examImage.setThumbnailPath("/storage/thumbnails/CT/2026/04/06/thumb001.jpg");
        examImage.setImageWidth(512);
        examImage.setImageHeight(512);
        examImage.setBitsAllocated(16);
        examImage.setBitsStored(12);
        examImage.setWindowCenter(new BigDecimal("0"));
        examImage.setWindowWidth(new BigDecimal("350"));
        examImage.setIsKeyImage(false);
        examImage.setFileSize(512000L);
        examImage.setFileFormat("DICOM");

        // Setup ImageReceiveDTO
        imageReceiveDTO = new ImageReceiveDTO();
        imageReceiveDTO.setExamId("exam-001");
        imageReceiveDTO.setSeriesNo("2");
        imageReceiveDTO.setSeriesUid("1.2.3.4.5.7");
        imageReceiveDTO.setSeriesDescription("胸部增强");
        imageReceiveDTO.setModality("CT");
        imageReceiveDTO.setBodyPart("CHEST");
        imageReceiveDTO.setImageCount(50);
        imageReceiveDTO.setStoragePath("/storage/series/002");
        imageReceiveDTO.setScanDate("2026-04-06");
        imageReceiveDTO.setScanTime("11:00");
        imageReceiveDTO.setKvp("120");
        imageReceiveDTO.setMas("250");
        imageReceiveDTO.setSliceThickness("5");
        imageReceiveDTO.setPixelSpacing("0.5");

        // Setup ImageFileDTO
        imageFileDTO = new ImageFileDTO();
        imageFileDTO.setSeriesId("series-001");
        imageFileDTO.setExamId("exam-001");
        imageFileDTO.setImageNo(2);
        imageFileDTO.setImageUid("1.2.3.4.5.6.8");
        imageFileDTO.setSopUid("1.2.3.4.5.6.8.9");
        imageFileDTO.setImagePath("/storage/images/CT/2026/04/06/image002.dcm");
        imageFileDTO.setThumbnailPath("/storage/thumbnails/CT/2026/04/06/thumb002.jpg");
        imageFileDTO.setImageWidth(512);
        imageFileDTO.setImageHeight(512);
        imageFileDTO.setBitsAllocated(16);
        imageFileDTO.setBitsStored(12);
        imageFileDTO.setWindowCenter(new BigDecimal("50"));
        imageFileDTO.setWindowWidth(new BigDecimal("400"));
        imageFileDTO.setIsKeyImage(true);
        imageFileDTO.setFileSize(524288L);
        imageFileDTO.setFileFormat("DICOM");
    }

    @Nested
    @DisplayName("Receive Series Tests")
    class ReceiveSeriesTests {

        @Test
        @DisplayName("Should receive series successfully")
        void receiveSeries_Success() {
            // Arrange
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examSeriesRepository.save(any(ExamSeries.class))).thenAnswer(invocation -> {
                ExamSeries saved = invocation.getArgument(0);
                saved.setId("new-series-id");
                return saved;
            });
            when(examSeriesRepository.countByExamId(anyString())).thenReturn(2);
            when(examRecordRepository.save(any(ExamRecord.class))).thenReturn(examRecord);

            // Act
            ExamSeriesVO result = imageService.receiveSeries(imageReceiveDTO);

            // Assert
            assertNotNull(result);
            assertEquals("new-series-id", result.getId());
            assertEquals("exam-001", result.getExamId());
            assertEquals("2", result.getSeriesNo());
            assertEquals("胸部增强", result.getSeriesDescription());
            assertEquals("CT", result.getModality());
            assertEquals("CHEST", result.getBodyPart());
            assertEquals(50, result.getImageCount());
            assertEquals(LocalDate.of(2026, 4, 6), result.getScanDate());
            assertEquals(LocalTime.of(11, 0), result.getScanTime());
            assertEquals(new BigDecimal("120"), result.getKvp());
            assertEquals(new BigDecimal("250"), result.getMas());

            verify(examRecordRepository).findById("exam-001");
            verify(examSeriesRepository).save(any(ExamSeries.class));
            verify(examSeriesRepository).countByExamId("exam-001");
            verify(examRecordRepository).save(any(ExamRecord.class));
        }

        @Test
        @DisplayName("Should throw exception when exam record not found")
        void receiveSeries_ExamNotFound() {
            // Arrange
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> imageService.receiveSeries(imageReceiveDTO));

            assertEquals("检查记录不存在", exception.getMessage());
            verify(examSeriesRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle null optional fields in series")
        void receiveSeries_NullOptionalFields() {
            // Arrange
            imageReceiveDTO.setScanDate(null);
            imageReceiveDTO.setScanTime(null);
            imageReceiveDTO.setKvp(null);
            imageReceiveDTO.setMas(null);
            imageReceiveDTO.setSliceThickness(null);
            imageReceiveDTO.setImageCount(null);

            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examSeriesRepository.save(any(ExamSeries.class))).thenAnswer(invocation -> {
                ExamSeries saved = invocation.getArgument(0);
                saved.setId("new-series-id");
                assertEquals(0, saved.getImageCount()); // Default value
                return saved;
            });
            when(examSeriesRepository.countByExamId(anyString())).thenReturn(1);
            when(examRecordRepository.save(any(ExamRecord.class))).thenReturn(examRecord);

            // Act
            ExamSeriesVO result = imageService.receiveSeries(imageReceiveDTO);

            // Assert
            assertNotNull(result);
            assertNull(result.getScanDate());
            assertNull(result.getScanTime());
            assertNull(result.getKvp());
            assertNull(result.getMas());
        }
    }

    @Nested
    @DisplayName("Receive Image Tests")
    class ReceiveImageTests {

        @Test
        @DisplayName("Should receive single image successfully")
        void receiveImage_Success() {
            // Arrange
            when(examSeriesRepository.findById(anyString())).thenReturn(Optional.of(examSeries));
            when(examImageRepository.save(any(ExamImage.class))).thenAnswer(invocation -> {
                ExamImage saved = invocation.getArgument(0);
                saved.setId("new-image-id");
                return saved;
            });
            when(examImageRepository.countBySeriesId(anyString())).thenReturn(101);
            when(examSeriesRepository.save(any(ExamSeries.class))).thenReturn(examSeries);
            when(examImageRepository.countByExamId(anyString())).thenReturn(101);
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examRecordRepository.save(any(ExamRecord.class))).thenReturn(examRecord);

            // Act
            ExamImageVO result = imageService.receiveImage(imageFileDTO);

            // Assert
            assertNotNull(result);
            assertEquals("new-image-id", result.getId());
            assertEquals("series-001", result.getSeriesId());
            assertEquals("exam-001", result.getExamId());
            assertEquals(2, result.getImageNo());
            assertEquals("/storage/images/CT/2026/04/06/image002.dcm", result.getImagePath());
            assertEquals(512, result.getImageWidth());
            assertEquals(512, result.getImageHeight());
            assertTrue(result.getIsKeyImage());

            verify(examSeriesRepository).findById("series-001");
            verify(examImageRepository).save(any(ExamImage.class));
        }

        @Test
        @DisplayName("Should throw exception when series not found")
        void receiveImage_SeriesNotFound() {
            // Arrange
            when(examSeriesRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> imageService.receiveImage(imageFileDTO));

            assertEquals("序列不存在", exception.getMessage());
            verify(examImageRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should batch receive images successfully")
        void receiveImages_BatchSuccess() {
            // Arrange
            ImageFileDTO image2 = new ImageFileDTO();
            image2.setSeriesId("series-001");
            image2.setExamId("exam-001");
            image2.setImageNo(3);

            List<ImageFileDTO> dtoList = List.of(imageFileDTO, image2);

            when(examSeriesRepository.findById(anyString())).thenReturn(Optional.of(examSeries));
            when(examImageRepository.save(any(ExamImage.class))).thenAnswer(invocation -> {
                ExamImage saved = invocation.getArgument(0);
                saved.setId("image-" + saved.getImageNo());
                return saved;
            });
            when(examImageRepository.countBySeriesId(anyString())).thenReturn(102);
            when(examSeriesRepository.save(any(ExamSeries.class))).thenReturn(examSeries);
            when(examImageRepository.countByExamId(anyString())).thenReturn(102);
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examRecordRepository.save(any(ExamRecord.class))).thenReturn(examRecord);

            // Act
            List<ExamImageVO> results = imageService.receiveImages(dtoList);

            // Assert
            assertNotNull(results);
            assertEquals(2, results.size());
            verify(examImageRepository, times(2)).save(any(ExamImage.class));
        }
    }

    @Nested
    @DisplayName("Get Series Tests")
    class GetSeriesTests {

        @Test
        @DisplayName("Should get series by exam ID successfully")
        void getSeriesByExamId_Success() {
            // Arrange
            when(examSeriesRepository.findByExamId(anyString())).thenReturn(List.of(examSeries));

            // Act
            List<ExamSeriesVO> results = imageService.getSeriesByExamId("exam-001");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("series-001", results.get(0).getId());
            assertEquals("胸部平扫", results.get(0).getSeriesDescription());
        }

        @Test
        @DisplayName("Should return empty list when no series found")
        void getSeriesByExamId_EmptyList() {
            // Arrange
            when(examSeriesRepository.findByExamId(anyString())).thenReturn(Collections.emptyList());

            // Act
            List<ExamSeriesVO> results = imageService.getSeriesByExamId("exam-002");

            // Assert
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Should get series by ID with images")
        void getSeriesById_Success() {
            // Arrange
            when(examSeriesRepository.findById(anyString())).thenReturn(Optional.of(examSeries));
            when(examImageRepository.findBySeriesId(anyString())).thenReturn(List.of(examImage));

            // Act
            ExamSeriesVO result = imageService.getSeriesById("series-001");

            // Assert
            assertNotNull(result);
            assertEquals("series-001", result.getId());
            assertEquals("胸部平扫", result.getSeriesDescription());
            assertNotNull(result.getImageList());
            assertEquals(1, result.getImageList().size());
        }

        @Test
        @DisplayName("Should throw exception when series not found")
        void getSeriesById_NotFound() {
            // Arrange
            when(examSeriesRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> imageService.getSeriesById("non-existent-id"));

            assertEquals("序列不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Images Tests")
    class GetImagesTests {

        @Test
        @DisplayName("Should get images by series ID successfully")
        void getImagesBySeriesId_Success() {
            // Arrange
            when(examImageRepository.findBySeriesId(anyString())).thenReturn(List.of(examImage));

            // Act
            List<ExamImageVO> results = imageService.getImagesBySeriesId("series-001");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("image-001", results.get(0).getId());
            assertEquals("/storage/images/CT/2026/04/06/image001.dcm", results.get(0).getImagePath());
        }

        @Test
        @DisplayName("Should return empty list when no images found by series ID")
        void getImagesBySeriesId_EmptyList() {
            // Arrange
            when(examImageRepository.findBySeriesId(anyString())).thenReturn(Collections.emptyList());

            // Act
            List<ExamImageVO> results = imageService.getImagesBySeriesId("series-002");

            // Assert
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Should get images by exam ID successfully")
        void getImagesByExamId_Success() {
            // Arrange
            when(examImageRepository.findByExamId(anyString())).thenReturn(List.of(examImage));

            // Act
            List<ExamImageVO> results = imageService.getImagesByExamId("exam-001");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("exam-001", results.get(0).getExamId());
        }

        @Test
        @DisplayName("Should get image by ID successfully")
        void getImageById_Success() {
            // Arrange
            when(examImageRepository.findById(anyString())).thenReturn(Optional.of(examImage));

            // Act
            ExamImageVO result = imageService.getImageById("image-001");

            // Assert
            assertNotNull(result);
            assertEquals("image-001", result.getId());
            assertEquals("1.2.3.4.5.6.7", result.getImageUid());
            assertEquals("1.2.3.4.5.6.7.8", result.getSopUid());
        }

        @Test
        @DisplayName("Should throw exception when image not found")
        void getImageById_NotFound() {
            // Arrange
            when(examImageRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> imageService.getImageById("non-existent-id"));

            assertEquals("影像不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Key Image Tests")
    class KeyImageTests {

        @Test
        @DisplayName("Should set key image successfully")
        void setKeyImage_MarkAsKey() {
            // Arrange
            examImage.setIsKeyImage(false);
            when(examImageRepository.findById(anyString())).thenReturn(Optional.of(examImage));
            when(examImageRepository.save(any(ExamImage.class))).thenAnswer(invocation -> {
                ExamImage saved = invocation.getArgument(0);
                return saved;
            });

            // Act
            ExamImageVO result = imageService.setKeyImage("image-001", true);

            // Assert
            assertNotNull(result);
            assertTrue(result.getIsKeyImage());
            verify(examImageRepository).save(any(ExamImage.class));
        }

        @Test
        @DisplayName("Should unset key image successfully")
        void setKeyImage_Unmark() {
            // Arrange
            examImage.setIsKeyImage(true);
            when(examImageRepository.findById(anyString())).thenReturn(Optional.of(examImage));
            when(examImageRepository.save(any(ExamImage.class))).thenAnswer(invocation -> {
                ExamImage saved = invocation.getArgument(0);
                return saved;
            });

            // Act
            ExamImageVO result = imageService.setKeyImage("image-001", false);

            // Assert
            assertNotNull(result);
            assertFalse(result.getIsKeyImage());
        }

        @Test
        @DisplayName("Should throw exception when image not found for key image setting")
        void setKeyImage_NotFound() {
            // Arrange
            when(examImageRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> imageService.setKeyImage("non-existent-id", true));

            assertEquals("影像不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should get key images by exam ID")
        void getKeyImagesByExamId_Success() {
            // Arrange
            examImage.setIsKeyImage(true);
            when(examImageRepository.findKeyImagesByExamId(anyString())).thenReturn(List.of(examImage));

            // Act
            List<ExamImageVO> results = imageService.getKeyImagesByExamId("exam-001");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertTrue(results.get(0).getIsKeyImage());
        }
    }

    @Nested
    @DisplayName("Exam Image Stats Tests")
    class ExamImageStatsTests {

        @Test
        @DisplayName("Should update exam image stats successfully")
        void updateExamImageStats_Success() {
            // Arrange
            when(examImageRepository.countByExamId(anyString())).thenReturn(150);
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examRecordRepository.save(any(ExamRecord.class))).thenAnswer(invocation -> {
                ExamRecord saved = invocation.getArgument(0);
                assertEquals(150, saved.getImageCount());
                return saved;
            });

            // Act
            imageService.updateExamImageStats("exam-001");

            // Assert
            verify(examImageRepository).countByExamId("exam-001");
            verify(examRecordRepository).save(any(ExamRecord.class));
        }

        @Test
        @DisplayName("Should handle missing exam record for stats update")
        void updateExamImageStats_RecordNotFound() {
            // Arrange
            when(examImageRepository.countByExamId(anyString())).thenReturn(100);
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act
            imageService.updateExamImageStats("exam-002");

            // Assert - should not throw exception, just skip
            verify(examRecordRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Exam Detail Tests")
    class ExamDetailTests {

        @Test
        @DisplayName("Should get exam detail with series and images")
        void getExamDetail_Success() {
            // Arrange
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examSeriesRepository.findByExamId(anyString())).thenReturn(List.of(examSeries));
            when(examImageRepository.findBySeriesId(anyString())).thenReturn(List.of(examImage));

            // Act
            ExamRecordVO result = imageService.getExamDetail("exam-001");

            // Assert
            assertNotNull(result);
            assertEquals("exam-001", result.getId());
            assertEquals("CT20260406001", result.getExamNo());
            assertEquals("P001", result.getPatientId());
            assertEquals("测试患者", result.getPatientName());
            assertEquals("CT", result.getExamType());
            assertNotNull(result.getSeriesList());
            assertEquals(1, result.getSeriesList().size());
            assertNotNull(result.getSeriesList().get(0).getImageList());
            assertEquals(1, result.getSeriesList().get(0).getImageList().size());
        }

        @Test
        @DisplayName("Should throw exception when exam not found for detail")
        void getExamDetail_NotFound() {
            // Arrange
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> imageService.getExamDetail("non-existent-id"));

            assertEquals("检查记录不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should get exam detail with empty series list")
        void getExamDetail_EmptySeriesList() {
            // Arrange
            when(examRecordRepository.findById(anyString())).thenReturn(Optional.of(examRecord));
            when(examSeriesRepository.findByExamId(anyString())).thenReturn(Collections.emptyList());

            // Act
            ExamRecordVO result = imageService.getExamDetail("exam-001");

            // Assert
            assertNotNull(result);
            assertNotNull(result.getSeriesList());
            assertTrue(result.getSeriesList().isEmpty());
        }
    }
}