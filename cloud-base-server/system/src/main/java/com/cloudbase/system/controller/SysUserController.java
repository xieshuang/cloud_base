package com.cloudbase.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudbase.common.core.domain.PageResult;
import com.cloudbase.common.core.query.PageQuery;
import com.cloudbase.common.core.result.Result;
import com.cloudbase.common.log.annotation.OperLog;
import com.cloudbase.common.security.annotation.RequiresPermissions;
import com.cloudbase.system.entity.SysUser;
import com.cloudbase.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final ISysUserService userService;

    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    @RequiresPermissions("system:user:list")
    public Result<PageResult<SysUser>> list(SysUser user, PageQuery pageQuery) {
        Page<SysUser> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        List<SysUser> list = userService.selectUserList(user);
        long total = list.size();
        int fromIndex = (int) ((pageQuery.getCurrent() - 1) * pageQuery.getSize());
        int toIndex = (int) Math.min(fromIndex + pageQuery.getSize(), list.size());
        List<SysUser> pageRecords = list.subList(fromIndex, toIndex);
        return Result.success(new PageResult<>(pageRecords, total, pageQuery.getCurrent(), pageQuery.getSize()));
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    @RequiresPermissions("system:user:list")
    public Result<SysUser> getInfo(@PathVariable Long id) {
        SysUser user = userService.selectById(id);
        user.setPassword(null);
        List<Long> roleIds = userService.selectRoleIdsByUserId(id);
        return Result.success(user);
    }

    @ApiOperation("新增用户")
    @PostMapping
    @RequiresPermissions("system:user:add")
    @OperLog(module = "用户管理", content = "新增用户")
    public Result<?> add(@RequestBody SysUser user) {
        if (userService.isUsernameExist(user.getUsername(), null)) {
            return Result.error("用户名已存在");
        }
        user.setPassword(userService.encodePassword("admin123"));
        userService.save(user);
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            userService.assignRoles(user.getId(), user.getRoleIds());
        }
        return Result.success("新增成功");
    }

    @ApiOperation("修改用户")
    @PutMapping
    @RequiresPermissions("system:user:edit")
    @OperLog(module = "用户管理", content = "修改用户")
    public Result<?> edit(@RequestBody SysUser user) {
        if (userService.isUsernameExist(user.getUsername(), user.getId())) {
            return Result.error("用户名已存在");
        }
        userService.updateById(user);
        userService.assignRoles(user.getId(), user.getRoleIds());
        return Result.success("修改成功");
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("system:user:delete")
    @OperLog(module = "用户管理", content = "删除用户")
    public Result<?> remove(@PathVariable List<Long> ids) {
        userService.removeByIds(ids);
        return Result.success("删除成功");
    }

    @ApiOperation("重置用户密码")
    @PutMapping("/resetPwd")
    @RequiresPermissions("system:user:resetPwd")
    @OperLog(module = "用户管理", content = "重置密码")
    public Result<?> resetPwd(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String password = params.get("password").toString();
        userService.resetPassword(userId, password);
        return Result.success("密码重置成功，新密码：" + password);
    }

    @ApiOperation("修改用户状态")
    @PutMapping("/changeStatus")
    @RequiresPermissions("system:user:edit")
    @OperLog(module = "用户管理", content = "修改状态")
    public Result<?> changeStatus(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        userService.changeStatus(userId, status);
        return Result.success("状态修改成功");
    }

    @ApiOperation("获取用户角色ID列表")
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getUserRoles(@PathVariable Long id) {
        return Result.success(userService.selectRoleIdsByUserId(id));
    }
}
