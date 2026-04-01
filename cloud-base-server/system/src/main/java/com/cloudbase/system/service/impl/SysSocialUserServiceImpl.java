package com.cloudbase.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudbase.system.entity.SysSocialUser;
import com.cloudbase.system.mapper.SysSocialUserMapper;
import com.cloudbase.system.service.ISysSocialUserService;
import org.springframework.stereotype.Service;

@Service
public class SysSocialUserServiceImpl extends ServiceImpl<SysSocialUserMapper, SysSocialUser> implements ISysSocialUserService {

    @Override
    public SysSocialUser findByOpenid(Integer socialType, String openid) {
        LambdaQueryWrapper<SysSocialUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSocialUser::getSocialType, socialType)
               .eq(SysSocialUser::getOpenid, openid);
        return getOne(wrapper);
    }

    @Override
    public SysSocialUser findByUserId(Long userId) {
        LambdaQueryWrapper<SysSocialUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSocialUser::getUserId, userId);
        return getOne(wrapper);
    }

    @Override
    public void bindUser(Long userId, Integer socialType, String openid, String unionid, String nickname, String avatar) {
        SysSocialUser socialUser = new SysSocialUser();
        socialUser.setUserId(userId);
        socialUser.setSocialType(socialType);
        socialUser.setOpenid(openid);
        socialUser.setUnionid(unionid);
        socialUser.setNickname(nickname);
        socialUser.setAvatar(avatar);
        socialUser.setStatus(1);
        save(socialUser);
    }

    @Override
    public void unbind(Long userId, Integer socialType) {
        LambdaQueryWrapper<SysSocialUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSocialUser::getUserId, userId)
               .eq(SysSocialUser::getSocialType, socialType);
        remove(wrapper);
    }
}