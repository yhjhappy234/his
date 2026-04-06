package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.DataDictionaryDTO;
import com.yhj.his.module.system.vo.DataDictionaryVO;

import java.util.List;

/**
 * 数据字典服务接口
 */
public interface DataDictionaryService {

    /**
     * 创建字典项
     *
     * @param dto 字典信息
     * @return 字典信息
     */
    Result<DataDictionaryVO> create(DataDictionaryDTO dto);

    /**
     * 更新字典项
     *
     * @param dto 字典信息
     * @return 字典信息
     */
    Result<DataDictionaryVO> update(DataDictionaryDTO dto);

    /**
     * 删除字典项
     *
     * @param dictId 字典ID
     * @return 删除结果
     */
    Result<Void> delete(String dictId);

    /**
     * 获取字典项详情
     *
     * @param dictId 字典ID
     * @return 字典信息
     */
    Result<DataDictionaryVO> getById(String dictId);

    /**
     * 分页查询字典项
     *
     * @param dictType 字典类型
     * @param dictName 字典名称
     * @param dictCode 字典编码
     * @param isEnabled 是否启用
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 字典分页列表
     */
    Result<PageResult<DataDictionaryVO>> page(String dictType, String dictName, String dictCode,
                                               Boolean isEnabled, Integer pageNum, Integer pageSize);

    /**
     * 根据字典类型获取字典项列表
     *
     * @param dictType 字典类型
     * @return 字典项列表
     */
    Result<List<DataDictionaryVO>> listByType(String dictType);

    /**
     * 根据字典类型获取启用的字典项列表
     *
     * @param dictType 字典类型
     * @return 字典项列表
     */
    Result<List<DataDictionaryVO>> listEnabledByType(String dictType);

    /**
     * 获取所有字典类型
     *
     * @return 字典类型列表
     */
    Result<List<String>> listAllTypes();

    /**
     * 获取字典树
     *
     * @param dictType 字典类型
     * @return 字典树
     */
    Result<List<DataDictionaryVO>> getTree(String dictType);

    /**
     * 获取字典类型的默认值
     *
     * @param dictType 字典类型
     * @return 默认值
     */
    Result<DataDictionaryVO> getDefault(String dictType);
}