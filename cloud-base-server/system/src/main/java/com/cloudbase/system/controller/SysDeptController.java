package com.cloudbase.system.controller;

import com.cloudbase.common.core.result.Result;
import com.cloudbase.common.log.annotation.OperLog;
import com.cloudbase.common.security.annotation.RequiresPermissions;
import com.cloudbase.system.entity.SysDept;
import com.cloudbase.system.service.ISysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final ISysDeptService deptService;

    @ApiOperation("获取部门列表")
    @GetMapping("/list")
    @RequiresPermissions("system:dept:list")
    public Result<List<SysDept>> list(SysDept dept) {
        return Result.success(deptService.selectDeptList(dept));
    }

    @ApiOperation("获取部门树")
    @GetMapping("/tree")
    public Result<List<SysDept>> tree() {
        return Result.success(deptService.buildDeptTree(deptService.list()));
    }

    @ApiOperation("获取部门详情")
    @GetMapping("/{id}")
    @RequiresPermissions("system:dept:list")
    public Result<SysDept> getInfo(@PathVariable Long id) {
        return Result.success(deptService.selectDeptById(id));
    }

    @ApiOperation("新增部门")
    @PostMapping
    @RequiresPermissions("system:dept:add")
    @OperLog(module = "部门管理", content = "新增部门")
    public Result<?> add(@RequestBody SysDept dept) {
        deptService.addDept(dept);
        return Result.success("新增成功");
    }

    @ApiOperation("修改部门")
    @PutMapping
    @RequiresPermissions("system:dept:edit")
    @OperLog(module = "部门管理", content = "修改部门")
    public Result<?> edit(@RequestBody SysDept dept) {
        deptService.updateDept(dept);
        return Result.success("修改成功");
    }

    @ApiOperation("删除部门")
    @DeleteMapping("/{id}")
    @RequiresPermissions("system:dept:delete")
    @OperLog(module = "部门管理", content = "删除部门")
    public Result<?> remove(@PathVariable Long id) {
        if (deptService.hasChild(id)) {
            return Result.error("存在子部门，无法删除");
        }
        deptService.deleteDept(id);
        return Result.success("删除成功");
    }
}
