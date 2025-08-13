# UROS Binder

UROS Binder 是 UROS 内核的资源绑定和关联管理模块。

## 功能概述

UROS Binder 提供以下核心功能：

- **资源绑定管理**：管理不同资源之间的绑定关系
- **关联关系维护**：维护资源间的依赖和关联关系
- **绑定策略**：支持多种绑定策略和规则
- **生命周期管理**：管理绑定关系的创建、更新和销毁

## 模块结构

```
uros-binder/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/uros/kernel/binder/
│   │   │       ├── model/          # 绑定相关的数据模型
│   │   │       ├── service/        # 绑定服务层
│   │   │       ├── controller/     # REST API 控制器
│   │   │       └── config/         # 配置类
│   │   └── resources/
│   │       └── application.yml     # 配置文件
│   └── test/
│       ├── java/                   # 测试代码
│       └── resources/              # 测试资源
├── pom.xml
└── README.md
```

## 依赖关系

- **uros-base**: 基础模块，提供核心功能
- **Spring Boot**: Web 框架和依赖注入
- **Jackson**: JSON 处理

## 开发指南

### 编译和测试

```bash
# 编译模块
mvn compile

# 运行测试
mvn test

# 打包
mvn package
```

### API 文档

模块启动后，可以通过以下地址访问 API 文档：
- Swagger UI: http://localhost:8082/binder/swagger-ui.html

## 配置说明

主要配置项在 `application.yml` 中：

```yaml
server:
  port: 8082
  servlet:
    context-path: /binder

binder:
  id: "binder-app-001"
  # 其他绑定相关配置
```

## 版本历史

- **1.0-SNAPSHOT**: 初始版本，提供基础的资源绑定功能