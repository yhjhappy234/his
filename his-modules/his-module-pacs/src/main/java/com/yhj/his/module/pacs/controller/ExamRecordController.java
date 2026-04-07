package com.yhj.his.module.pacs.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pacs.dto.ImageFileDTO;
import com.yhj.his.module.pacs.dto.ImageReceiveDTO;
import com.yhj.his.module.pacs.service.ImageService;
import com.yhj.his.module.pacs.vo.ExamImageVO;
import com.yhj.his.module.pacs.vo.ExamRecordVO;
import com.yhj.his.module.pacs.vo.ExamSeriesVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检查记录与影像管理控制器
 */
@RestController
@RequestMapping("/api/pacs/v1/exam-records")
@Tag(name = "检查记录与影像管理", description = "检查记录查询、影像接收与管理等操作")
public class ExamRecordController {

    @Autowired
    private ImageService imageService;

    @Operation(summary = "查询检查详情", description = "查询检查详情，包含序列和影像信息")
    @GetMapping("/{examId}")
    public Result<ExamRecordVO> getExamDetail(
            @Parameter(description = "检查ID") @PathVariable String examId) {
        ExamRecordVO result = imageService.getExamDetail(examId);
        return Result.success(result);
    }

    @Operation(summary = "查询检查的所有序列", description = "查询指定检查的所有影像序列")
    @GetMapping("/{examId}/series")
    public Result<List<ExamSeriesVO>> getSeriesByExamId(
            @Parameter(description = "检查ID") @PathVariable String examId) {
        List<ExamSeriesVO> result = imageService.getSeriesByExamId(examId);
        return Result.success(result);
    }

    @Operation(summary = "查询检查的所有影像", description = "查询指定检查的所有影像文件")
    @GetMapping("/{examId}/images")
    public Result<List<ExamImageVO>> getImagesByExamId(
            @Parameter(description = "检查ID") @PathVariable String examId) {
        List<ExamImageVO> result = imageService.getImagesByExamId(examId);
        return Result.success(result);
    }

    @Operation(summary = "查询检查的关键影像", description = "查询指定检查的关键影像")
    @GetMapping("/{examId}/key-images")
    public Result<List<ExamImageVO>> getKeyImagesByExamId(
            @Parameter(description = "检查ID") @PathVariable String examId) {
        List<ExamImageVO> result = imageService.getKeyImagesByExamId(examId);
        return Result.success(result);
    }

    @Operation(summary = "接收影像序列", description = "接收DICOM影像序列数据")
    @PostMapping("/series/receive")
    public Result<ExamSeriesVO> receiveSeries(@Valid @RequestBody ImageReceiveDTO dto) {
        ExamSeriesVO result = imageService.receiveSeries(dto);
        return Result.success("序列接收成功", result);
    }

    @Operation(summary = "接收单个影像", description = "接收单个DICOM影像文件")
    @PostMapping("/images/receive")
    public Result<ExamImageVO> receiveImage(@Valid @RequestBody ImageFileDTO dto) {
        ExamImageVO result = imageService.receiveImage(dto);
        return Result.success("影像接收成功", result);
    }

    @Operation(summary = "批量接收影像", description = "批量接收DICOM影像文件")
    @PostMapping("/images/batch-receive")
    public Result<List<ExamImageVO>> receiveImages(@RequestBody List<ImageFileDTO> dtoList) {
        List<ExamImageVO> result = imageService.receiveImages(dtoList);
        return Result.success("批量影像接收成功", result);
    }

    @Operation(summary = "查询序列详情", description = "查询指定序列的详细信息")
    @GetMapping("/series/{seriesId}")
    public Result<ExamSeriesVO> getSeriesById(
            @Parameter(description = "序列ID") @PathVariable String seriesId) {
        ExamSeriesVO result = imageService.getSeriesById(seriesId);
        return Result.success(result);
    }

    @Operation(summary = "查询序列的所有影像", description = "查询指定序列的所有影像文件")
    @GetMapping("/series/{seriesId}/images")
    public Result<List<ExamImageVO>> getImagesBySeriesId(
            @Parameter(description = "序列ID") @PathVariable String seriesId) {
        List<ExamImageVO> result = imageService.getImagesBySeriesId(seriesId);
        return Result.success(result);
    }

    @Operation(summary = "查询影像详情", description = "查询指定影像的详细信息")
    @GetMapping("/images/{imageId}")
    public Result<ExamImageVO> getImageById(
            @Parameter(description = "影像ID") @PathVariable String imageId) {
        ExamImageVO result = imageService.getImageById(imageId);
        return Result.success(result);
    }

    @Operation(summary = "设置关键影像", description = "设置或取消影像的关键影像标记")
    @PutMapping("/images/{imageId}/key-image")
    public Result<ExamImageVO> setKeyImage(
            @Parameter(description = "影像ID") @PathVariable String imageId,
            @Parameter(description = "是否关键影像") @RequestParam boolean isKeyImage) {
        ExamImageVO result = imageService.setKeyImage(imageId, isKeyImage);
        return Result.success("设置成功", result);
    }

    @Operation(summary = "更新检查影像统计", description = "更新检查的影像数量统计")
    @PutMapping("/{examId}/stats")
    public Result<Void> updateExamImageStats(
            @Parameter(description = "检查ID") @PathVariable String examId) {
        imageService.updateExamImageStats(examId);
        return Result.successVoid();
    }
}