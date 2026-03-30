package com.cloudbase.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudbase.system.entity.SysDict;

import java.util.List;

public interface ISysDictService extends IService<SysDict> {

    List<SysDict> selectDictList(SysDict dict);

    boolean isDictCodeExist(String dictCode, Long excludeId);

    void addDict(SysDict dict);

    void updateDict(SysDict dict);

    void deleteDict(Long id);
}
