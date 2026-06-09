package com.cloudbase.system.agent.controller;

import com.cloudbase.common.core.domain.agent.WeatherChatRequest;
import com.cloudbase.common.core.result.Result;
import com.cloudbase.system.agent.service.WeatherAgentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 天气查询聊天机器人 REST 接口
 * <p>
 * 基于 AgentScope Java + DeepSeek 大模型 + 和风天气 API，
 * 提供自然语言天气查询服务。
 */
@Slf4j
@Api(tags = "AI天气查询")
@RestController
@RequestMapping("/agent/weather")
@RequiredArgsConstructor
public class WeatherAgentController {

    private final WeatherAgentService weatherAgentService;

    /**
     * 天气查询聊天接口
     * <p>
     * 接收用户自然语言输入，通过 AgentScope ReActAgent 进行意图理解，
     * 自动调用和风天气 API 获取实时数据并返回格式化结果。
     *
     * @param request 聊天请求，包含用户消息和可选的会话 ID
     * @return Agent 回复内容
     */
    @ApiOperation("天气查询聊天")
    @PostMapping("/chat")
    public Mono<Result<String>> chat(@RequestBody WeatherChatRequest request) {
        log.info("天气聊天请求：message={}, sessionId={}",
                request.getMessage(), request.getSessionId());
        return weatherAgentService.queryWeather(request.getMessage())
                .map(Result::success)
                .onErrorResume(e -> {
                    log.error("天气聊天异常", e);
                    return Mono.just(Result.error("天气查询服务暂时不可用，请稍后再试"));
                });
    }
}
