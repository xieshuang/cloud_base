package com.cloudbase.system.service.impl;

import com.cloudbase.common.exception.BusinessException;
import com.cloudbase.system.config.WeixinProperties;
import com.cloudbase.system.service.IWeixinService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinServiceImpl implements IWeixinService {

    private static final String MINI_PROGRAM_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String MP_AUTHORIZE_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private static final String MP_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";

    private final WeixinProperties weixinProperties;
    private final RestTemplate restTemplate;

    public WeixinServiceImpl(WeixinProperties weixinProperties) {
        this.weixinProperties = weixinProperties;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Map<String, String> miniProgramLogin(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", weixinProperties.getMini().getAppid());
        params.put("secret", weixinProperties.getMini().getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        try {
            String url = buildUrl(MINI_PROGRAM_URL, params);
            String response = restTemplate.getForObject(url, String.class);
            return parseWeixinResponse(response);
        } catch (Exception e) {
            throw new BusinessException("微信小程序登录失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, String> mpGetOpenId(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", weixinProperties.getMp().getAppid());
        params.put("secret", weixinProperties.getMp().getSecret());
        params.put("code", code);
        params.put("grant_type", "authorization_code");

        try {
            String url = buildUrl(MP_AUTHORIZE_URL, params);
            String response = restTemplate.getForObject(url, String.class);
            return parseWeixinResponse(response);
        } catch (Exception e) {
            throw new BusinessException("微信公众号授权失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getUserInfo(String accessToken, String openid) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        params.put("openid", openid);
        params.put("lang", "zh_CN");

        try {
            String url = buildUrl(MP_USER_INFO_URL, params);
            String response = restTemplate.getForObject(url, String.class);
            return parseWeixinResponseToObject(response);
        } catch (Exception e) {
            throw new BusinessException("获取用户信息失败: " + e.getMessage());
        }
    }

    private String buildUrl(String baseUrl, Map<String, String> params) {
        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?");
        params.forEach((key, value) -> {
            url.append(key).append("=").append(value).append("&");
        });
        return url.substring(0, url.length() - 1);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseWeixinResponse(String response) {
        com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(response);
        if (json.containsKey("errcode") && json.getInteger("errcode") != 0) {
            throw new BusinessException("微信接口返回错误: " + json.getString("errmsg"));
        }
        Map<String, String> result = new HashMap<>();
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (value != null) {
                result.put(key, value.toString());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseWeixinResponseToObject(String response) {
        com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(response);
        if (json.containsKey("errcode") && json.getInteger("errcode") != 0) {
            throw new BusinessException("微信接口返回错误: " + json.getString("errmsg"));
        }
        Map<String, Object> result = new HashMap<>();
        for (String key : json.keySet()) {
            result.put(key, json.get(key));
        }
        return result;
    }
}