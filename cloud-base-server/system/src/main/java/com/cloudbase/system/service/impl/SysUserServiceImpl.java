package com.cloudbase.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudbase.common.exception.BusinessException;
import com.cloudbase.system.entity.SysUser;
import com.cloudbase.system.entity.SysUserRole;
import com.cloudbase.system.mapper.SysUserMapper;
import com.cloudbase.system.mapper.SysUserRoleMapper;
import com.cloudbase.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public SysUser selectByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public SysUser selectById(Long id) {
        SysUser user = baseMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (user != null) {
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                wrapper.like(SysUser::getUsername, user.getUsername());
            }
            if (user.getNickname() != null && !user.getNickname().isEmpty()) {
                wrapper.like(SysUser::getNickname, user.getNickname());
            }
            if (user.getStatus() != null) {
                wrapper.eq(SysUser::getStatus, user.getStatus());
            }
            if (user.getDeptId() != null) {
                wrapper.eq(SysUser::getDeptId, user.getDeptId());
            }
        }
        wrapper.eq(SysUser::getDeleted, 0);
        wrapper.orderByDesc(SysUser::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Long> selectRoleIdsByUserId(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = roleIds.stream().map(roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            }).collect(Collectors.toList());
            userRoleMapper.insertBatch(userRoles);
        }
    }

    @Override
    public void resetPassword(Long userId, String password) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(encodePassword(password));
        baseMapper.updateById(user);
    }

    @Override
    public void changeStatus(Long userId, Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus(status);
        baseMapper.updateById(user);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean isUsernameExist(String username, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        wrapper.eq(SysUser::getDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        return baseMapper.selectCount(wrapper) > 0;
    }
}
