package com.cloudbase.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DeepSeek AI 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekProperties {

    /** DeepSeek API Key */
    private String apiKey;

    /** API 端点地址，默认指向 DeepSeek 官方 */
    private String baseUrl = "https://api.deepseek.com";

    /** 模型名称，如 deepseek-chat、deepseek-reasoner */
    private String model = "deepseek-chat";
}
