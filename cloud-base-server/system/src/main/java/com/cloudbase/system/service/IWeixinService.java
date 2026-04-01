package com.cloudbase.system.service;

import java.util.Map;

public interface IWeixinService {

    Map<String, String> miniProgramLogin(String code);

    Map<String, String> mpGetOpenId(String code);

    Map<String, Object> getUserInfo(String accessToken, String openid);
}