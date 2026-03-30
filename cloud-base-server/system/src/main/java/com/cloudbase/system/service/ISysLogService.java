package com.cloudbase.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudbase.common.log.entity.SysLog;

import java.util.List;

public interface ISysLogService extends IService<SysLog> {

    void saveLog(SysLog log);

    List<SysLog> selectLogList(SysLog log);

    void deleteLogByIds(List<Long> ids);

    void cleanLog();
}
