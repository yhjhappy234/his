package com.yhj.his.module.pacs.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.vo.*;

import java.util.List;

/**
 * 影像采集服务接口
 */
public interface ImageService {

    /**
     * 接收影像序列
     */
    ExamSeriesVO receiveSeries(ImageReceiveDTO dto);

    /**
     * 接收影像文件
     */
    ExamImageVO receiveImage(ImageFileDTO dto);

    /**
     * 批量接收影像
     */
    List<ExamImageVO> receiveImages(List<ImageFileDTO> dtoList);

    /**
     * 查询检查的所有序列
     */
    List<ExamSeriesVO> getSeriesByExamId(String examId);

    /**
     * 查询序列详情
     */
    ExamSeriesVO getSeriesById(String seriesId);

    /**
     * 查询序列的所有影像
     */
    List<ExamImageVO> getImagesBySeriesId(String seriesId);

    /**
     * 查询检查的所有影像
     */
    List<ExamImageVO> getImagesByExamId(String examId);

    /**
     * 查询影像详情
     */
    ExamImageVO getImageById(String imageId);

    /**
     * 设置关键影像
     */
    ExamImageVO setKeyImage(String imageId, boolean isKeyImage);

    /**
     * 查询检查的关键影像
     */
    List<ExamImageVO> getKeyImagesByExamId(String examId);

    /**
     * 更新检查影像统计
     */
    void updateExamImageStats(String examId);

    /**
     * 查询检查详情(包含序列和影像)
     */
    ExamRecordVO getExamDetail(String examId);
}