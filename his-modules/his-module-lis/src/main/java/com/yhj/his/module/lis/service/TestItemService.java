package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestItemCreateDTO;
import com.yhj.his.module.lis.dto.TestItemUpdateDTO;
import com.yhj.his.module.lis.entity.TestItem;
import com.yhj.his.module.lis.enums.TestItemCategory;
import com.yhj.his.module.lis.enums.TestItemStatus;
import com.yhj.his.module.lis.vo.TestItemVO;

import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 检验项目服务接口
 */
public interface TestItemService {

    /**
     * 创建检验项目
     */
    TestItemVO create(TestItemCreateDTO dto);

    /**
     * 更新检验项目
     */
    TestItemVO update(String id, TestItemUpdateDTO dto);

    /**
     * 根据ID获取检验项目
     */
    TestItemVO getById(String id);

    /**
     * 根据项目编码获取检验项目
     */
    TestItemVO getByItemCode(String itemCode);

    /**
     * 分页查询检验项目
     */
    PageResult<TestItemVO> list(Pageable pageable);

    /**
     * 根据关键词搜索检验项目
     */
    PageResult<TestItemVO> search(String keyword, TestItemStatus status, Pageable pageable);

    /**
     * 根据分类查询检验项目
     */
    List<TestItemVO> listByCategory(TestItemCategory category);

    /**
     * 根据状态查询检验项目
     */
    List<TestItemVO> listByStatus(TestItemStatus status);

    /**
     * 查询有危急值的项目
     */
    List<TestItemVO> listCriticalItems();

    /**
     * 删除检验项目
     */
    void delete(String id);

    /**
     * 启用检验项目
     */
    TestItemVO enable(String id);

    /**
     * 停用检验项目
     */
    TestItemVO disable(String id);

    /**
     * 检查项目编码是否存在
     */
    boolean existsByItemCode(String itemCode);
}