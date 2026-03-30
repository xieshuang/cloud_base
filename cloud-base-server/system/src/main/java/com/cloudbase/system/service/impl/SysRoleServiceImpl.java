package com.cloudbase.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudbase.system.entity.SysRole;
import com.cloudbase.system.entity.SysRoleMenu;
import com.cloudbase.system.mapper.SysRoleMapper;
import com.cloudbase.system.mapper.SysRoleMenuMapper;
import com.cloudbase.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (role != null) {
            if (role.getRoleName() != null && !role.getRoleName().isEmpty()) {
                wrapper.like(SysRole::getRoleName, role.getRoleName());
            }
            if (role.getRoleKey() != null && !role.getRoleKey().isEmpty()) {
                wrapper.like(SysRole::getRoleKey, role.getRoleKey());
            }
            if (role.getStatus() != null) {
                wrapper.eq(SysRole::getStatus, role.getStatus());
            }
        }
        wrapper.eq(SysRole::getDeleted, 0);
        wrapper.orderByAsc(SysRole::getRoleSort);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            List<SysRoleMenu> roleMenus = menuIds.stream().map(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                return rm;
            }).collect(Collectors.toList());
            roleMenuMapper.insertBatch(roleMenus);
        }
    }
}
