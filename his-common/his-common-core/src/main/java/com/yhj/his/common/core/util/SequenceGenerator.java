package com.yhj.his.common.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 编号生成器
 */
public class SequenceGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    /**
     * 生成业务编号
     * 格式: 前缀 + 日期 + 6位序号
     *
     * @param prefix 前缀
     * @return 编号
     */
    public static synchronized String generate(String prefix) {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        long seq = SEQUENCE.incrementAndGet() % 1000000;
        return prefix + dateStr + String.format("%06d", seq);
    }

    /**
     * 生成业务编号（指定长度）
     * 格式: 前缀 + 日期 + N位序号
     *
     * @param prefix 前缀
     * @param seqLength 序号长度
     * @return 编号
     */
    public static synchronized String generate(String prefix, int seqLength) {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        long maxSeq = (long) Math.pow(10, seqLength);
        long seq = SEQUENCE.incrementAndGet() % maxSeq;
        return prefix + dateStr + String.format("%0" + seqLength + "d", seq);
    }

    /**
     * 生成带时间的业务编号
     * 格式: 前缀 + 日期时间 + 4位序号
     *
     * @param prefix 前缀
     * @return 编号
     */
    public static synchronized String generateWithTime(String prefix) {
        String datetimeStr = LocalDateTime.now().format(DATETIME_FORMAT);
        long seq = SEQUENCE.incrementAndGet() % 10000;
        return prefix + datetimeStr + String.format("%04d", seq);
    }

    /**
     * 生成UUID（无横线）
     *
     * @return UUID
     */
    public static String uuid() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}