# 温度传感器 JSON Schema

## 概述

本目录包含一个用于测试的JSON Schema文件，描述了一个温度传感器的数据结构。该Schema可用于在uros-kernel-handle模块中注册资源类型，并创建符合该类型的资源实例。

## 文件说明

- `temperature_sensor.json`: 温度传感器的JSON Schema定义

## Schema结构

温度传感器Schema使用 `$defs` 定义了可重用的数据类型，并包含以下主要字段：

| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | string | 传感器唯一标识符 |
| name | string | 传感器名称 |
| telemetry | object | 温度遥测数据（时序数据），引用 `#/$defs/telemetryData` 类型 |
| status | string | 传感器状态，可选值：on, off, error |
| powerSwitch | boolean | 电源开关状态，true表示开启，false表示关闭 |
| lastUpdated | integer | 最后更新时间（Unix时间戳，毫秒） |
| location | object | 传感器安装位置，包含room和position字段 |
| alarmThresholds | object | 温度报警阈值设置，包含high和low字段 |

### 遥测数据结构

`$defs/telemetryData` 类型定义了遥测数据结构，包含以下子字段：

| 字段名 | 类型 | 描述 |
|-------|------|------|
| current | number | 当前温度读数（摄氏度），范围-50到150 |
| timestamp | integer | 当前读数的时间戳（Unix时间戳，毫秒） |
| history | array | 历史温度数据数组，每个元素包含value（温度值）和timestamp（时间戳）|
| statistics | object | 统计信息，包含min（最低温度）、max（最高温度）、average（平均温度）和trend（趋势）|
| unit | string | 温度单位，如celsius（摄氏度）、fahrenheit（华氏度） |
| samplingRate | integer | 采样频率（毫秒） |

## 使用方法

### 在测试中使用

可以参考`TemperatureSensorSchemaTest.java`测试类，了解如何在测试中使用该Schema：

```java
// 从资源文件加载温度传感器Schema
InputStream schemaStream = getClass().getResourceAsStream("/schemas/temperature_sensor.json");
JsonNode temperatureSensorSchema = objectMapper.readTree(schemaStream);

// 注册温度传感器资源类型
ResourceType type = kernelHandle.registerResourceType(
    "TemperatureSensor", 
    "温度传感器资源类型", 
    temperatureSensorSchema);
```

### 在应用中使用

可以参考`TemperatureSensorExample.java`示例类，了解如何在应用中使用该Schema：

```java
// 加载温度传感器Schema
ObjectMapper objectMapper = new ObjectMapper();
InputStream schemaStream = TemperatureSensorExample.class.getResourceAsStream("/schemas/temperature_sensor.json");
JsonNode temperatureSensorSchema = objectMapper.readTree(schemaStream);

// 注册温度传感器资源类型
ResourceType sensorType = kernel.registerResourceType(
    "TemperatureSensor", 
    "温度传感器资源类型，包含温度读数和开关接口", 
    temperatureSensorSchema);
```

## 示例数据

以下是一个符合该Schema的温度传感器数据示例：

```json
{
  "id": "living-room-sensor",
  "name": "客厅温度传感器",
  "telemetry": {
    "current": 24.5,
    "timestamp": 1623456789000,
    "history": [
      {
        "value": 24.2,
        "timestamp": 1623456729000
      },
      {
        "value": 23.8,
        "timestamp": 1623456669000
      }
    ],
    "statistics": {
      "min": 23.5,
      "max": 25.0,
      "average": 24.3,
      "trend": "stable"
    },
    "unit": "celsius",
    "samplingRate": 60000
  },
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