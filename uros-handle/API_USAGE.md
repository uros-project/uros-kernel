# Uros Kernel Handle API 使用指南

## 概述

本文档描述了 `uros-kernel-handle` 模块提供的 REST API 接口，用于管理资源类型。

## 基础信息

- **服务地址**: `http://localhost:8081/handle`
- **API 基础路径**: `/api/resource-types`
- **完整 URL**: `http://localhost:8081/handle/api/resource-types`

## API 接口列表

### 1. 注册资源类型

**POST** `/api/resource-types`

**请求体**:
```json
{
  "name": "User",
  "description": "用户资源类型",
  "schema": {
    "type": "object",
    "properties": {
      "name": {
        "type": "string",
        "description": "用户姓名"
      },
      "age": {
        "type": "integer",
        "minimum": 0,
        "maximum": 150
      },
      "email": {
        "type": "string",
        "format": "email"
      }
    },
    "required": ["name", "email"]
  }
}
```

**响应**:
```json
{
  "success": true,
  "message": "资源类型注册成功",
  "data": {
    "id": "uuid-here",
    "name": "User",
    "description": "用户资源类型",
    "schema": { ... },
    "createdAt": 1234567890,
    "updatedAt": 1234567890
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 2. 查询资源类型

#### 2.1 根据ID查询

**GET** `/api/resource-types/{typeId}`

**响应**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": "uuid-here",
    "name": "User",
    "description": "用户资源类型",
    "schema": { ... }
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

#### 2.2 根据名称查询

**GET** `/api/resource-types/name/{name}`

**响应**: 同上

#### 2.3 获取所有资源类型

**GET** `/api/resource-types`

**响应**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {
      "id": "uuid-1",
      "name": "User",
      "description": "用户资源类型"
    },
    {
      "id": "uuid-2",
      "name": "Product",
      "description": "产品资源类型"
    }
  ],
  "timestamp": "2024-01-01T12:00:00"
}
```

### 3. 更新资源类型

**PUT** `/api/resource-types/{typeId}`

**请求体**:
```json
{
  "description": "更新后的用户资源类型描述",
  "schema": {
    "type": "object",
    "properties": {
      "name": { "type": "string" },
      "age": { "type": "integer" },
      "email": { "type": "string" },
      "phone": { "type": "string" }
    }
  }
}
```

**响应**:
```json
{
  "success": true,
  "message": "资源类型更新成功",
  "data": {
    "id": "uuid-here",
    "name": "User",
    "description": "更新后的用户资源类型描述",
    "schema": { ... },
    "updatedAt": 1234567890
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 4. 删除资源类型

**DELETE** `/api/resource-types/{typeId}`

**响应**:
```json
{
  "success": true,
  "message": "资源类型删除成功",
  "data": true,
  "timestamp": "2024-01-01T12:00:00"
}
```

### 5. 检查资源类型是否存在

**GET** `/api/resource-types/{typeId}/exists`

**响应**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": true,
  "timestamp": "2024-01-01T12:00:00"
}
```

### 6. 获取资源类型数量

**GET** `/api/resource-types/count`

**响应**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": 5,
  "timestamp": "2024-01-01T12:00:00"
}
```

## 健康检查接口

### 健康状态

**GET** `/health`

**响应**:
```json
{
  "success": true,
  "message": "服务运行正常",
  "data": {
    "status": "UP",
    "service": "uros-kernel-handle",
    "timestamp": 1234567890
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### Ping 测试

**GET** `/health/ping`

**响应**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": "pong",
  "timestamp": "2024-01-01T12:00:00"
}
```

## 错误处理

### 错误响应格式

```json
{
  "success": false,
  "message": "错误描述",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```

### 常见错误码

- **400 Bad Request**: 请求参数错误
- **404 Not Found**: 资源不存在
- **500 Internal Server Error**: 服务器内部错误

## 使用示例

### cURL 示例

```bash
# 注册资源类型
curl -X POST http://localhost:8081/handle/api/resource-types \
  -H "Content-Type: application/json" \
  -d '{
    "name": "User",
    "description": "用户资源类型",
    "schema": {"type": "object"}
  }'

# 查询所有资源类型
curl http://localhost:8081/handle/api/resource-types

# 根据名称查询
curl http://localhost:8081/handle/api/resource-types/name/User

# 更新资源类型
curl -X PUT http://localhost:8081/handle/api/resource-types/{typeId} \
  -H "Content-Type: application/json" \
  -d '{
    "description": "新的描述",
    "schema": {"type": "object"}
  }'

# 删除资源类型
curl -X DELETE http://localhost:8081/handle/api/resource-types/{typeId}
```

### JavaScript 示例

```javascript
// 注册资源类型
const response = await fetch('http://localhost:8081/handle/api/resource-types', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: 'User',
    description: '用户资源类型',
    schema: { type: 'object' }
  })
});

const result = await response.json();
console.log(result);
```

## 注意事项

1. 所有请求和响应都使用 JSON 格式
2. 资源类型名称必须唯一
3. 删除资源类型会同时删除相关的资源实例
4. 建议在生产环境中配置适当的 CORS 策略
5. API 文档可通过 Swagger UI 访问：`http://localhost:8081/handle/swagger-ui.html`
