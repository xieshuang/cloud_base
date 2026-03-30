package com.cloudbase.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudbase.system.entity.SysDict;
import com.cloudbase.system.mapper.SysDictMapper;
import com.cloudbase.system.service.ISysDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

    @Override
    public List<SysDict> selectDictList(SysDict dict) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        if (dict != null) {
            if (dict.getDictName() != null && !dict.getDictName().isEmpty()) {
                wrapper.like(SysDict::getDictName, dict.getDictName());
            }
            if (dict.getDictCode() != null && !dict.getDictCode().isEmpty()) {
                wrapper.like(SysDict::getDictCode, dict.getDictCode());
            }
            if (dict.getStatus() != null) {
                wrapper.eq(SysDict::getStatus, dict.getStatus());
            }
        }
        wrapper.eq(SysDict::getDeleted, 0);
        wrapper.orderByDesc(SysDict::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean isDictCodeExist(String dictCode, Long excludeId) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDictCode, dictCode);
        wrapper.eq(SysDict::getDeleted, 0);
        if (excludeId != null) {
            wrapper.ne(SysDict::getId, excludeId);
        }
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDict(SysDict dict) {
        baseMapper.insert(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDict(SysDict dict) {
        baseMapper.updateById(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDict(Long id) {
        SysDict dict = new SysDict();
        dict.setId(id);
        dict.setDeleted(1);
        baseMapper.updateById(dict);
    }
}
