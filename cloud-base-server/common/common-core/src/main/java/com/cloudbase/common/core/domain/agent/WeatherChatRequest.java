package com.cloudbase.common.core.domain.agent;

import lombok.Data;

import java.io.Serializable;

/**
 * 天气查询聊天请求
 */
@Data
public class WeatherChatRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户输入的消息内容 */
    private String message;

    /** 会话 ID，用于关联多轮对话上下文 */
    private String sessionId;
}
