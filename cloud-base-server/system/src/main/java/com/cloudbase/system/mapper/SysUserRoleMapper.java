package com.cloudbase.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudbase.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    int deleteByUserId(@Param("userId") Long userId);

    int deleteByRoleId(@Param("roleId") Long roleId);

    int insertBatch(@Param("list") List<SysUserRole> list);
}
