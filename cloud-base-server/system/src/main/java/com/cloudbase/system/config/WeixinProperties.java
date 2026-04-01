package com.cloudbase.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "weixin")
public class WeixinProperties {

    private MiniConfig mini;
    private MpConfig mp;

    @Data
    public static class MiniConfig {
        private String appid;
        private String secret;
    }

    @Data
    public static class MpConfig {
        private String appid;
        private String secret;
    }
}