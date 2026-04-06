package com.yhj.his.common.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

/**
 * 编号生成器
 */
@Component
public class SequenceGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private long sequence = 0;

    /**
     * 生成业务编号
     * 格式: 前缀 + 日期 + 6位序号
     *
     * @param prefix 前缀
     * @return 编号
     */
    public synchronized String generate(String prefix) {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        sequence = (sequence + 1) % 1000000;
        return prefix + dateStr + String.format("%06d", sequence);
    }

    /**
     * 生成业务编号（指定长度）
     * 格式: 前缀 + 日期 + N位序号
     *
     * @param prefix 前缀
     * @param seqLength 序号长度
     * @return 编号
     */
    public synchronized String generate(String prefix, int seqLength) {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        long maxSeq = (long) Math.pow(10, seqLength);
        sequence = (sequence + 1) % maxSeq;
        // 使用正确的格式字符串构造方式
        String formatPattern = "%0" + seqLength + "d";
        return prefix + dateStr + String.format(formatPattern, sequence);
    }

    /**
     * 生成带时间的业务编号
     * 格式: 前缀 + 日期时间 + 4位序号
     *
     * @param prefix 前缀
     * @return 编号
     */
    public synchronized String generateWithTime(String prefix) {
        String datetimeStr = LocalDateTime.now().format(DATETIME_FORMAT);
        sequence = (sequence + 1) % 10000;
        return prefix + datetimeStr + String.format("%04d", sequence);
    }

    /**
     * 生成UUID（无横线）
     *
     * @return UUID
     */
    public String uuid() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}