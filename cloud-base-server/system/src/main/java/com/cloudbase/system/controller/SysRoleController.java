package com.cloudbase.system.controller;

import com.cloudbase.common.core.result.Result;
import com.cloudbase.common.log.annotation.OperLog;
import com.cloudbase.common.security.annotation.RequiresPermissions;
import com.cloudbase.system.entity.SysRole;
import com.cloudbase.system.service.ISysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final ISysRoleService roleService;

    @ApiOperation("获取角色列表")
    @GetMapping("/list")
    @RequiresPermissions("system:role:list")
    public Result<List<SysRole>> list(SysRole role) {
        return Result.success(roleService.selectRoleList(role));
    }

    @ApiOperation("获取角色详情")
    @GetMapping("/{id}")
    @RequiresPermissions("system:role:list")
    public Result<SysRole> getInfo(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @ApiOperation("新增角色")
    @PostMapping
    @RequiresPermissions("system:role:add")
    @OperLog(module = "角色管理", content = "新增角色")
    public Result<?> add(@RequestBody SysRole role) {
        roleService.save(role);
        return Result.success("新增成功");
    }

    @ApiOperation("修改角色")
    @PutMapping
    @RequiresPermissions("system:role:edit")
    @OperLog(module = "角色管理", content = "修改角色")
    public Result<?> edit(@RequestBody SysRole role) {
        roleService.updateById(role);
        return Result.success("修改成功");
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("system:role:delete")
    @OperLog(module = "角色管理", content = "删除角色")
    public Result<?> remove(@PathVariable List<Long> ids) {
        roleService.removeByIds(ids);
        return Result.success("删除成功");
    }

    @ApiOperation("修改角色状态")
    @PutMapping("/changeStatus")
    @RequiresPermissions("system:role:edit")
    public Result<?> changeStatus(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        SysRole role = new SysRole();
        role.setId(roleId);
        role.setStatus(status);
        roleService.updateById(role);
        return Result.success("状态修改成功");
    }

    @ApiOperation("获取角色菜单ID列表")
    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long id) {
        return Result.success(roleService.selectMenuIdsByRoleId(id));
    }

    @ApiOperation("分配角色菜单权限")
    @PutMapping("/{id}/menus")
    @RequiresPermissions("system:role:edit")
    @OperLog(module = "角色管理", content = "分配权限")
    public Result<?> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.success("权限分配成功");
    }
}
