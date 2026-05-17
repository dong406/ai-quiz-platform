# 腾科刷题AI答题系统

## 项目简介

腾科刷题AI答题系统是一个基于Spring Boot + Vue 3的全栈应用，支持题目创建、AI生成题目、答题评分、结果分享等功能。

## 技术栈

### 后端
- Spring Boot 2.7.14
- MyBatis Plus
- MySQL 8.0
- Redis
- 智谱AI SDK

### 前端
- Vue 3
- TypeScript
- Arco Design Vue
- Vite
- Pinia
- Vue Router

## 项目结构

```
.
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/tengke/
│   │   │   │   ├── annotation/    # 注解
│   │   │   │   ├── aop/           # AOP拦截器
│   │   │   │   ├── config/        # 配置类
│   │   │   │   ├── constant/      # 常量
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── exception/      # 异常处理
│   │   │   │   ├── interceptor/   # 拦截器
│   │   │   │   ├── manager/       # 管理器（AI等）
│   │   │   │   ├── mapper/        # Mapper接口
│   │   │   │   ├── model/         # 实体类、DTO、VO
│   │   │   │   ├── service/       # 服务层
│   │   │   │   ├── strategy/      # 策略模式
│   │   │   │   └── utils/         # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/schema.sql
│   │   └── pom.xml
└── frontend/               # 前端项目
    ├── src/
    │   ├── api/            # API接口
    │   ├── views/          # 页面组件
    │   ├── stores/         # Pinia状态管理
    │   ├── router/         # 路由配置
    │   └── utils/          # 工具函数
    ├── package.json
    └── vite.config.ts
```

## 快速开始

### 后端启动

1. 创建数据库并执行 `backend/src/main/resources/db/schema.sql`
2. 修改 `application.yml` 中的数据库和Redis配置
3. 配置AI密钥（已在application.yml中配置）
4. 运行 `TengkeQuestionApplication`

### 前端启动

1. 进入 `frontend` 目录
2. 执行 `npm install`
3. 执行 `npm run dev`

## 功能模块

### 1. 用户模块
- 用户注册（账号唯一校验、密码MD5加密）
- 用户登录（Session存储登录态）
- 权限控制（管理员@AuthCheck注解 + AOP拦截）

### 2. 应用模块
- 应用创建（校验应用名称、类型、评分策略）
- 应用审核（管理员审核，仅审核通过的应用可被普通用户查看）
- 应用分享（生成二维码）

### 3. 问题模块
- 题目创建（关联应用ID，包含题干、选项、分数/结果）
- 题目查询（根据应用ID批量查询）
- AI生成题目（调用智谱AI生成题目）

### 4. 评分模块
- 策略模式实现多种评分方式
  - 自定义打分（打分类-自定义打分）
  - 自定义测评（测评类-自定义测评）
  - AI测评（测评类-AI测评）

### 5. 评分结果模块
- 保存评分结果（关联用户ID、应用ID、答题选项、评分结果）
- 结果查询（按用户ID/应用ID分页查询）
- 结果分享（二维码分享）

## 注意事项

1. **AI调用**：当前AI Manager中的智谱AI调用为模拟实现，需要根据实际SDK进行替换
2. **Session管理**：前端需要正确设置Cookie以支持Session
3. **数据库**：确保MySQL和Redis服务已启动
4. **跨域配置**：后端已配置跨域，前端开发时使用代理

## 默认账号

- 管理员账号：admin
- 管理员密码：admin123456（MD5加密后存储）

## 开发规范

- 代码风格与参考代码片段保持一致
- 使用统一的异常处理和响应封装
- 参数校验使用ThrowUtils工具类
- 前端使用TypeScript严格模式
