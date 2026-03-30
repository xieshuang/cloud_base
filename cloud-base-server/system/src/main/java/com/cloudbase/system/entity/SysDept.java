package com.cloudbase.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudbase.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    private Long parentId;
    private String deptName;
    private String deptCode;
    private String leader;
    private String phone;
    private String email;
    private Integer orderNum;
    private Integer status;

    @TableField(exist = false)
    private List<SysDept> children;
}
