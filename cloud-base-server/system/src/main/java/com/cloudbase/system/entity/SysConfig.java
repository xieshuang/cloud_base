package com.cloudbase.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudbase.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    private String configName;
    private String configKey;
    private String configValue;
    private String configType;
    private Integer status;
}
