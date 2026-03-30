package com.cloudbase.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudbase.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectMenuListByUserId(@Param("userId") Long userId);

    List<SysMenu> selectMenuTreeByUserId(@Param("userId") Long userId);

    List<String> selectPermsByUserId(@Param("userId") Long userId);
}
