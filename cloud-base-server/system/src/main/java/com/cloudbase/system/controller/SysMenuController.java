package com.cloudbase.system.controller;

import com.cloudbase.common.core.result.Result;
import com.cloudbase.common.log.annotation.OperLog;
import com.cloudbase.common.security.annotation.RequiresPermissions;
import com.cloudbase.system.entity.SysMenu;
import com.cloudbase.system.service.ISysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final ISysMenuService menuService;

    @ApiOperation("获取菜单列表")
    @GetMapping("/list")
    @RequiresPermissions("system:menu:list")
    public Result<List<SysMenu>> list(SysMenu menu) {
        return Result.success(menuService.selectMenuList(menu));
    }

    @ApiOperation("获取菜单树")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        return Result.success(menuService.buildMenuTree(menuService.list()));
    }

    @ApiOperation("获取角色菜单树")
    @GetMapping("/roleTree")
    public Result<List<SysMenu>> roleTree() {
        return Result.success(menuService.buildMenuTree(menuService.list()));
    }

    @ApiOperation("获取用户菜单树")
    @GetMapping("/userTree")
    public Result<List<SysMenu>> userTree() {
        return Result.success(menuService.selectMenuTreeByUserId(1L));
    }

    @ApiOperation("获取菜单详情")
    @GetMapping("/{id}")
    @RequiresPermissions("system:menu:list")
    public Result<SysMenu> getInfo(@PathVariable Long id) {
        return Result.success(menuService.selectMenuById(id));
    }

    @ApiOperation("新增菜单")
    @PostMapping
    @RequiresPermissions("system:menu:add")
    @OperLog(module = "菜单管理", content = "新增菜单")
    public Result<?> add(@RequestBody SysMenu menu) {
        menuService.addMenu(menu);
        return Result.success("新增成功");
    }

    @ApiOperation("修改菜单")
    @PutMapping
    @RequiresPermissions("system:menu:edit")
    @OperLog(module = "菜单管理", content = "修改菜单")
    public Result<?> edit(@RequestBody SysMenu menu) {
        menuService.updateMenu(menu);
        return Result.success("修改成功");
    }

    @ApiOperation("删除菜单")
    @DeleteMapping("/{id}")
    @RequiresPermissions("system:menu:delete")
    @OperLog(module = "菜单管理", content = "删除菜单")
    public Result<?> remove(@PathVariable Long id) {
        if (menuService.hasChild(id)) {
            return Result.error("存在子菜单，无法删除");
        }
        menuService.deleteMenu(id);
        return Result.success("删除成功");
    }

    @ApiOperation("获取菜单下拉树")
    @GetMapping("/selectTree")
    public Result<List<SysMenu>> selectTree() {
        List<SysMenu> menus = menuService.selectMenuList(null);
        SysMenu root = new SysMenu();
        root.setId(0L);
        root.setMenuName("顶级菜单");
        root.setParentId(-1L);
        menus.add(0, root);
        return Result.success(menuService.buildMenuTree(menus));
    }
}
