package com.yhj.his.module.pacs.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.pacs.dto.*;
import com.yhj.his.module.pacs.vo.*;

import java.util.List;

/**
 * 检查项目管理服务接口
 */
public interface ExamItemService {

    /**
     * 创建检查项目
     */
    ExamItemVO createItem(ExamItemDTO dto);

    /**
     * 更新检查项目
     */
    ExamItemVO updateItem(ExamItemDTO dto);

    /**
     * 删除检查项目
     */
    void deleteItem(String itemId);

    /**
     * 启用检查项目
     */
    ExamItemVO enableItem(String itemId);

    /**
     * 停用检查项目
     */
    ExamItemVO disableItem(String itemId);

    /**
     * 查询项目详情
     */
    ExamItemVO getItemById(String itemId);

    /**
     * 根据项目编码查询
     */
    ExamItemVO getItemByCode(String itemCode);

    /**
     * 查询所有启用的项目
     */
    List<ExamItemVO> getActiveItems();

    /**
     * 根据检查类型查询项目
     */
    List<ExamItemVO> getItemsByExamType(String examType);

    /**
     * 分页查询项目
     */
    PageResult<ExamItemVO> queryItems(String itemCode, String itemName, String examType, String status, Integer pageNum, Integer pageSize);

    /**
     * 查询需要造影的项目
     */
    List<ExamItemVO> getContrastItems();

    /**
     * 批量创建项目
     */
    List<ExamItemVO> batchCreateItems(List<ExamItemDTO> dtoList);
}