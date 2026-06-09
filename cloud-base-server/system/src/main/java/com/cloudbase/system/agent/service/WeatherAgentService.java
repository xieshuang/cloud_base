package com.cloudbase.system.agent.service;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 天气 Agent 服务层
 * <p>
 * 封装 ReActAgent 的调用逻辑，将用户消息转换为 AgentScope 消息格式，
 * 并处理响应式返回值。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherAgentService {

    private final ReActAgent weatherAgent;

    /**
     * 向天气助手发送消息并获取回复
     *
     * @param userMessage 用户输入的自然语言消息
     * @return Agent 的文本回复（响应式 Mono）
     */
    public Mono<String> queryWeather(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return Mono.just("请输入您想查询的天气信息。");
        }

        log.info("天气查询请求：{}", userMessage);

        // 构建 AgentScope 消息对象
        Msg input = Msg.builder()
                .name("user")
                .textContent(userMessage)
                .build();

        // 调用 Agent 并提取文本回复
        return weatherAgent.call(input)
                .map(Msg::getTextContent)
                .doOnSuccess(reply -> log.info("天气查询回复：{}", reply))
                .doOnError(e -> log.error("天气查询异常", e));
    }
}
