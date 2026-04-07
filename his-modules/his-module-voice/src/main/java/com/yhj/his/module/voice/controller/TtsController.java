package com.yhj.his.module.voice.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.service.TtsService;
import com.yhj.his.module.voice.vo.TtsEngineVO;
import com.yhj.his.module.voice.vo.VoiceInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * TTS语音合成控制器
 */
@Tag(name = "TTS语音合成", description = "语音合成引擎配置与调用接口")
@RestController
@RequestMapping("/api/voice/v1/tts")
@RequiredArgsConstructor
public class TtsController {

    private final TtsService ttsService;

    @Operation(summary = "获取可用引擎列表", description = "获取系统支持的TTS引擎列表")
    @GetMapping("/engines")
    public Result<List<TtsEngineVO>> getAvailableEngines() {
        return ttsService.getAvailableEngines();
    }

    @Operation(summary = "获取引擎语音列表", description = "获取指定引擎支持的语音列表")
    @GetMapping("/voices")
    public Result<List<VoiceInfoVO>> getVoices(
            @Parameter(description = "引擎名称") @RequestParam(defaultValue = "SAPI") String engineName) {
        return ttsService.getVoices(engineName);
    }

    @Operation(summary = "合成语音", description = "将文本合成为语音音频")
    @PostMapping("/synthesize")
    public Result<byte[]> synthesize(
            @Parameter(description = "播报内容") @RequestParam String content,
            @Parameter(description = "引擎名称") @RequestParam(defaultValue = "SAPI") String engineName,
            @Parameter(description = "语音名称") @RequestParam(required = false) String voiceName,
            @Parameter(description = "语速(0.5-2.0)") @RequestParam(defaultValue = "1.0") Double speed,
            @Parameter(description = "音量(0-100)") @RequestParam(defaultValue = "80") Integer volume,
            @Parameter(description = "音调(0-100)") @RequestParam(defaultValue = "50") Integer pitch) {
        return ttsService.synthesize(content, engineName, voiceName, speed, volume, pitch);
    }

    @Operation(summary = "合成语音并缓存", description = "合成语音并保存到缓存")
    @PostMapping("/synthesize-cache")
    public Result<String> synthesizeAndCache(
            @Parameter(description = "播报内容") @RequestParam String content,
            @Parameter(description = "引擎名称") @RequestParam(defaultValue = "SAPI") String engineName,
            @Parameter(description = "语音名称") @RequestParam(required = false) String voiceName,
            @Parameter(description = "语速(0.5-2.0)") @RequestParam(defaultValue = "1.0") Double speed,
            @Parameter(description = "音量(0-100)") @RequestParam(defaultValue = "80") Integer volume) {
        return ttsService.synthesizeAndCache(content, engineName, voiceName, speed, volume);
    }

    @Operation(summary = "获取缓存音频", description = "从缓存获取已合成的音频")
    @GetMapping("/cached/{cacheKey}")
    public Result<byte[]> getCachedAudio(@Parameter(description = "缓存Key") @PathVariable String cacheKey) {
        return ttsService.getCachedAudio(cacheKey);
    }

    @Operation(summary = "清除语音缓存", description = "清除所有语音缓存")
    @PostMapping("/cache/clear")
    public Result<Void> clearCache() {
        return ttsService.clearCache();
    }

    @Operation(summary = "配置默认引擎", description = "配置默认使用的TTS引擎")
    @PostMapping("/config/engine")
    public Result<Void> configureDefaultEngine(
            @Parameter(description = "引擎名称") @RequestParam String engineName,
            @RequestBody(required = false) Map<String, Object> config) {
        return ttsService.configureDefaultEngine(engineName, config);
    }

    @Operation(summary = "获取默认引擎配置", description = "获取当前默认引擎配置")
    @GetMapping("/config/default")
    public Result<TtsEngineVO> getDefaultEngine() {
        return ttsService.getDefaultEngine();
    }

    @Operation(summary = "测试语音合成", description = "测试语音合成功能是否正常")
    @PostMapping("/test")
    public Result<Void> testSynthesis(
            @Parameter(description = "测试内容") @RequestParam(defaultValue = "语音合成测试") String content,
            @Parameter(description = "引擎名称") @RequestParam(defaultValue = "SAPI") String engineName,
            @Parameter(description = "语音名称") @RequestParam(required = false) String voiceName) {
        return ttsService.testSynthesis(content, engineName, voiceName);
    }

    @Operation(summary = "预生成常用语音", description = "预生成数字和常用短语语音缓存")
    @PostMapping("/pre-generate")
    public Result<Void> preGenerateCommonVoices() {
        return ttsService.preGenerateCommonVoices();
    }
}