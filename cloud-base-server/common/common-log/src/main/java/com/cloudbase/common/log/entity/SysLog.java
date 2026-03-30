package com.cloudbase.common.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_log")
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String module;

    private String content;

    private String method;

    private String requestMethod;

    private String requestUrl;

    private String requestParams;

    private String responseResult;

    private Integer logType;

    private String ip;

    private String location;

    private String userAgent;

    private Integer status;

    private String errorMsg;

    private Long executionTime;

    private LocalDateTime createTime;
}
