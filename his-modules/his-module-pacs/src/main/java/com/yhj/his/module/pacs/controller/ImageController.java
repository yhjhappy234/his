package com.yhj.his.module.pacs.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.service.*;
import com.yhj.his.module.pacs.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 影像采集管理Controller
 */
@Tag(name = "影像采集管理", description = "影像接收、影像查询等接口")
@RestController
@RequestMapping("/api/pacs/v1/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "接收影像序列", description = "接收DICOM影像序列")
    @PostMapping("/series/receive")
    public Result<ExamSeriesVO> receiveSeries(@Valid @RequestBody ImageReceiveDTO dto) {
        ExamSeriesVO vo = imageService.receiveSeries(dto);
        return Result.success("接收成功", vo);
    }

    @Operation(summary = "接收影像文件", description = "接收单个DICOM影像文件")
    @PostMapping("/receive")
    public Result<ExamImageVO> receiveImage(@Valid @RequestBody ImageFileDTO dto) {
        ExamImageVO vo = imageService.receiveImage(dto);
        return Result.success("接收成功", vo);
    }

    @Operation(summary = "批量接收影像", description = "批量接收DICOM影像文件")
    @PostMapping("/batch-receive")
    public Result<List<ExamImageVO>> receiveImages(@Valid @RequestBody List<ImageFileDTO> dtoList) {
        List<ExamImageVO> list = imageService.receiveImages(dtoList);
        return Result.success("批量接收成功", list);
    }

    @Operation(summary = "查询检查序列", description = "查询检查的所有序列")
    @GetMapping("/series/exam/{examId}")
    public Result<List<ExamSeriesVO>> getSeriesByExamId(@PathVariable String examId) {
        List<ExamSeriesVO> list = imageService.getSeriesByExamId(examId);
        return Result.success(list);
    }

    @Operation(summary = "查询序列详情", description = "查询序列详情(包含影像)")
    @GetMapping("/series/{seriesId}")
    public Result<ExamSeriesVO> getSeriesById(@PathVariable String seriesId) {
        ExamSeriesVO vo = imageService.getSeriesById(seriesId);
        return Result.success(vo);
    }

    @Operation(summary = "查询序列影像", description = "查询序列的所有影像")
    @GetMapping("/series/{seriesId}/images")
    public Result<List<ExamImageVO>> getImagesBySeriesId(@PathVariable String seriesId) {
        List<ExamImageVO> list = imageService.getImagesBySeriesId(seriesId);
        return Result.success(list);
    }

    @Operation(summary = "查询检查影像", description = "查询检查的所有影像")
    @GetMapping("/exam/{examId}/images")
    public Result<List<ExamImageVO>> getImagesByExamId(@PathVariable String examId) {
        List<ExamImageVO> list = imageService.getImagesByExamId(examId);
        return Result.success(list);
    }

    @Operation(summary = "查询影像详情", description = "查询影像文件详情")
    @GetMapping("/{imageId}")
    public Result<ExamImageVO> getImageById(@PathVariable String imageId) {
        ExamImageVO vo = imageService.getImageById(imageId);
        return Result.success(vo);
    }

    @Operation(summary = "设置关键影像", description = "设置或取消关键影像标记")
    @PostMapping("/key-image")
    public Result<ExamImageVO> setKeyImage(
            @Parameter(description = "影像ID") @RequestParam String imageId,
            @Parameter(description = "是否关键影像") @RequestParam boolean isKeyImage) {
        ExamImageVO vo = imageService.setKeyImage(imageId, isKeyImage);
        return Result.success(vo);
    }

    @Operation(summary = "查询关键影像", description = "查询检查的关键影像")
    @GetMapping("/exam/{examId}/key-images")
    public Result<List<ExamImageVO>> getKeyImagesByExamId(@PathVariable String examId) {
        List<ExamImageVO> list = imageService.getKeyImagesByExamId(examId);
        return Result.success(list);
    }

    @Operation(summary = "查询检查详情", description = "查询检查详情(包含序列和影像)")
    @GetMapping("/exam/{examId}/detail")
    public Result<ExamRecordVO> getExamDetail(@PathVariable String examId) {
        ExamRecordVO vo = imageService.getExamDetail(examId);
        return Result.success(vo);
    }
}