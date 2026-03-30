package com.cloudbase.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudbase.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private String menuName;
    private Long parentId;
    private String path;
    private String component;
    private Integer menuType;
    private Integer visible;
    private Integer status;
    private String perms;
    private String icon;
    private Integer orderNum;

    @TableField(exist = false)
    private List<SysMenu> children;

    @TableField(exist = false)
    private String remark;
}
