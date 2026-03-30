package com.cloudbase.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudbase.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {

    private Long dictId;
    private String dictLabel;
    private String dictValue;
    private Integer dictSort;
    private Integer status;
}
