package com.yhj.his.module.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * 时区配置类
 * 所有日期时间处理使用统一时区（默认中国上海时区）
 */
@Slf4j
@Configuration
public class TimeZoneConfig {

    @Value("${his.timezone:Asia/Shanghai}")
    private String defaultTimeZone;

    /**
     * 默认时区
     */
    @Bean
    public ZoneId defaultZoneId() {
        ZoneId zoneId = ZoneId.of(defaultTimeZone);
        TimeZone.setDefault(TimeZone.getTimeZone(zoneId));
        log.info("系统时区设置为: {}", zoneId);
        return zoneId;
    }

    /**
     * 时区时钟
     */
    @Bean
    public Clock defaultClock(ZoneId zoneId) {
        return Clock.system(zoneId);
    }

    /**
     * 获取当前时间（使用配置时区）
     */
    public static ZonedDateTime now(ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }

    /**
     * 获取当前时间字符串（ISO格式）
     */
    public static String nowAsString(ZoneId zoneId) {
        return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now(zoneId));
    }

    /**
     * 获取当前时间字符串（自定义格式）
     */
    public static String formatNow(ZoneId zoneId, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(ZonedDateTime.now(zoneId));
    }
}