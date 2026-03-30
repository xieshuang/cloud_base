package com.cloudbase.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudbase.system.entity.SysMenu;

import java.util.List;

public interface ISysMenuService extends IService<SysMenu> {

    List<SysMenu> selectMenuList(SysMenu menu);

    List<SysMenu> selectMenuListByUserId(Long userId);

    List<SysMenu> selectMenuTreeByUserId(Long userId);

    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    List<String> selectPermsByUserId(Long userId);

    SysMenu selectMenuById(Long id);

    boolean hasChild(Long menuId);

    boolean checkMenuExistRole(Long menuId);

    boolean isMenuExist(String menuName, Long parentId, Long excludeId);

    void addMenu(SysMenu menu);

    void updateMenu(SysMenu menu);

    void deleteMenu(Long id);
}
