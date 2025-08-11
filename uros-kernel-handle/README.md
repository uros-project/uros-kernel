# uros-kernel-handle

这是 uros-kernel 项目的资源处理模块，提供资源类型注册与资源对象创建、删除、更新、查询等基本服务。

## 核心功能

### 1. 资源类型管理
- **资源类型注册**: 使用 JSON Schema 作为 DSL 描述资源元数据
- **类型查询**: 支持按名称和ID查询资源类型
- **类型更新**: 支持更新资源类型的描述和Schema

### 2. 资源实例管理
- **资源创建**: 基于资源类型创建资源实例，每个实例有唯一编号
- **CRUD 操作**: 支持创建、读取、更新、删除资源实例
- **状态管理**: 支持资源实例的状态管理（ACTIVE, INACTIVE, DELETED）

### 3. 核心资源解析服务
- **资源解析**: 基于资源唯一编号进行资源解析的核心服务
- **完整信息获取**: 获取包含类型定义和实例数据的完整资源信息
- **数据验证**: 验证资源数据是否符合类型定义

## 架构设计

```
HandleKernel (主服务)
├── ResourceTypeRegistry (资源类型注册)
├── ResourceInstanceManager (资源实例管理)
└── ResourceResolver (核心资源解析)
```

## 依赖关系

- 依赖于 `uros-kernel-base` 模块
- 使用 Jackson 进行 JSON 处理
- 使用 Java 17
- 基于 Maven 构建

## 使用方法

### 基本使用

```java
import com.uros.kernel.handle.HandleKernel;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// 创建处理器
HandleKernel handle = new HandleKernel();
ObjectMapper mapper = new ObjectMapper();

// 注册资源类型
ObjectNode schema = mapper.createObjectNode();
ResourceType userType = handle.registerResourceType("User", "用户资源", schema);

// 创建资源实例
ObjectNode userData = mapper.createObjectNode();
userData.put("name", "张三");
userData.put("age", 25);
ResourceInstance user = handle.createResourceInstance("User", userData);

// 核心服务：基于ID解析资源
ResourceResolutionResult result = handle.resolveResource(user.getId());
```

### 资源类型管理

```java
// 查询资源类型
ResourceType type = handle.getResourceTypeByName("User");
List<ResourceType> allTypes = handle.getAllResourceTypes();

// 统计信息
int typeCount = handle.getResourceTypeCount();
int instanceCount = handle.getResourceInstanceCount();
```

### 资源实例操作

```java
// 查询资源实例
ResourceInstance instance = handle.getResourceInstanceById("instance-id");

// 更新资源实例
ObjectNode newData = mapper.createObjectNode();
newData.put("name", "新名称");
handle.updateResourceInstance("instance-id", newData);

// 删除资源实例
boolean deleted = handle.deleteResourceInstance("instance-id");
```

## 构建和测试

```bash
# 编译模块
mvn compile

# 运行测试
mvn test

# 打包
mvn package
```

## 模块结构

```
uros-kernel-handle/
├── src/
│   ├── main/java/com/uros/kernel/handle/
│   │   ├── HandleKernel.java          # 主服务类（原 KernelHandle）
│   │   ├── ResourceType.java          # 资源类型定义
│   │   ├── ResourceInstance.java      # 资源实例
│   │   ├── ResourceTypeRegistry.java  # 资源类型注册服务
│   │   ├── ResourceInstanceManager.java # 资源实例管理服务
│   │   ├── ResourceResolver.java      # 核心资源解析服务
│   │   ├── ResourceResolutionResult.java # 解析结果
│   │   ├── ValidationResult.java      # 验证结果
│   │   └── CompleteResourceInfo.java  # 完整资源信息
│   └── test/java/com/uros/kernel/handle/
│       └── KernelHandleTest.java      # 测试类（保留名称以兼容）
├── pom.xml
└── README.md
```

## 设计特点

1. **DSL 支持**: 使用 JSON Schema 作为资源元数据描述语言
2. **唯一标识**: 每个资源实例都有全局唯一的编号
3. **类型安全**: 基于类型定义进行资源管理
4. **核心服务**: 提供基于ID的资源解析核心功能
5. **并发安全**: 使用 ConcurrentHashMap 保证线程安全
6. **完整测试**: 包含全面的单元测试覆盖
7. **REST API**: 提供完整的 REST 接口支持

## REST API 支持

### 服务信息
- **服务端口**: 8081
- **上下文路径**: `/handle`
- **API 基础路径**: `/api/resource-types`
- **完整服务地址**: `http://localhost:8081/handle`

### 主要接口
- **POST** `/api/resource-types` - 注册资源类型
- **GET** `/api/resource-types` - 获取所有资源类型
- **GET** `/api/resource-types/{id}` - 根据ID查询资源类型
- **GET** `/api/resource-types/name/{name}` - 根据名称查询资源类型
- **PUT** `/api/resource-types/{id}` - 更新资源类型
- **DELETE** `/api/resource-types/{id}` - 删除资源类型
- **GET** `/api/resource-types/count` - 获取资源类型数量
- **GET** `/health` - 健康检查

### API 文档
- **Swagger UI**: `http://localhost:8081/handle/swagger-ui.html`
- **详细使用指南**: 请参考 `API_USAGE.md` 文件
