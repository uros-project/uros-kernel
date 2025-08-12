package com.uros.kernel.handle;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** 温度传感器Schema测试类 */
public class TemperatureSensorSchemaTest {

  private HandleKernel kernelHandle;
  private ObjectMapper objectMapper;
  private JsonNode temperatureSensorSchema;

  @BeforeEach
  void setUp() throws Exception {
    kernelHandle = new HandleKernel();
    objectMapper = new ObjectMapper();
    
    // 从资源文件加载温度传感器Schema
    InputStream schemaStream = getClass().getResourceAsStream("/schemas/temperature_sensor.json");
    temperatureSensorSchema = objectMapper.readTree(schemaStream);
  }

  @Test
  void testRegisterTemperatureSensorType() {
    // 测试注册温度传感器资源类型
    ResourceType type = kernelHandle.registerResourceType(
        "TemperatureSensor", 
        "温度传感器资源类型", 
        temperatureSensorSchema);

    assertNotNull(type);
    assertEquals("TemperatureSensor", type.getName());
    assertEquals("温度传感器资源类型", type.getDescription());
    assertNotNull(type.getSchema());
    assertEquals(1, kernelHandle.getResourceTypeCount());
    
    // 验证Schema内容
    JsonNode schema = type.getSchema();
    assertEquals("温度传感器", schema.get("title").asText());
    assertTrue(schema.get("properties").has("telemetry"));
    assertTrue(schema.has("$defs"));
    assertTrue(schema.get("$defs").has("telemetryData"));
    assertTrue(schema.get("$defs").get("telemetryData").get("properties").has("current"));
    assertTrue(schema.get("$defs").get("telemetryData").get("properties").has("timestamp"));
    assertTrue(schema.get("$defs").get("telemetryData").get("properties").has("history"));
    assertTrue(schema.get("properties").has("powerSwitch"));
  }

  @Test
  void testCreateTemperatureSensorInstance() {
    // 先注册温度传感器资源类型
    kernelHandle.registerResourceType(
        "TemperatureSensor", 
        "温度传感器资源类型", 
        temperatureSensorSchema);

    // 创建温度传感器资源实例
    ObjectNode sensorData = objectMapper.createObjectNode();
    sensorData.put("id", "sensor-001");
    sensorData.put("name", "客厅温度传感器");
    
    // 添加遥测数据
    ObjectNode telemetry = sensorData.putObject("telemetry");
    telemetry.put("current", 25.5);
    telemetry.put("timestamp", System.currentTimeMillis());
    
    // 添加历史数据
    ObjectNode historyItem1 = objectMapper.createObjectNode();
    historyItem1.put("value", 25.2);
    historyItem1.put("timestamp", System.currentTimeMillis() - 60000);
    
    ObjectNode historyItem2 = objectMapper.createObjectNode();
    historyItem2.put("value", 24.8);
    historyItem2.put("timestamp", System.currentTimeMillis() - 120000);
    
    telemetry.putArray("history").add(historyItem1).add(historyItem2);
    
    // 添加统计信息
    ObjectNode statistics = telemetry.putObject("statistics");
    statistics.put("min", 24.5);
    statistics.put("max", 26.0);
    statistics.put("average", 25.3);
    statistics.put("trend", "rising");
    
    telemetry.put("unit", "celsius");
    telemetry.put("samplingRate", 60000);
    
    sensorData.put("status", "on");
    sensorData.put("powerSwitch", true);
    sensorData.put("lastUpdated", System.currentTimeMillis());
    
    // 添加位置信息
    ObjectNode location = sensorData.putObject("location");
    location.put("room", "客厅");
    location.put("position", "东墙");
    
    // 添加报警阈值
    ObjectNode alarmThresholds = sensorData.putObject("alarmThresholds");
    alarmThresholds.put("high", 35);
    alarmThresholds.put("low", 10);

    ResourceInstance instance = kernelHandle.createResourceInstance("TemperatureSensor", sensorData);

    assertNotNull(instance);
    assertEquals("TemperatureSensor", instance.getTypeName());
    assertEquals("sensor-001", instance.getData().get("id").asText());
    assertEquals("客厅温度传感器", instance.getData().get("name").asText());
    
    // 验证遥测数据
    JsonNode instanceTelemetry = instance.getData().get("telemetry");
    assertNotNull(instanceTelemetry);
    assertEquals(25.5, instanceTelemetry.get("current").asDouble());
    assertTrue(instanceTelemetry.has("timestamp"));
    
    // 验证历史数据
    JsonNode history = instanceTelemetry.get("history");
    assertNotNull(history);
    assertEquals(2, history.size());
    assertEquals(25.2, history.get(0).get("value").asDouble());
    assertEquals(24.8, history.get(1).get("value").asDouble());
    
    // 验证统计信息
    JsonNode telemetryStats = instanceTelemetry.get("statistics");
    assertNotNull(telemetryStats);
    assertEquals(24.5, telemetryStats.get("min").asDouble());
    assertEquals(26.0, telemetryStats.get("max").asDouble());
    assertEquals(25.3, telemetryStats.get("average").asDouble());
    assertEquals("rising", telemetryStats.get("trend").asText());
    
    // 验证单位和采样率
    assertEquals("celsius", instanceTelemetry.get("unit").asText());
    assertEquals(60000, instanceTelemetry.get("samplingRate").asInt());
    
    assertEquals("on", instance.getData().get("status").asText());
    assertTrue(instance.getData().get("powerSwitch").asBoolean());
    assertEquals(1, kernelHandle.getResourceInstanceCount());
  }

  @Test
  void testUpdateTemperatureSensorStatus() {
    // 先注册温度传感器资源类型
    kernelHandle.registerResourceType(
        "TemperatureSensor", 
        "温度传感器资源类型", 
        temperatureSensorSchema);

    // 创建温度传感器资源实例
    ObjectNode sensorData = objectMapper.createObjectNode();
    sensorData.put("id", "sensor-002");
    sensorData.put("name", "卧室温度传感器");
    
    // 添加遥测数据
    ObjectNode telemetry = sensorData.putObject("telemetry");
    telemetry.put("current", 22.0);
    telemetry.put("timestamp", System.currentTimeMillis());
    telemetry.put("unit", "celsius");
    
    sensorData.put("status", "on");
    sensorData.put("powerSwitch", true);
    
    ResourceInstance instance = kernelHandle.createResourceInstance("TemperatureSensor", sensorData);

    // 更新温度传感器状态
    ObjectNode updatedData = objectMapper.createObjectNode();
    updatedData.put("id", "sensor-002");
    updatedData.put("name", "卧室温度传感器");
    
    // 更新遥测数据
    ObjectNode updatedTelemetry = updatedData.putObject("telemetry");
    updatedTelemetry.put("current", 23.5);  // 温度变化
    updatedTelemetry.put("timestamp", System.currentTimeMillis());
    updatedTelemetry.put("unit", "celsius");
    
    // 添加新的统计信息
    ObjectNode statistics = updatedTelemetry.putObject("statistics");
    statistics.put("min", 22.0);
    statistics.put("max", 23.5);
    statistics.put("average", 22.8);
    statistics.put("trend", "rising");
    
    updatedData.put("status", "off");     // 状态变化
    updatedData.put("powerSwitch", false); // 开关变化
    updatedData.put("lastUpdated", System.currentTimeMillis());

    ResourceInstance updatedInstance = kernelHandle.updateResourceInstance(instance.getId(), updatedData);

    // 验证更新后的遥测数据
    JsonNode updatedTelemetryData = updatedInstance.getData().get("telemetry");
    assertEquals(23.5, updatedTelemetryData.get("current").asDouble());
    assertEquals("celsius", updatedTelemetryData.get("unit").asText());
    
    // 验证更新后的统计信息
    JsonNode updatedStats = updatedTelemetryData.get("statistics");
    assertEquals(22.0, updatedStats.get("min").asDouble());
    assertEquals(23.5, updatedStats.get("max").asDouble());
    assertEquals(22.8, updatedStats.get("average").asDouble());
    assertEquals("rising", updatedStats.get("trend").asText());
    
    assertEquals("off", updatedInstance.getData().get("status").asText());
    assertFalse(updatedInstance.getData().get("powerSwitch").asBoolean());
  }

  @Test
  void testResolveTemperatureSensor() {
    // 先注册温度传感器资源类型
    kernelHandle.registerResourceType(
        "TemperatureSensor", 
        "温度传感器资源类型", 
        temperatureSensorSchema);

    // 创建温度传感器资源实例
    ObjectNode sensorData = objectMapper.createObjectNode();
    sensorData.put("id", "sensor-003");
    sensorData.put("name", "厨房温度传感器");
    
    // 添加遥测数据
    ObjectNode telemetry = sensorData.putObject("telemetry");
    telemetry.put("current", 28.0);
    telemetry.put("timestamp", System.currentTimeMillis());
    telemetry.put("unit", "celsius");
    
    sensorData.put("status", "on");
    sensorData.put("powerSwitch", true);
    
    ResourceInstance instance = kernelHandle.createResourceInstance("TemperatureSensor", sensorData);

    // 测试资源解析
    ResourceResolutionResult result = kernelHandle.resolveResource(instance.getId());

    assertTrue(result.isSuccess());
    assertNotNull(result.getInstance());
    assertNotNull(result.getType());
    assertEquals("TemperatureSensor", result.getType().getName());
    assertEquals("厨房温度传感器", result.getInstance().getData().get("name").asText());
    
    // 验证解析后的遥测数据
    JsonNode resolvedTelemetry = result.getInstance().getData().get("telemetry");
    assertNotNull(resolvedTelemetry);
    assertEquals(28.0, resolvedTelemetry.get("current").asDouble());
    assertEquals("celsius", resolvedTelemetry.get("unit").asText());
    assertTrue(resolvedTelemetry.has("timestamp"));
  }
}