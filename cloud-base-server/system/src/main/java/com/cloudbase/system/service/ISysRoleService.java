package com.cloudbase.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudbase.system.entity.SysRole;

import java.util.List;

public interface ISysRoleService extends IService<SysRole> {

    List<SysRole> selectRoleList(SysRole role);

    List<Long> selectMenuIdsByRoleId(Long roleId);

    void assignMenus(Long roleId, List<Long> menuIds);
}
