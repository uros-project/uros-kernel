package com.uros.kernel.handle.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.uros.kernel.handle.CompleteResourceInfo;
import com.uros.kernel.handle.HandleKernel;
import com.uros.kernel.handle.ResourceInstance;
import com.uros.kernel.handle.ResourceType;
import java.io.InputStream;

/**
 * 温度传感器示例类
 * 演示如何使用温度传感器的JSON Schema注册资源类型并创建资源实例
 */
public class TemperatureSensorExample {

  public static void main(String[] args) {
    try {
      // 创建HandleKernel实例
      HandleKernel kernel = new HandleKernel();
      
      // 加载温度传感器Schema
      ObjectMapper objectMapper = new ObjectMapper();
      InputStream schemaStream = TemperatureSensorExample.class.getResourceAsStream("/schemas/temperature_sensor.json");
      JsonNode temperatureSensorSchema = objectMapper.readTree(schemaStream);
      
      System.out.println("=== 注册温度传感器资源类型 ===");
      // 注册温度传感器资源类型
      ResourceType sensorType = kernel.registerResourceType(
          "TemperatureSensor", 
          "温度传感器资源类型，包含温度读数和开关接口", 
          temperatureSensorSchema);
      
      System.out.println("资源类型ID: " + sensorType.getId());
      System.out.println("资源类型名称: " + sensorType.getName());
      System.out.println("资源类型描述: " + sensorType.getDescription());
      
      System.out.println("\n=== 创建温度传感器资源实例 ===");
      // 创建温度传感器资源实例
      ObjectNode sensorData = objectMapper.createObjectNode();
      sensorData.put("id", "living-room-sensor");
      sensorData.put("name", "客厅温度传感器");
      
      // 添加遥测数据
      ObjectNode telemetry = sensorData.putObject("telemetry");
      telemetry.put("current", 24.5);
      telemetry.put("timestamp", System.currentTimeMillis());
      
      // 添加历史数据
      ObjectNode historyItem1 = objectMapper.createObjectNode();
      historyItem1.put("value", 24.2);
      historyItem1.put("timestamp", System.currentTimeMillis() - 60000);
      
      ObjectNode historyItem2 = objectMapper.createObjectNode();
      historyItem2.put("value", 23.8);
      historyItem2.put("timestamp", System.currentTimeMillis() - 120000);
      
      telemetry.putArray("history").add(historyItem1).add(historyItem2);
      
      // 添加统计信息
      ObjectNode statistics = telemetry.putObject("statistics");
      statistics.put("min", 23.5);
      statistics.put("max", 25.0);
      statistics.put("average", 24.3);
      statistics.put("trend", "stable");
      
      telemetry.put("unit", "celsius");
      telemetry.put("samplingRate", 60000);
      
      sensorData.put("status", "on");
      sensorData.put("powerSwitch", true);
      sensorData.put("lastUpdated", System.currentTimeMillis());
      
      // 添加位置信息
      ObjectNode location = sensorData.putObject("location");
      location.put("room", "客厅");
      location.put("position", "南墙");
      
      // 添加报警阈值
      ObjectNode alarmThresholds = sensorData.putObject("alarmThresholds");
      alarmThresholds.put("high", 30);
      alarmThresholds.put("low", 15);
      
      ResourceInstance instance = kernel.createResourceInstance("TemperatureSensor", sensorData);
      
      System.out.println("资源实例ID: " + instance.getId());
      System.out.println("资源类型: " + instance.getTypeName());
      System.out.println("传感器ID: " + instance.getData().get("id").asText());
      System.out.println("传感器名称: " + instance.getData().get("name").asText());
      
      // 打印遥测数据
      JsonNode sensorTelemetry = instance.getData().get("telemetry");
      System.out.println("当前温度: " + sensorTelemetry.get("current").asDouble() + "°C");
      System.out.println("温度单位: " + sensorTelemetry.get("unit").asText());
      System.out.println("采样频率: " + sensorTelemetry.get("samplingRate").asInt() + "ms");
      
      // 打印统计信息
      JsonNode sensorStats = sensorTelemetry.get("statistics");
      System.out.println("温度统计: 最低" + sensorStats.get("min").asDouble() + 
                         "°C, 最高" + sensorStats.get("max").asDouble() + 
                         "°C, 平均" + sensorStats.get("average").asDouble() + "°C");
      System.out.println("温度趋势: " + sensorStats.get("trend").asText());
      
      // 打印历史数据数量
      System.out.println("历史数据点: " + sensorTelemetry.get("history").size() + "个");
      
      System.out.println("传感器状态: " + instance.getData().get("status").asText());
      System.out.println("电源开关: " + (instance.getData().get("powerSwitch").asBoolean() ? "开启" : "关闭"));
      
      System.out.println("\n=== 模拟温度变化 ===");
      // 模拟温度变化
      ObjectNode updatedData = objectMapper.createObjectNode();
      updatedData.put("id", "living-room-sensor");
      updatedData.put("name", "客厅温度传感器");
      
      // 更新遥测数据 - 温度升高
      ObjectNode updatedTelemetry = updatedData.putObject("telemetry");
      updatedTelemetry.put("current", 26.8);  // 温度升高
      updatedTelemetry.put("timestamp", System.currentTimeMillis());
      
      // 更新历史数据 - 添加新的数据点
      ObjectNode newHistoryItem = objectMapper.createObjectNode();
      newHistoryItem.put("value", 24.5); // 上一次的当前值变成历史数据
      newHistoryItem.put("timestamp", System.currentTimeMillis() - 30000);
      
      // 保留之前的历史数据，但限制数量
      updatedTelemetry.putArray("history").add(newHistoryItem);
      
      // 更新统计信息
      ObjectNode updatedStatistics = updatedTelemetry.putObject("statistics");
      updatedStatistics.put("min", 23.5);
      updatedStatistics.put("max", 26.8); // 最高温度更新
      updatedStatistics.put("average", 25.1); // 平均温度更新
      updatedStatistics.put("trend", "rising"); // 趋势变为上升
      
      updatedTelemetry.put("unit", "celsius");
      updatedTelemetry.put("samplingRate", 60000);
      
      updatedData.put("status", "on");
      updatedData.put("powerSwitch", true);
      updatedData.put("lastUpdated", System.currentTimeMillis());
      
      // 保持位置信息
      ObjectNode updatedLocation = updatedData.putObject("location");
      updatedLocation.put("room", "客厅");
      updatedLocation.put("position", "南墙");
      
      // 保持报警阈值
      ObjectNode updatedAlarmThresholds = updatedData.putObject("alarmThresholds");
      updatedAlarmThresholds.put("high", 30);
      updatedAlarmThresholds.put("low", 15);
      
      ResourceInstance updatedInstance = kernel.updateResourceInstance(instance.getId(), updatedData);
      
      // 打印更新后的遥测数据
      JsonNode updatedSensorTelemetry = updatedInstance.getData().get("telemetry");
      System.out.println("更新后温度: " + updatedSensorTelemetry.get("current").asDouble() + "°C");
      System.out.println("温度趋势: " + updatedSensorTelemetry.get("statistics").get("trend").asText());
      System.out.println("历史数据点: " + updatedSensorTelemetry.get("history").size() + "个");
      
      System.out.println("\n=== 模拟关闭传感器 ===");
      // 模拟关闭传感器
      ObjectNode powerOffData = objectMapper.createObjectNode();
      powerOffData.put("id", "living-room-sensor");
      powerOffData.put("name", "客厅温度传感器");
      
      // 保持遥测数据不变
      ObjectNode powerOffTelemetry = powerOffData.putObject("telemetry");
      powerOffTelemetry.put("current", 26.8);
      powerOffTelemetry.put("timestamp", System.currentTimeMillis());
      powerOffTelemetry.put("unit", "celsius");
      
      // 添加最后一次的统计信息
      ObjectNode lastStatistics = powerOffTelemetry.putObject("statistics");
      lastStatistics.put("min", 23.5);
      lastStatistics.put("max", 26.8);
      lastStatistics.put("average", 25.1);
      lastStatistics.put("trend", "stable"); // 关闭后趋势变为稳定
      
      // 保持历史数据为空，因为设备关闭后不再收集数据
      powerOffTelemetry.putArray("history");
      
      powerOffData.put("status", "off");     // 状态改为关闭
      powerOffData.put("powerSwitch", false); // 开关改为关闭
      powerOffData.put("lastUpdated", System.currentTimeMillis());
      
      // 保持位置信息
      ObjectNode powerOffLocation = powerOffData.putObject("location");
      powerOffLocation.put("room", "客厅");
      powerOffLocation.put("position", "南墙");
      
      // 保持报警阈值
      ObjectNode powerOffAlarmThresholds = powerOffData.putObject("alarmThresholds");
      powerOffAlarmThresholds.put("high", 30);
      powerOffAlarmThresholds.put("low", 15);
      
      ResourceInstance powerOffInstance = kernel.updateResourceInstance(instance.getId(), powerOffData);
      
      System.out.println("传感器状态: " + powerOffInstance.getData().get("status").asText());
      System.out.println("电源开关: " + (powerOffInstance.getData().get("powerSwitch").asBoolean() ? "开启" : "关闭"));
      
      System.out.println("\n=== 获取完整资源信息 ===");
      // 获取完整资源信息
      CompleteResourceInfo info = kernel.getCompleteResourceInfo(instance.getId());
      
      System.out.println("资源ID: " + info.getResourceId());
      System.out.println("资源类型: " + info.getTypeName());
      System.out.println("资源类型ID: " + info.getType().getId());
      System.out.println("资源状态: " + (info.isActive() ? "活跃" : "非活跃"));
      System.out.println("资源数据: " + info.getResourceData().toString());
      
    } catch (Exception e) {
      System.err.println("示例运行出错: " + e.getMessage());
      e.printStackTrace();
    }
  }
}