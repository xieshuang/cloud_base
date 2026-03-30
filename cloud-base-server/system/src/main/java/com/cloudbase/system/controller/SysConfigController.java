package com.cloudbase.system.controller;

import com.cloudbase.common.core.result.Result;
import com.cloudbase.common.log.annotation.OperLog;
import com.cloudbase.common.security.annotation.RequiresPermissions;
import com.cloudbase.system.entity.SysConfig;
import com.cloudbase.system.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "参数配置")
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final ISysConfigService configService;

    @ApiOperation("获取参数列表")
    @GetMapping("/list")
    @RequiresPermissions("system:config:list")
    public Result<List<SysConfig>> list(SysConfig config) {
        return Result.success(configService.selectConfigList(config));
    }

    @ApiOperation("获取参数详情")
    @GetMapping("/{id}")
    @RequiresPermissions("system:config:list")
    public Result<SysConfig> getInfo(@PathVariable Long id) {
        return Result.success(configService.getById(id));
    }

    @ApiOperation("根据键名查询参数")
    @GetMapping("/configKey/{configKey}")
    public Result<String> getConfigByKey(@PathVariable String configKey) {
        return Result.success(configService.selectConfigByKey(configKey));
    }

    @ApiOperation("新增参数")
    @PostMapping
    @RequiresPermissions("system:config:add")
    @OperLog(module = "参数配置", content = "新增参数")
    public Result<?> add(@RequestBody SysConfig config) {
        configService.addConfig(config);
        return Result.success("新增成功");
    }

    @ApiOperation("修改参数")
    @PutMapping
    @RequiresPermissions("system:config:edit")
    @OperLog(module = "参数配置", content = "修改参数")
    public Result<?> edit(@RequestBody SysConfig config) {
        configService.updateConfig(config);
        return Result.success("修改成功");
    }

    @ApiOperation("删除参数")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("system:config:delete")
    @OperLog(module = "参数配置", content = "删除参数")
    public Result<?> remove(@PathVariable List<Long> ids) {
        ids.forEach(id -> configService.deleteConfig(id));
        return Result.success("删除成功");
    }
}
