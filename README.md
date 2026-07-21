# Blog System - 博客系统

[![CI](https://github.com/Zhu0Ge/blogSys/actions/workflows/maven.yml/badge.svg)](https://github.com/Zhu0Ge/blogSys/actions/workflows/maven.yml)

基于 Spring Boot 3.4 + Vue 3 的前后端分离博客系统，支持用户认证、文章管理、评论互动、个人主页等功能。

---

## 技术栈

| 分类 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.4, Spring Data JPA, Spring Security |
| 数据库 | MySQL 8.0, H2（测试环境） |
| 认证 | JWT（jjwt 0.12） |
| 前端 | Vue 3 (Composition API), Vue Router 4, Vite 5 |
| 部署 | Docker Compose（MySQL + 后端 + 前端） |
| CI/CD | GitHub Actions（自动构建 + 测试 + 覆盖率报告） |
| 测试 | JUnit 5, Mockito, JaCoCo |

---

## 功能特性

- [x] 用户注册 / 登录（JWT 认证）
- [x] 文章 CRUD（仅作者可编辑/删除）
- [x] 文章分页查询（Spring Data Pageable）
- [x] 文章搜索（标题模糊匹配 + 300ms 防抖）
- [x] 评论系统（树形结构 + 折叠展开）
- [x] 个人主页（头像上传 + 个人简介）
- [x] 统一异常处理 + 参数校验
- [x] Docker 一键部署

---

## 性能报告

### 分页查询优化

在 5075 篇文章下，50 并发测试结果：

| 指标 | 全量查询 | 分页查询（10篇/页） | 提升 |
|------|---------|------------------|------|
| 平均响应时间 | 556ms | **56ms** | **快 10 倍** |
| 90% 响应时间 | 847ms | **79ms** | **快 10 倍** |
| 单次传输量 | 1.6 MB | **3.4 KB** | **减少 99.8%** |
| 错误率 | 0% | 0% | - |

### N+1 查询优化

| 指标 | 优化前 | 优化后（@EntityGraph） |
|------|--------|---------------------|
| SQL 查询次数 | 5 次（1+4） | **1 次 JOIN** |
| 响应时间（10并发） | 85ms | **24ms** |

### 单元测试覆盖率

```
JwtUtil               ∼ 70%
ArticleServiceImpl    ∼ 30%
UserServiceImpl       ∼ 60%
CommentServiceImpl    ∼ 40%
```

---

## 快速开始

### Docker 部署（推荐）

```bash
git clone https://github.com/Zhu0Ge/blogSys.git
cd blogSys

# 一键启动
docker compose up -d --build

# 访问
# 前端：http://localhost
# 后端：http://localhost:8080
```

### 本地开发

**后端：**
```bash
cd user-api-java
mvn spring-boot:run
```

**前端：**
```bash
npm install
npm run dev
# 访问 http://localhost:5173
```

---

## 项目结构

```
blogSys/
├── docker-compose.yml           # Docker 编排
├── Dockerfile                   # 前端镜像构建
├── nginx.conf                   # Nginx SPA 路由配置
├── user-api-java/
│   ├── Dockerfile               # 后端镜像构建
│   ├── src/main/java/com/example/
│   │   ├── controller/          # 控制器层
│   │   ├── service/             # 业务逻辑层
│   │   ├── repository/          # JPA 数据访问层
│   │   ├── model/               # 实体类
│   │   ├── dto/                 # 数据传输对象
│   │   ├── config/              # 安全、CORS 等配置
│   │   ├── common/              # 统一响应 R<T>
│   │   └── util/                # JWT 工具类
│   └── src/test/                # 单元测试 + 集成测试
└── src/
    ├── views/                   # 前端页面组件
    ├── router/                  # 前端路由
    └── http.js                  # HTTP 请求封装
```

---

## API 概览

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/register | 注册 | ❌ |
| POST | /api/login | 登录 | ❌ |
| GET | /api/articles | 全量文章列表 | ✅ |
| GET | /api/articles/paged | **分页文章列表** | ✅ |
| GET | /api/articles/{id} | 文章详情 | ✅ |
| POST | /api/articles | 创建文章 | ✅ |
| PUT | /api/articles/{id} | 更新文章（仅作者） | ✅ |
| DELETE | /api/articles/{id} | 删除文章（仅作者） | ✅ |
| GET | /api/articles/search?q= | 搜索文章 | ✅ |
| GET | /api/articles/{id}/comments | 文章评论 | ✅ |
| POST | /api/comments | 发表评论 | ✅ |
| DELETE | /api/comments/{id} | 删除评论（仅作者） | ✅ |
| GET | /api/users/{id} | 用户信息 | ✅ |
| PUT | /api/users/profile | 更新个人资料 | ✅ |
| POST | /api/upload/avatar | 上传头像 | ✅ |
