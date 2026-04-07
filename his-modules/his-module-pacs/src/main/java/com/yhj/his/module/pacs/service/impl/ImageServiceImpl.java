package com.yhj.his.module.pacs.service.impl;

import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.entity.*;
import com.yhj.his.module.pacs.repository.*;
import com.yhj.his.module.pacs.service.*;
import com.yhj.his.module.pacs.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 影像采集服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ExamRecordRepository examRecordRepository;
    private final ExamSeriesRepository examSeriesRepository;
    private final ExamImageRepository examImageRepository;

    @Override
    @Transactional
    public ExamSeriesVO receiveSeries(ImageReceiveDTO dto) {
        ExamRecord record = examRecordRepository.findById(dto.getExamId())
                .orElseThrow(() -> new BusinessException("检查记录不存在"));

        ExamSeries series = new ExamSeries();
        series.setExamId(dto.getExamId());
        series.setSeriesNo(dto.getSeriesNo());
        series.setSeriesUid(dto.getSeriesUid());
        series.setSeriesDescription(dto.getSeriesDescription());
        series.setModality(dto.getModality());
        series.setBodyPart(dto.getBodyPart());
        series.setImageCount(dto.getImageCount() != null ? dto.getImageCount() : 0);
        series.setStoragePath(dto.getStoragePath());
        if (dto.getScanDate() != null) {
            series.setScanDate(LocalDate.parse(dto.getScanDate()));
        }
        if (dto.getScanTime() != null) {
            series.setScanTime(LocalTime.parse(dto.getScanTime()));
        }
        if (dto.getKvp() != null) {
            series.setKvp(new BigDecimal(dto.getKvp()));
        }
        if (dto.getMas() != null) {
            series.setMas(new BigDecimal(dto.getMas()));
        }
        if (dto.getSliceThickness() != null) {
            series.setSliceThickness(new BigDecimal(dto.getSliceThickness()));
        }
        series.setPixelSpacing(dto.getPixelSpacing());

        series = examSeriesRepository.save(series);

        // 更新检查记录的序列数
        Integer seriesCount = examSeriesRepository.countByExamId(dto.getExamId());
        record.setSeriesCount(seriesCount);
        examRecordRepository.save(record);

        log.info("接收影像序列成功: examId={}, seriesNo={}", dto.getExamId(), dto.getSeriesNo());

        return convertSeriesToVO(series);
    }

    @Override
    @Transactional
    public ExamImageVO receiveImage(ImageFileDTO dto) {
        ExamSeries series = examSeriesRepository.findById(dto.getSeriesId())
                .orElseThrow(() -> new BusinessException("序列不存在"));

        ExamImage image = new ExamImage();
        image.setSeriesId(dto.getSeriesId());
        image.setExamId(dto.getExamId());
        image.setImageNo(dto.getImageNo());
        image.setImageUid(dto.getImageUid());
        image.setSopUid(dto.getSopUid());
        image.setImagePath(dto.getImagePath());
        image.setThumbnailPath(dto.getThumbnailPath());
        image.setImageWidth(dto.getImageWidth());
        image.setImageHeight(dto.getImageHeight());
        image.setBitsAllocated(dto.getBitsAllocated());
        image.setBitsStored(dto.getBitsStored());
        image.setWindowCenter(dto.getWindowCenter());
        image.setWindowWidth(dto.getWindowWidth());
        image.setIsKeyImage(dto.getIsKeyImage());
        image.setFileSize(dto.getFileSize());
        image.setFileFormat(dto.getFileFormat());

        image = examImageRepository.save(image);

        // 更新序列的影像数
        Integer imageCount = examImageRepository.countBySeriesId(dto.getSeriesId());
        series.setImageCount(imageCount);
        examSeriesRepository.save(series);

        // 更新检查记录的影像数
        if (dto.getExamId() != null) {
            updateExamImageStats(dto.getExamId());
        }

        log.info("接收影像文件成功: seriesId={}, imageNo={}", dto.getSeriesId(), dto.getImageNo());

        return convertImageToVO(image);
    }

    @Override
    @Transactional
    public List<ExamImageVO> receiveImages(List<ImageFileDTO> dtoList) {
        return dtoList.stream()
                .map(this::receiveImage)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamSeriesVO> getSeriesByExamId(String examId) {
        List<ExamSeries> seriesList = examSeriesRepository.findByExamId(examId);
        return seriesList.stream()
                .map(this::convertSeriesToVO)
                .collect(Collectors.toList());
    }

    @Override
    public ExamSeriesVO getSeriesById(String seriesId) {
        ExamSeries series = examSeriesRepository.findById(seriesId)
                .orElseThrow(() -> new BusinessException("序列不存在"));
        ExamSeriesVO vo = convertSeriesToVO(series);
        // 加载影像列表
        List<ExamImage> images = examImageRepository.findBySeriesId(seriesId);
        vo.setImageList(images.stream().map(this::convertImageToVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public List<ExamImageVO> getImagesBySeriesId(String seriesId) {
        List<ExamImage> images = examImageRepository.findBySeriesId(seriesId);
        return images.stream()
                .map(this::convertImageToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamImageVO> getImagesByExamId(String examId) {
        List<ExamImage> images = examImageRepository.findByExamId(examId);
        return images.stream()
                .map(this::convertImageToVO)
                .collect(Collectors.toList());
    }

    @Override
    public ExamImageVO getImageById(String imageId) {
        ExamImage image = examImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException("影像不存在"));
        return convertImageToVO(image);
    }

    @Override
    @Transactional
    public ExamImageVO setKeyImage(String imageId, boolean isKeyImage) {
        ExamImage image = examImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException("影像不存在"));
        image.setIsKeyImage(isKeyImage);
        image = examImageRepository.save(image);
        log.info("设置关键影像: imageId={}, isKeyImage={}", imageId, isKeyImage);
        return convertImageToVO(image);
    }

    @Override
    public List<ExamImageVO> getKeyImagesByExamId(String examId) {
        List<ExamImage> images = examImageRepository.findKeyImagesByExamId(examId);
        return images.stream()
                .map(this::convertImageToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateExamImageStats(String examId) {
        Integer imageCount = examImageRepository.countByExamId(examId);
        ExamRecord record = examRecordRepository.findById(examId).orElse(null);
        if (record != null) {
            record.setImageCount(imageCount);
            examRecordRepository.save(record);
        }
    }

    @Override
    public ExamRecordVO getExamDetail(String examId) {
        ExamRecord record = examRecordRepository.findById(examId)
                .orElseThrow(() -> new BusinessException("检查记录不存在"));

        ExamRecordVO vo = new ExamRecordVO();
        vo.setId(record.getId());
        vo.setExamNo(record.getExamNo());
        vo.setRequestId(record.getRequestId());
        vo.setPatientId(record.getPatientId());
        vo.setPatientName(record.getPatientName());
        vo.setAccessionNo(record.getAccessionNo());
        vo.setStudyId(record.getStudyId());
        vo.setExamType(record.getExamType());
        vo.setExamPart(record.getExamPart());
        vo.setModality(record.getModality());
        vo.setEquipmentId(record.getEquipmentId());
        vo.setEquipmentName(record.getEquipmentName());
        vo.setRoomNo(record.getRoomNo());
        vo.setTechnicianId(record.getTechnicianId());
        vo.setTechnicianName(record.getTechnicianName());
        vo.setExamTime(record.getExamTime());
        vo.setExamDuration(record.getExamDuration());
        vo.setSeriesCount(record.getSeriesCount());
        vo.setImageCount(record.getImageCount());
        vo.setStoragePath(record.getStoragePath());
        vo.setContrastAgent(record.getContrastAgent());
        vo.setContrastDose(record.getContrastDose());
        vo.setRadiationDose(record.getRadiationDose());
        vo.setExamStatus(record.getExamStatus());
        vo.setReportStatus(record.getReportStatus());
        vo.setExamDescription(record.getExamDescription());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());

        // 加载序列列表
        List<ExamSeries> seriesList = examSeriesRepository.findByExamId(examId);
        vo.setSeriesList(seriesList.stream()
                .map(s -> {
                    ExamSeriesVO svo = convertSeriesToVO(s);
                    List<ExamImage> images = examImageRepository.findBySeriesId(s.getId());
                    svo.setImageList(images.stream().map(this::convertImageToVO).collect(Collectors.toList()));
                    return svo;
                })
                .collect(Collectors.toList()));

        return vo;
    }

    private ExamSeriesVO convertSeriesToVO(ExamSeries series) {
        ExamSeriesVO vo = new ExamSeriesVO();
        vo.setId(series.getId());
        vo.setExamId(series.getExamId());
        vo.setSeriesNo(series.getSeriesNo());
        vo.setSeriesUid(series.getSeriesUid());
        vo.setSeriesDescription(series.getSeriesDescription());
        vo.setModality(series.getModality());
        vo.setBodyPart(series.getBodyPart());
        vo.setImageCount(series.getImageCount());
        vo.setStoragePath(series.getStoragePath());
        vo.setScanDate(series.getScanDate());
        vo.setScanTime(series.getScanTime());
        vo.setKvp(series.getKvp());
        vo.setMas(series.getMas());
        vo.setSliceThickness(series.getSliceThickness());
        vo.setPixelSpacing(series.getPixelSpacing());
        vo.setSeriesDirection(series.getSeriesDirection());
        vo.setProtocolName(series.getProtocolName());
        vo.setRemark(series.getRemark());
        vo.setCreateTime(series.getCreateTime() != null ? series.getCreateTime().toLocalDate() : null);
        return vo;
    }

    private ExamImageVO convertImageToVO(ExamImage image) {
        ExamImageVO vo = new ExamImageVO();
        vo.setId(image.getId());
        vo.setSeriesId(image.getSeriesId());
        vo.setExamId(image.getExamId());
        vo.setImageNo(image.getImageNo());
        vo.setImageUid(image.getImageUid());
        vo.setSopUid(image.getSopUid());
        vo.setImagePath(image.getImagePath());
        vo.setThumbnailPath(image.getThumbnailPath());
        vo.setImageWidth(image.getImageWidth());
        vo.setImageHeight(image.getImageHeight());
        vo.setBitsAllocated(image.getBitsAllocated());
        vo.setBitsStored(image.getBitsStored());
        vo.setWindowCenter(image.getWindowCenter());
        vo.setWindowWidth(image.getWindowWidth());
        vo.setIsKeyImage(image.getIsKeyImage());
        vo.setImageDescription(image.getImageDescription());
        vo.setFileSize(image.getFileSize());
        vo.setFileFormat(image.getFileFormat());
        return vo;
    }
}