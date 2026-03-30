package com.cloudbase.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudbase.system.entity.SysDept;
import com.cloudbase.system.mapper.SysDeptMapper;
import com.cloudbase.system.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        if (dept != null) {
            if (dept.getDeptName() != null && !dept.getDeptName().isEmpty()) {
                wrapper.like(SysDept::getDeptName, dept.getDeptName());
            }
            if (dept.getStatus() != null) {
                wrapper.eq(SysDept::getStatus, dept.getStatus());
            }
        }
        wrapper.eq(SysDept::getDeleted, 0);
        wrapper.orderByAsc(SysDept::getOrderNum);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> result = new ArrayList<>();
        List<SysDept> rootDepts = depts.stream()
                .filter(d -> d.getParentId() == 0)
                .collect(Collectors.toList());
        for (SysDept root : rootDepts) {
            root = buildChildren(root, depts);
            result.add(root);
        }
        return result;
    }

    private SysDept buildChildren(SysDept parent, List<SysDept> allDepts) {
        List<SysDept> children = allDepts.stream()
                .filter(d -> d.getParentId().equals(parent.getId()))
                .collect(Collectors.toList());
        for (SysDept child : children) {
            buildChildren(child, allDepts);
        }
        parent.setChildren(children);
        return parent;
    }

    @Override
    public SysDept selectDeptById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean hasChild(Long deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, deptId);
        wrapper.eq(SysDept::getDeleted, 0);
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean checkDeptExistUsers(Long deptId) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDept(SysDept dept) {
        if (dept.getParentId() == null) {
            dept.setParentId(0L);
        }
        baseMapper.insert(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(SysDept dept) {
        baseMapper.updateById(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long id) {
        SysDept dept = new SysDept();
        dept.setId(id);
        dept.setDeleted(1);
        baseMapper.updateById(dept);
    }
}
