-- Cloud Base 系统基础表结构
-- 数据库: cloud_base
-- 创建时间: 2024

CREATE DATABASE IF NOT EXISTS `cloud_base` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `cloud_base`;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `sex` TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用, 2-锁定',
  `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
  `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `login_count` INT DEFAULT 0 COMMENT '登录次数',
  `create_by` VARCHAR(50) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(50) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除, 1-已删除',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 2. 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_key` VARCHAR(50) NOT NULL COMMENT '角色标识',
  `role_sort` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `data_scope` VARCHAR(50) DEFAULT '1' COMMENT '数据权限范围',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_by` VARCHAR(50) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(50) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除, 1-已删除',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- 3. 菜单权限表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `path` VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
  `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  `menu_type` TINYINT NOT NULL DEFAULT 1 COMMENT '菜单类型: 0-目录, 1-菜单, 2-按钮',
  `visible` TINYINT NOT NULL DEFAULT 0 COMMENT '菜单状态: 0-显示, 1-隐藏',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '菜单状态: 0-禁用, 1-启用',
  `perms` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
  `icon` VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
  `order_num` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `create_by` VARCHAR(50) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(50) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除, 1-已删除',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_perms` (`perms`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- ----------------------------
-- 4. 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';

-- ----------------------------
-- 5. 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和菜单关联表';

-- ----------------------------
-- 6. 部门表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
  `dept_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
  `dept_code` VARCHAR(50) DEFAULT NULL COMMENT '部门编码',
  `leader` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `order_num` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '部门状态: 0-禁用, 1-启用',
  `create_by` VARCHAR(50) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(50) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除, 1-已删除',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- ----------------------------
-- 7. 字典表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称',
  `dict_code` VARCHAR(100) NOT NULL COMMENT '字典编码',
  `dict_type` VARCHAR(20) NOT NULL DEFAULT 'string' COMMENT '字典类型',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_by` VARCHAR(50) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(50) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除, 1-已删除',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_code` (`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典表';

-- ----------------------------
-- 8. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
  `dict_id` BIGINT NOT NULL COMMENT '字典ID',
  `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
  `dict_value` VARCHAR(100) NOT NULL COMMENT '字典键值',
  `dict_sort` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_by` VARCHAR(50) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(50) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除, 1-已删除',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_dict_id` (`dict_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- ----------------------------
-- 9. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `module` VARCHAR(100) DEFAULT NULL COMMENT '操作模块',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '操作内容',
  `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
  `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方式',
  `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
  `request_params` TEXT COMMENT '请求参数',
  `response_result` TEXT COMMENT '响应结果',
  `log_type` TINYINT NOT NULL DEFAULT 1 COMMENT '日志类型: 0-登录, 1-操作, 2-异常',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '操作地点',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-成功, 1-失败',
  `error_msg` TEXT COMMENT '错误信息',
  `execution_time` BIGINT DEFAULT NULL COMMENT '执行时长(ms)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- ----------------------------
-- 10. 参数配置表
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '参数ID',
  `config_name` VARCHAR(100) NOT NULL COMMENT '参数名称',
  `config_key` VARCHAR(100) NOT NULL COMMENT '参数键名',
  `config_value` VARCHAR(500) NOT NULL COMMENT '参数键值',
  `config_type` VARCHAR(20) NOT NULL DEFAULT 'string' COMMENT '参数类型',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_by` VARCHAR(50) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(50) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除, 1-已删除',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='参数配置表';

-- ----------------------------
-- 初始数据
-- ----------------------------

-- 插入超级管理员角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `status`, `remark`) VALUES
(1, '超级管理员', 'super_admin', 1, '1', 1, '拥有所有权限');

-- 插入默认菜单
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `order_num`, `remark`) VALUES
(1, '系统管理', 0, '/system', 'Layout', 0, 0, 1, NULL, 'setting', 1, '系统管理目录'),
(2, '用户管理', 1, '/system/user', 'system/user/index', 1, 0, 1, 'system:user:list', 'user', 1, ''),
(3, '角色管理', 1, '/system/role', 'system/role/index', 1, 0, 1, 'system:role:list', 'peoples', 2, ''),
(4, '菜单管理', 1, '/system/menu', 'system/menu/index', 1, 0, 1, 'system:menu:list', 'tree-table', 3, ''),
(5, '部门管理', 1, '/system/dept', 'system/dept/index', 1, 0, 1, 'system:dept:list', 'tree', 4, ''),
(6, '日志管理', 0, '/logs', 'Layout', 0, 0, 1, NULL, 'log', 10, '日志管理目录'),
(7, '操作日志', 6, '/logs/oper', 'logs/oper/index', 1, 0, 1, 'log:oper:list', 'form', 1, ''),
(8, '登录日志', 6, '/logs/login', 'logs/login/index', 1, 0, 1, 'log:login:list', 'login', 2, '');

-- 插入按钮权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `menu_type`, `visible`, `status`, `perms`, `order_num`) VALUES
('用户新增', 2, 2, 0, 1, 'system:user:add', 1),
('用户编辑', 2, 2, 0, 1, 'system:user:edit', 2),
('用户删除', 2, 2, 0, 1, 'system:user:delete', 3),
('重置密码', 2, 2, 0, 1, 'system:user:resetPwd', 4),
('角色新增', 3, 2, 0, 1, 'system:role:add', 1),
('角色编辑', 3, 2, 0, 1, 'system:role:edit', 2),
('角色删除', 3, 2, 0, 1, 'system:role:delete', 3),
('菜单新增', 4, 2, 0, 1, 'system:menu:add', 1),
('菜单编辑', 4, 2, 0, 1, 'system:menu:edit', 2),
('菜单删除', 4, 2, 0, 1, 'system:menu:delete', 3);

-- 插入默认部门
INSERT INTO `sys_dept` (`id`, `parent_id`, `dept_name`, `dept_code`, `order_num`, `status`, `remark`) VALUES
(1, 0, '总公司', 'HQ', 0, 1, '总公司'),
(2, 1, '研发部', 'RD', 1, 1, '研发部门'),
(3, 1, '市场部', 'MKT', 2, 1, '市场部门'),
(4, 2, '开发组', 'DEV', 1, 1, '开发组'),
(5, 2, '测试组', 'QA', 2, 1, '测试组');

-- 插入默认用户 (密码: admin123)
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `status`, `dept_id`, `create_by`, `remark`) VALUES
(1, 'admin', '$2a$10$9rHBaCFU4VYwzBp33jy/fO.lLqipHfFva52vrhdtke5/lZ6tRB16G', '管理员', 'admin@cloudbase.com', '13800138000', 1, 1, 'system', '超级管理员账号');

-- 关联管理员和角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 关联角色和菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8);
