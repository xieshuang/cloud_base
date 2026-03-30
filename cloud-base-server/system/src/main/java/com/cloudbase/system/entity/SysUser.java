package com.cloudbase.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudbase.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer sex;
    private Integer status;
    private Long deptId;
    private String lastLoginIp;
    private java.time.LocalDateTime lastLoginTime;
    private Integer loginCount;

    @TableField(exist = false)
    private List<Long> roleIds;
}
