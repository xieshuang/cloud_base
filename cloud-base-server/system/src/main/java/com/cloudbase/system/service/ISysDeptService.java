package com.cloudbase.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudbase.system.entity.SysDept;

import java.util.List;

public interface ISysDeptService extends IService<SysDept> {

    List<SysDept> selectDeptList(SysDept dept);

    List<SysDept> buildDeptTree(List<SysDept> depts);

    SysDept selectDeptById(Long id);

    boolean hasChild(Long deptId);

    boolean checkDeptExistUsers(Long deptId);

    void addDept(SysDept dept);

    void updateDept(SysDept dept);

    void deleteDept(Long id);
}
