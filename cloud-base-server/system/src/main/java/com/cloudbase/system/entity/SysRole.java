package com.cloudbase.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudbase.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    private String roleName;
    private String roleKey;
    private Integer roleSort;
    private String dataScope;
    private Integer status;
}
