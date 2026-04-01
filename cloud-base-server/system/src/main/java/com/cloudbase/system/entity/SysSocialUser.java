package com.cloudbase.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_social_user")
public class SysSocialUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer socialType;

    private String openid;

    private String unionid;

    private String nickname;

    private String avatar;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public static final int SOCIAL_TYPE_WEI_XIN_MINI = 1;
    public static final int SOCIAL_TYPE_WEI_XIN_MP = 2;
}