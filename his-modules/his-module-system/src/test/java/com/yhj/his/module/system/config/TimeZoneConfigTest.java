package com.yhj.his.module.system.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TimeZoneConfig 单元测试
 * 目标覆盖率: 90%+
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("时区配置测试")
class TimeZoneConfigTest {

    @InjectMocks
    private TimeZoneConfig timeZoneConfig;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(timeZoneConfig, "defaultTimeZone", "Asia/Shanghai");
    }

    @Nested
    @DisplayName("时区Bean配置测试")
    class ZoneIdTests {

        @Test
        @DisplayName("创建默认时区ZoneId")
        void defaultZoneId() {
            // When
            ZoneId zoneId = timeZoneConfig.defaultZoneId();

            // Then
            assertNotNull(zoneId);
            assertEquals("Asia/Shanghai", zoneId.getId());
        }

        @Test
        @DisplayName("自定义时区配置")
        void customTimeZone() {
            // Given
            ReflectionTestUtils.setField(timeZoneConfig, "defaultTimeZone", "UTC");

            // When
            ZoneId zoneId = timeZoneConfig.defaultZoneId();

            // Then
            assertEquals("UTC", zoneId.getId());
        }

        @Test
        @DisplayName("系统默认时区已设置")
        void systemTimeZoneSet() {
            // When
            timeZoneConfig.defaultZoneId();

            // Then
            assertEquals("Asia/Shanghai", TimeZone.getDefault().getID());
        }
    }

    @Nested
    @DisplayName("时钟配置测试")
    class ClockTests {

        @Test
        @DisplayName("创建默认时钟")
        void defaultClock() {
            // Given
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");

            // When
            Clock clock = timeZoneConfig.defaultClock(zoneId);

            // Then
            assertNotNull(clock);
            assertEquals(zoneId, clock.getZone());
        }

        @Test
        @DisplayName("时钟时间正确")
        void clockTimeCorrect() {
            // Given
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");
            Clock clock = timeZoneConfig.defaultClock(zoneId);

            // When
            ZonedDateTime nowFromClock = ZonedDateTime.now(clock);
            ZonedDateTime nowFromZone = ZonedDateTime.now(zoneId);

            // Then - 两者应该非常接近
            assertTrue(Math.abs(nowFromClock.toEpochSecond() - nowFromZone.toEpochSecond()) < 2);
        }
    }

    @Nested
    @DisplayName("静态方法测试")
    class StaticMethodTests {

        @Test
        @DisplayName("获取当前时间")
        void now() {
            // Given
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");

            // When
            ZonedDateTime now = TimeZoneConfig.now(zoneId);

            // Then
            assertNotNull(now);
            assertEquals(zoneId, now.getZone());
        }

        @Test
        @DisplayName("获取当前时间字符串ISO格式")
        void nowAsString() {
            // Given
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");

            // When
            String nowStr = TimeZoneConfig.nowAsString(zoneId);

            // Then
            assertNotNull(nowStr);
            assertTrue(nowStr.contains("Asia/Shanghai"));
        }

        @Test
        @DisplayName("获取当前时间字符串自定义格式")
        void formatNow() {
            // Given
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");
            String pattern = "yyyy-MM-dd HH:mm:ss";

            // When
            String formatted = TimeZoneConfig.formatNow(zoneId, pattern);

            // Then
            assertNotNull(formatted);
            assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        }
    }
}