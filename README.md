# Cloud Base - Web系统通用脚手架

## 项目简介

基于 Spring Boot + Vue3 的通用后台管理系统脚手架，包含完善的用户、角色、权限、日志管理等基础功能。

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.18
- **ORM**: MyBatis-Plus 3.5.3
- **安全**: Shiro 1.11 + JWT
- **文档**: Swagger 3.0
- **工具**: Hutool, Lombok, FastJSON

### 前端
- **框架**: Vue3
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **UI**: Element Plus
- **构建**: Vite

## 功能模块

| 模块 | 功能 |
|------|------|
| 用户管理 | 用户CRUD、密码重置、状态启用/禁用 |
| 角色管理 | 角色CRUD、权限分配 |
| 菜单管理 | 菜单CRUD、树形结构 |
| 部门管理 | 部门CRUD、树形结构 |
| 字典管理 | 字典CRUD |
| 参数配置 | 参数CRUD |
| 操作日志 | 日志查询、删除、清空 |
| 登录日志 | 登录记录查询 |

## 快速开始

### 1. 初始化数据库

```bash
mysql -u root -p < cloud-base-server/src/main/resources/sql/init.sql
```

### 2. 修改数据库配置

编辑 `cloud-base-server/src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cloud_base
    username: your_username
    password: your_password
```

### 3. 启动后端

```bash
cd cloud-base-server
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

后端启动地址: http://localhost:8080/api

### 4. 启动前端

```bash
cd cloud-base-ui
npm install
npm run dev
```

前端启动地址: http://localhost:3000

## 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 超级管理员 |

## 项目结构

```
cloud_base/
├── cloud-base-server/          # Spring Boot后端
│   ├── common/                 # 公共模块
│   │   ├── common-core/        # 核心工具
│   │   ├── common-security/    # 安全认证
│   │   └── common-log/         # 日志记录
│   ├── framework/              # 框架配置
│   │   ├── config/             # 配置类
│   │   └── aspect/             # AOP切面
│   ├── system/                 # 系统业务模块
│   │   ├── controller/         # 控制器
│   │   ├── service/           # 服务层
│   │   ├── mapper/            # 数据访问
│   │   └── entity/            # 实体类
│   └── src/main/resources/
│       ├── sql/init.sql       # 数据库初始化脚本
│       └── application*.yml   # 多环境配置
│
├── cloud-base-ui/              # Vue3前端
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── layout/            # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── utils/             # 工具函数
│   │   └── views/             # 页面视图
│   └── package.json
│
└── README.md                   # 项目文档
```

## 接口文档

启动后端后访问: http://localhost:8080/api/swagger-ui/

## License

MIT
