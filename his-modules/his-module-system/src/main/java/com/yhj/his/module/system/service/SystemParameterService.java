package com.yhj.his.module.system.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.SystemParameterDTO;
import com.yhj.his.module.system.vo.SystemParameterVO;

import java.util.List;
import java.util.Map;

/**
 * 系统参数服务接口
 */
public interface SystemParameterService {

    /**
     * 创建参数
     *
     * @param dto 参数信息
     * @return 参数信息
     */
    Result<SystemParameterVO> create(SystemParameterDTO dto);

    /**
     * 更新参数
     *
     * @param dto 参数信息
     * @return 参数信息
     */
    Result<SystemParameterVO> update(SystemParameterDTO dto);

    /**
     * 删除参数
     *
     * @param paramId 参数ID
     * @return 删除结果
     */
    Result<Void> delete(String paramId);

    /**
     * 获取参数详情
     *
     * @param paramId 参数ID
     * @return 参数信息
     */
    Result<SystemParameterVO> getById(String paramId);

    /**
     * 根据参数编码获取参数值
     *
     * @param paramCode 参数编码
     * @return 参数值
     */
    Result<String> getValueByCode(String paramCode);

    /**
     * 分页查询参数
     *
     * @param paramName 参数名称
     * @param paramCode 参数编码
     * @param paramGroup 参数分组
     * @param paramType 参数类型
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 参数分页列表
     */
    Result<PageResult<SystemParameterVO>> page(String paramName, String paramCode, String paramGroup,
                                               String paramType, Integer pageNum, Integer pageSize);

    /**
     * 获取所有参数列表
     *
     * @return 参数列表
     */
    Result<List<SystemParameterVO>> listAll();

    /**
     * 根据分组获取参数列表
     *
     * @param paramGroup 参数分组
     * @return 参数列表
     */
    Result<List<SystemParameterVO>> listByGroup(String paramGroup);

    /**
     * 根据编码列表获取参数Map
     *
     * @param paramCodes 参数编码列表
     * @return 参数Map
     */
    Result<Map<String, String>> getValuesByCodes(List<String> paramCodes);
}