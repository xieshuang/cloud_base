package com.cloudbase.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudbase.common.core.query.PageQuery;
import com.cloudbase.common.log.entity.SysLog;
import com.cloudbase.system.mapper.SysLogMapper;
import com.cloudbase.system.service.ISysLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements ISysLogService {

    @Override
    public void saveLog(SysLog log) {
        baseMapper.insert(log);
    }

    @Override
    public List<SysLog> selectLogList(SysLog log) {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if (log != null) {
            if (log.getUsername() != null && !log.getUsername().isEmpty()) {
                wrapper.like(SysLog::getUsername, log.getUsername());
            }
            if (log.getModule() != null && !log.getModule().isEmpty()) {
                wrapper.like(SysLog::getModule, log.getModule());
            }
            if (log.getLogType() != null) {
                wrapper.eq(SysLog::getLogType, log.getLogType());
            }
            if (log.getStatus() != null) {
                wrapper.eq(SysLog::getStatus, log.getStatus());
            }
            if (log.getIp() != null && !log.getIp().isEmpty()) {
                wrapper.like(SysLog::getIp, log.getIp());
            }
        }
        wrapper.orderByDesc(SysLog::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public void deleteLogByIds(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public void cleanLog() {
        baseMapper.cleanLog();
    }
}
