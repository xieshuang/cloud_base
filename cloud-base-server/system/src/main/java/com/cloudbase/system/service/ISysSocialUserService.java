package com.cloudbase.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudbase.system.entity.SysSocialUser;

public interface ISysSocialUserService extends IService<SysSocialUser> {

    SysSocialUser findByOpenid(Integer socialType, String openid);

    SysSocialUser findByUserId(Long userId);

    void bindUser(Long userId, Integer socialType, String openid, String unionid, String nickname, String avatar);

    void unbind(Long userId, Integer socialType);
}