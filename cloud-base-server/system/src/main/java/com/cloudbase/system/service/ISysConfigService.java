package com.cloudbase.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudbase.system.entity.SysConfig;

import java.util.List;

public interface ISysConfigService extends IService<SysConfig> {

    List<SysConfig> selectConfigList(SysConfig config);

    String selectConfigByKey(String configKey);

    boolean isConfigKeyExist(String configKey, Long excludeId);

    void addConfig(SysConfig config);

    void updateConfig(SysConfig config);

    void deleteConfig(Long id);

    void deleteConfigByKeys(List<String> keys);
}
