package com.cloudbase.system.controller;

import com.cloudbase.common.core.domain.PageResult;
import com.cloudbase.common.core.query.PageQuery;
import com.cloudbase.common.core.result.Result;
import com.cloudbase.common.log.annotation.OperLog;
import com.cloudbase.common.log.entity.SysLog;
import com.cloudbase.common.security.annotation.RequiresPermissions;
import com.cloudbase.system.service.ISysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "日志管理")
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class SysLogController {

    private final ISysLogService logService;

    @ApiOperation("获取操作日志列表")
    @GetMapping("/oper/list")
    @RequiresPermissions("log:oper:list")
    public Result<PageResult<SysLog>> operList(SysLog log, PageQuery pageQuery) {
        List<SysLog> list = logService.selectLogList(log);
        long total = list.size();
        int fromIndex = (int) ((pageQuery.getCurrent() - 1) * pageQuery.getSize());
        int toIndex = (int) Math.min(fromIndex + pageQuery.getSize(), list.size());
        List<SysLog> pageRecords = list.subList(fromIndex, toIndex);
        return Result.success(new PageResult<>(pageRecords, total, pageQuery.getCurrent(), pageQuery.getSize()));
    }

    @ApiOperation("获取登录日志列表")
    @GetMapping("/login/list")
    @RequiresPermissions("log:login:list")
    public Result<PageResult<SysLog>> loginList(SysLog log, PageQuery pageQuery) {
        log.setLogType(0);
        List<SysLog> list = logService.selectLogList(log);
        long total = list.size();
        int fromIndex = (int) ((pageQuery.getCurrent() - 1) * pageQuery.getSize());
        int toIndex = (int) Math.min(fromIndex + pageQuery.getSize(), list.size());
        List<SysLog> pageRecords = list.subList(fromIndex, toIndex);
        return Result.success(new PageResult<>(pageRecords, total, pageQuery.getCurrent(), pageQuery.getSize()));
    }

    @ApiOperation("删除日志")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("log:oper:delete")
    @OperLog(module = "日志管理", content = "删除日志")
    public Result<?> remove(@PathVariable List<Long> ids) {
        logService.deleteLogByIds(ids);
        return Result.success("删除成功");
    }

    @ApiOperation("清空日志")
    @DeleteMapping("/clean")
    @RequiresPermissions("log:oper:delete")
    @OperLog(module = "日志管理", content = "清空日志")
    public Result<?> clean() {
        logService.cleanLog();
        return Result.success("清空成功");
    }
}
