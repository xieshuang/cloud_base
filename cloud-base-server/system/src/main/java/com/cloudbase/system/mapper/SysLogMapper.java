package com.cloudbase.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudbase.common.log.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {

    void cleanLog();
}
