package com.cloudbase.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudbase.common.core.constant.Constants;
import com.cloudbase.common.exception.BusinessException;
import com.cloudbase.system.entity.SysMenu;
import com.cloudbase.system.mapper.SysMenuMapper;
import com.cloudbase.system.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (menu != null) {
            if (menu.getMenuName() != null && !menu.getMenuName().isEmpty()) {
                wrapper.like(SysMenu::getMenuName, menu.getMenuName());
            }
            if (menu.getVisible() != null) {
                wrapper.eq(SysMenu::getVisible, menu.getVisible());
            }
            if (menu.getStatus() != null) {
                wrapper.eq(SysMenu::getStatus, menu.getStatus());
            }
        }
        wrapper.eq(SysMenu::getDeleted, 0);
        wrapper.orderByAsc(SysMenu::getOrderNum);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<SysMenu> selectMenuListByUserId(Long userId) {
        return baseMapper.selectMenuListByUserId(userId);
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = baseMapper.selectMenuTreeByUserId(userId);
        return buildMenuTree(menus);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> result = new ArrayList<>();
        List<SysMenu> rootMenus = menus.stream()
                .filter(m -> m.getParentId() == 0)
                .collect(Collectors.toList());
        for (SysMenu root : rootMenus) {
            root = buildChildren(root, menus);
            result.add(root);
        }
        return result;
    }

    private SysMenu buildChildren(SysMenu parent, List<SysMenu> allMenus) {
        List<SysMenu> children = allMenus.stream()
                .filter(m -> m.getParentId().equals(parent.getId()))
                .collect(Collectors.toList());
        for (SysMenu child : children) {
            buildChildren(child, allMenus);
        }
        parent.setChildren(children);
        return parent;
    }

    @Override
    public List<String> selectPermsByUserId(Long userId) {
        return baseMapper.selectPermsByUserId(userId);
    }

    @Override
    public SysMenu selectMenuById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean hasChild(Long menuId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, menuId);
        wrapper.eq(SysMenu::getDeleted, 0);
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean checkMenuExistRole(Long menuId) {
        return false;
    }

    @Override
    public boolean isMenuExist(String menuName, Long parentId, Long excludeId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuName, menuName);
        wrapper.eq(SysMenu::getParentId, parentId);
        wrapper.eq(SysMenu::getDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(SysMenu::getId, excludeId);
        }
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMenu(SysMenu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        baseMapper.insert(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(SysMenu menu) {
        baseMapper.updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        SysMenu menu = new SysMenu();
        menu.setId(id);
        menu.setDeleted(1);
        baseMapper.updateById(menu);
    }
}
