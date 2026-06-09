package com.cloudbase.system.agent.config;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.model.OpenAIChatModel;
import io.agentscope.core.session.InMemorySession;
import io.agentscope.core.tool.Toolkit;
import com.cloudbase.system.agent.tool.QWeatherTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AgentScope 核心配置类
 * <p>
 * 负责创建 DeepSeek 模型实例、注册天气工具包、
 * 构建天气查询 ReActAgent Bean。
 */
@Slf4j
@Configuration
public class AgentConfig {

    /** DeepSeek API Key */
    @Value("${deepseek.api-key}")
    private String deepseekApiKey;

    /** DeepSeek API 端点地址 */
    @Value("${deepseek.base-url:https://api.deepseek.com}")
    private String deepseekBaseUrl;

    /** DeepSeek 模型名称 */
    @Value("${deepseek.model:deepseek-chat}")
    private String deepseekModel;

    /**
     * 创建 DeepSeek ChatModel Bean
     * <p>
     * 通过 OpenAI 兼容模式接入 DeepSeek，
     * 指定 baseUrl 为 DeepSeek 官方 API 端点。
     */
    @Bean
    public OpenAIChatModel deepSeekChatModel() {
        log.info("初始化 DeepSeek ChatModel：model={}, baseUrl={}", deepseekModel, deepseekBaseUrl);
        return OpenAIChatModel.builder()
                .apiKey(deepseekApiKey)
                .modelName(deepseekModel)
                .baseUrl(deepseekBaseUrl)
                .build();
    }

    /**
     * 注册天气工具包
     * <p>
     * 将 QWeatherTool 中的 @Tool 方法注册到 Toolkit，
     * Agent 可以自动根据用户意图调用这些工具。
     */
    @Bean
    public Toolkit weatherToolkit(QWeatherTool qWeatherTool) {
        log.info("注册天气工具包");
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(qWeatherTool);
        return toolkit;
    }

    /**
     * 创建天气查询 ReActAgent Bean
     * <p>
     * ReActAgent 具有 Reasoning + Acting 能力，
     * 会根据系统提示词自动判断是否需要调用天气工具。
     * 通过 InMemorySession 支持多轮对话上下文保持。
     */
    @Bean
    public ReActAgent weatherAgent(OpenAIChatModel deepSeekChatModel, Toolkit weatherToolkit) {
        log.info("初始化天气查询 ReActAgent");
        return ReActAgent.builder()
                .name("WeatherAssistant")
                .model(deepSeekChatModel)
                .sysPrompt("你是一个专业的天气查询助手。"
                        + "当用户询问天气相关信息时，请使用提供的天气工具获取真实数据，"
                        + "然后用友好、清晰的中文组织回复。"
                        + "如果用户询问的不是天气相关问题，请礼貌地告知对方你只能查询天气信息。"
                        + "回复时请尽量简洁明了，突出关键信息如温度、天气状况等。")
                .toolkit(weatherToolkit)
                .session(new InMemorySession())
                .build();
    }
}
