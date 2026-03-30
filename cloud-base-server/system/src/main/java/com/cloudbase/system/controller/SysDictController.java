package com.cloudbase.system.controller;

import com.cloudbase.common.core.result.Result;
import com.cloudbase.common.log.annotation.OperLog;
import com.cloudbase.common.security.annotation.RequiresPermissions;
import com.cloudbase.system.entity.SysDict;
import com.cloudbase.system.service.ISysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "字典管理")
@RestController
@RequestMapping("/system/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final ISysDictService dictService;

    @ApiOperation("获取字典列表")
    @GetMapping("/list")
    @RequiresPermissions("system:dict:list")
    public Result<List<SysDict>> list(SysDict dict) {
        return Result.success(dictService.selectDictList(dict));
    }

    @ApiOperation("获取字典详情")
    @GetMapping("/{id}")
    @RequiresPermissions("system:dict:list")
    public Result<SysDict> getInfo(@PathVariable Long id) {
        return Result.success(dictService.getById(id));
    }

    @ApiOperation("新增字典")
    @PostMapping
    @RequiresPermissions("system:dict:add")
    @OperLog(module = "字典管理", content = "新增字典")
    public Result<?> add(@RequestBody SysDict dict) {
        dictService.addDict(dict);
        return Result.success("新增成功");
    }

    @ApiOperation("修改字典")
    @PutMapping
    @RequiresPermissions("system:dict:edit")
    @OperLog(module = "字典管理", content = "修改字典")
    public Result<?> edit(@RequestBody SysDict dict) {
        dictService.updateDict(dict);
        return Result.success("修改成功");
    }

    @ApiOperation("删除字典")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("system:dict:delete")
    @OperLog(module = "字典管理", content = "删除字典")
    public Result<?> remove(@PathVariable List<Long> ids) {
        ids.forEach(id -> dictService.deleteDict(id));
        return Result.success("删除成功");
    }
}
