package com.cloudbase.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudbase.system.entity.SysConfig;
import com.cloudbase.system.mapper.SysConfigMapper;
import com.cloudbase.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        if (config != null) {
            if (config.getConfigName() != null && !config.getConfigName().isEmpty()) {
                wrapper.like(SysConfig::getConfigName, config.getConfigName());
            }
            if (config.getConfigKey() != null && !config.getConfigKey().isEmpty()) {
                wrapper.like(SysConfig::getConfigKey, config.getConfigKey());
            }
            if (config.getStatus() != null) {
                wrapper.eq(SysConfig::getStatus, config.getStatus());
            }
        }
        wrapper.eq(SysConfig::getDeleted, 0);
        wrapper.orderByDesc(SysConfig::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public String selectConfigByKey(String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        wrapper.eq(SysConfig::getDeleted, 0);
        wrapper.eq(SysConfig::getStatus, 1);
        SysConfig config = baseMapper.selectOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public boolean isConfigKeyExist(String configKey, Long excludeId) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        wrapper.eq(SysConfig::getDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(SysConfig::getId, excludeId);
        }
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addConfig(SysConfig config) {
        baseMapper.insert(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(SysConfig config) {
        baseMapper.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        SysConfig config = new SysConfig();
        config.setId(id);
        config.setDeleted(1);
        baseMapper.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfigByKeys(List<String> keys) {
        keys.forEach(key -> {
            LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysConfig::getConfigKey, key);
            SysConfig config = baseMapper.selectOne(wrapper);
            if (config != null) {
                config.setDeleted(1);
                baseMapper.updateById(config);
            }
        });
    }
}
