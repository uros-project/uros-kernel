# UROS Binder

UROS Binder 是 UROS 内核的资源连接和通信管理模块。

## 核心概念

**Binder** 是一个表示资源间连接的核心概念，它不仅仅是简单的绑定关系，而是实现了 Handle 间的数据传输通道。通过 Binder，Handle 间或 Handle 内的资源可以进行高效的通信和数据交换。

## 功能概述

UROS Binder 提供以下核心功能：

- **资源连接管理**：建立和管理不同资源之间的连接通道
- **数据传输通道**：实现 Handle 间的数据传输和通信机制
- **跨 Handle 通信**：支持不同 Handle 实例间的资源通信
- **Handle 内通信**：支持同一 Handle 内不同资源间的数据交换
- **连接策略**：支持多种连接策略和通信模式
- **生命周期管理**：管理连接关系的创建、维护和销毁

## 模块结构

```
uros-binder/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/uros/kernel/binder/
│   │   │       ├── model/          # 连接相关的数据模型
│   │   │       ├── service/        # 连接和通信服务层
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
- **Jackson**: JSON 处理和数据序列化

## 通信机制

UROS Binder 实现了多种通信模式：

### 1. Handle 间通信
- **跨实例通信**：不同 Handle 应用实例间的资源数据传输
- **分布式连接**：支持网络环境下的远程资源连接
- **协议适配**：支持多种通信协议和数据格式

### 2. Handle 内通信
- **本地连接**：同一 Handle 实例内资源间的高效数据交换
- **内存通道**：基于内存的快速数据传输
- **事件驱动**：支持异步事件驱动的通信模式

### 3. 连接类型
- **DEPENDENCY**：依赖关系连接，表示资源间的依赖关系
- **ASSOCIATION**：关联关系连接，表示资源间的业务关联
- **COMMUNICATION**：通信连接，专门用于数据传输
- **SYNCHRONIZATION**：同步连接，用于资源状态同步

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
  # 连接和通信相关配置
  binding:
    max-bindings-per-resource: 100
    auto-cleanup-expired: true
    cleanup-interval: 3600
```

## 版本历史

- **1.0-SNAPSHOT**: 初始版本，提供基础的资源连接和通信功能

## 使用场景

### 典型应用场景

1. **微服务通信**：不同 Handle 服务间的数据交换和协作
2. **资源同步**：多个资源实例间的状态同步和数据一致性
3. **工作流编排**：通过连接关系实现复杂的业务流程编排
4. **事件传播**：基于连接关系的事件传播和处理
5. **数据管道**：构建资源间的数据处理管道

### 连接示例

```json
{
  "sourceResourceId": "handle-001/sensor-temp-01",
  "targetResourceId": "handle-002/processor-data-01",
  "bindingType": "COMMUNICATION",
  "name": "温度数据传输通道",
  "properties": {
    "protocol": "HTTP",
    "format": "JSON",
    "frequency": "1s"
  }
}
```