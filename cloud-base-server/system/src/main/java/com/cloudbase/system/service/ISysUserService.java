package com.cloudbase.system.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudbase.system.entity.SysUser;
import com.cloudbase.system.mapper.SysUserMapper;

import java.util.List;

public interface ISysUserService extends IService<SysUser> {

    SysUser selectByUsername(String username);

    SysUser selectById(Long id);

    List<SysUser> selectUserList(SysUser user);

    List<Long> selectRoleIdsByUserId(Long userId);

    void assignRoles(Long userId, List<Long> roleIds);

    void resetPassword(Long userId, String password);

    void changeStatus(Long userId, Integer status);

    boolean verifyPassword(String rawPassword, String encodedPassword);

    String encodePassword(String rawPassword);

    boolean isUsernameExist(String username, Long excludeId);
}
