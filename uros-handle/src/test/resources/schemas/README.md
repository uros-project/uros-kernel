# 温度传感器 JSON Schema

## 概述

本目录包含一个用于测试的JSON Schema文件，描述了一个温度传感器的数据结构。该Schema可用于在uros-handle模块中注册资源类型，并创建符合该类型的资源实例。

## 文件说明

- `temperature_sensor.schema.json`: 温度传感器的JSON Schema定义

## Schema结构

温度传感器Schema定义了一个简单的温度传感器对象，包含以下主要字段：

| 字段名 | 类型 | 必需 | 描述 |
|-------|------|------|------|
| id | string | ✓ | 传感器唯一标识符 |
| name | string | ✓ | 传感器名称 |
| status | string | ✓ | 传感器状态，可选值：on, off, error，默认值：off |
| powerSwitch | boolean | ✓ | 电源开关状态，true表示开启，false表示关闭，默认值：false |
| lastUpdated | integer | | 最后更新时间（Unix时间戳，毫秒） |
| location | object | | 传感器安装位置，包含room和position字段 |
| alarmThresholds | object | | 温度报警阈值设置，包含high和low字段 |

### 位置信息结构 (location)

| 字段名 | 类型 | 描述 |
|-------|------|------|
| room | string | 房间名称 |
| position | string | 具体位置描述 |

### 报警阈值结构 (alarmThresholds)

| 字段名 | 类型 | 默认值 | 描述 |
|-------|------|--------|------|
| high | number | 40 | 高温报警阈值 |
| low | number | 0 | 低温报警阈值 |

## Schema特性

- **Schema版本**: JSON Schema Draft-07
- **类型**: object
- **附加属性**: 禁止 (additionalProperties: false)
- **必需字段**: id, name, status, powerSwitch

## 使用方法

### 在测试中使用

可以参考以下方式在测试中使用该Schema：

```java
// 从资源文件加载温度传感器Schema
InputStream schemaStream = getClass().getResourceAsStream("/schemas/temperature_sensor.schema.json");
JsonNode temperatureSensorSchema = objectMapper.readTree(schemaStream);

// 注册温度传感器资源类型
UniResourceType type = uniResourceTypeService.create(
    "TemperatureSensor", 
    "温度传感器资源类型", 
    "1.0",
    temperatureSensorSchema);
```

### 在应用中使用

可以参考以下方式在应用中使用该Schema：

```java
// 加载温度传感器Schema
ObjectMapper objectMapper = new ObjectMapper();
InputStream schemaStream = getClass().getResourceAsStream("/schemas/temperature_sensor.schema.json");
JsonNode temperatureSensorSchema = objectMapper.readTree(schemaStream);

// 注册温度传感器资源类型
UniResourceType sensorType = uniResourceTypeService.create(
    "TemperatureSensor", 
    "温度传感器资源类型，包含温度读数和开关接口", 
    "1.0",
    temperatureSensorSchema);
```

## 示例数据

以下是一个符合该Schema的温度传感器数据示例：

```json
{
  "id": "living-room-sensor",
  "name": "客厅温度传感器",
  "status": "on",
  "powerSwitch": true,
  "lastUpdated": 1623456789000,
  "location": {
    "room": "客厅",
    "position": "南墙"
  },
  "alarmThresholds": {
    "high": 30,
    "low": 15
  }
}
```

### 最小示例（仅包含必需字段）

```json
{
  "id": "sensor-001",
  "name": "基础温度传感器",
  "status": "off",
  "powerSwitch": false
}
```