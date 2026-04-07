package com.yhj.his.module.voice.service;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.vo.TtsEngineVO;
import com.yhj.his.module.voice.vo.VoiceInfoVO;

import java.util.List;
import java.util.Map;

/**
 * TTS语音合成服务接口
 */
public interface TtsService {

    /**
     * 获取可用的TTS引擎列表
     */
    Result<List<TtsEngineVO>> getAvailableEngines();

    /**
     * 获取引擎支持的语音列表
     */
    Result<List<VoiceInfoVO>> getVoices(String engineName);

    /**
     * 合成语音
     */
    Result<byte[]> synthesize(String content, String engineName, String voiceName,
                              Double speed, Integer volume, Integer pitch);

    /**
     * 合成语音并缓存
     */
    Result<String> synthesizeAndCache(String content, String engineName, String voiceName,
                                      Double speed, Integer volume);

    /**
     * 获取缓存的语音文件
     */
    Result<byte[]> getCachedAudio(String cacheKey);

    /**
     * 清除语音缓存
     */
    Result<Void> clearCache();

    /**
     * 配置默认引擎
     */
    Result<Void> configureDefaultEngine(String engineName, Map<String, Object> config);

    /**
     * 获取当前默认引擎配置
     */
    Result<TtsEngineVO> getDefaultEngine();

    /**
     * 测试语音合成
     */
    Result<Void> testSynthesis(String content, String engineName, String voiceName);

    /**
     * 预生成常用语音缓存
     */
    Result<Void> preGenerateCommonVoices();
}