package com.cloudbase.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 和风天气 API 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "qweather")
public class QWeatherProperties {

    /** 和风天气 API Key */
    private String apiKey;

    /** 和风天气开发服务基础地址 */
    private String baseUrl = "https://devapi.qweather.com/v7";
}
