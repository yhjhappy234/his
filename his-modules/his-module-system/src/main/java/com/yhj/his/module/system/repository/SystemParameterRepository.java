package com.yhj.his.module.system.repository;

import com.yhj.his.module.system.entity.SystemParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统参数数据访问
 */
@Repository
public interface SystemParameterRepository extends JpaRepository<SystemParameter, String> {

    /**
     * 根据参数编码查询
     */
    Optional<SystemParameter> findByParamCode(String paramCode);

    /**
     * 根据参数编码查询(未删除)
     */
    Optional<SystemParameter> findByParamCodeAndDeletedFalse(String paramCode);

    /**
     * 查询所有未删除参数
     */
    List<SystemParameter> findByDeletedFalseOrderBySortOrderAsc();

    /**
     * 根据参数分组查询
     */
    List<SystemParameter> findByParamGroupAndDeletedFalseOrderBySortOrderAsc(String paramGroup);

    /**
     * 根据参数类型查询
     */
    List<SystemParameter> findByParamTypeAndDeletedFalseOrderBySortOrderAsc(String paramType);

    /**
     * 分页查询参数
     */
    Page<SystemParameter> findByDeletedFalse(Pageable pageable);

    /**
     * 根据条件分页查询参数
     */
    @Query("SELECT p FROM SystemParameter p WHERE p.deleted = false " +
           "AND (:paramName IS NULL OR p.paramName LIKE %:paramName%) " +
           "AND (:paramCode IS NULL OR p.paramCode LIKE %:paramCode%) " +
           "AND (:paramGroup IS NULL OR p.paramGroup = :paramGroup) " +
           "AND (:paramType IS NULL OR p.paramType = :paramType)")
    Page<SystemParameter> findByCondition(
            @Param("paramName") String paramName,
            @Param("paramCode") String paramCode,
            @Param("paramGroup") String paramGroup,
            @Param("paramType") String paramType,
            Pageable pageable);

    /**
     * 检查参数编码是否存在
     */
    boolean existsByParamCodeAndDeletedFalse(String paramCode);

    /**
     * 根据参数编码列表查询
     */
    List<SystemParameter> findByParamCodeInAndDeletedFalse(List<String> paramCodes);
}