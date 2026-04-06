package com.yhj.his.common.core.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 分页工具
 */
public class PageUtils {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 1000;

    /**
     * 构建分页对象
     *
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页大小
     * @return Pageable
     */
    public static Pageable of(Integer pageNum, Integer pageSize) {
        return of(pageNum, pageSize, null);
    }

    /**
     * 构建分页对象（带排序）
     *
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页大小
     * @param sort     排序
     * @return Pageable
     */
    public static Pageable of(Integer pageNum, Integer pageSize, Sort sort) {
        int validPageNum = pageNum != null && pageNum > 0 ? pageNum - 1 : DEFAULT_PAGE_NUM - 1;
        int validPageSize = pageSize != null && pageSize > 0 ? Math.min(pageSize, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;

        if (sort != null) {
            return PageRequest.of(validPageNum, validPageSize, sort);
        }
        return PageRequest.of(validPageNum, validPageSize);
    }

    /**
     * 构建分页对象（按创建时间倒序）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return Pageable
     */
    public static Pageable ofDescByCreateTime(Integer pageNum, Integer pageSize) {
        return of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
    }

    /**
     * 构建分页对象（兼容旧方法名）
     *
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页大小
     * @return Pageable
     */
    public static Pageable toPageable(Integer pageNum, Integer pageSize) {
        return of(pageNum, pageSize);
    }
}