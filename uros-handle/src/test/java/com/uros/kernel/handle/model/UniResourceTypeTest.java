package com.uros.kernel.handle.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UniResourceType测试类
 * 测试基于JSON Schema生成示例数据和创建UniResource的功能
 */
public class UniResourceTypeTest {
    
    private ObjectMapper objectMapper;
    private UniResourceType temperatureSensorType;
    
    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        
        // 加载温度传感器Schema
        InputStream schemaStream = getClass().getResourceAsStream("/schemas/temperature_sensor.schema.json");
        assertNotNull(schemaStream, "Schema文件不存在");
        
        JsonNode temperatureSensorSchema = objectMapper.readTree(schemaStream);
        
        // 创建UniResourceType实例
        temperatureSensorType = new UniResourceType(
            "temperature-sensor-type",
            "温度传感器类型",
            "用于测试的温度传感器资源类型",
            "1.0",
            temperatureSensorSchema
        );
    }
    
    @Test
    void testGenerateSampleData() {
        // 测试生成示例数据
        JsonNode sampleData = temperatureSensorType.generateSampleData();
        
        assertNotNull(sampleData, "生成的示例数据不应为null");
        assertTrue(sampleData.isObject(), "生成的示例数据应为对象类型");
        
        // 验证必需字段是否存在
        assertTrue(sampleData.has("id"), "示例数据应包含id字段");
        assertTrue(sampleData.has("name"), "示例数据应包含name字段");
        assertTrue(sampleData.has("status"), "示例数据应包含status字段");
        assertTrue(sampleData.has("powerSwitch"), "示例数据应包含powerSwitch字段");
        
        // 验证字段类型
        assertTrue(sampleData.get("id").isTextual(), "id字段应为字符串类型");
        assertTrue(sampleData.get("name").isTextual(), "name字段应为字符串类型");
        assertTrue(sampleData.get("status").isTextual(), "status字段应为字符串类型");
        assertTrue(sampleData.get("powerSwitch").isBoolean(), "powerSwitch字段应为布尔类型");
        
        // 验证枚举值
        String status = sampleData.get("status").asText();
        assertTrue(status.equals("on") || status.equals("off") || status.equals("error"), 
                  "status字段值应为枚举值之一");
        
        System.out.println("生成的示例数据:");
        System.out.println(sampleData.toPrettyString());
    }
    
    @Test
    void testCreateUniResource() {
        // 测试创建UniResource实例
        UniResource resource = temperatureSensorType.createUniResource("测试温度传感器");
        
        assertNotNull(resource, "创建的UniResource不应为null");
        assertNotNull(resource.getId(), "UniResource的id不应为null");
        assertEquals("temperature-sensor-type", resource.getTypeId(), "typeId应匹配");
        assertEquals("测试温度传感器", resource.getName(), "资源名称应匹配");
        assertNotNull(resource.getData(), "资源数据不应为null");
        assertTrue(resource.getData().isObject(), "资源数据应为对象类型");
        
        // 验证生成的数据包含必需字段
        JsonNode data = resource.getData();
        assertTrue(data.has("id"), "资源数据应包含id字段");
        assertTrue(data.has("name"), "资源数据应包含name字段");
        assertTrue(data.has("status"), "资源数据应包含status字段");
        assertTrue(data.has("powerSwitch"), "资源数据应包含powerSwitch字段");
        
        System.out.println("创建的UniResource:");
        System.out.println("ID: " + resource.getId());
        System.out.println("Name: " + resource.getName());
        System.out.println("TypeId: " + resource.getTypeId());
        System.out.println("Data: " + resource.getData().toPrettyString());
    }
    
    @Test
    void testCreateUniResourceWithCustomData() throws Exception {
        // 测试使用自定义数据创建UniResource
        String customDataJson = "{\"id\":\"custom-sensor-001\",\"name\":\"自定义传感器\",\"status\":\"on\",\"powerSwitch\":true}";
        JsonNode customData = objectMapper.readTree(customDataJson);
        
        UniResource resource = temperatureSensorType.createUniResource("自定义温度传感器", customData);
        
        assertNotNull(resource, "创建的UniResource不应为null");
        assertEquals("自定义温度传感器", resource.getName(), "资源名称应匹配");
        assertEquals(customData, resource.getData(), "资源数据应匹配自定义数据");
        
        // 验证自定义数据的内容
        JsonNode data = resource.getData();
        assertEquals("custom-sensor-001", data.get("id").asText(), "自定义id应匹配");
        assertEquals("自定义传感器", data.get("name").asText(), "自定义name应匹配");
        assertEquals("on", data.get("status").asText(), "自定义status应匹配");
        assertTrue(data.get("powerSwitch").asBoolean(), "自定义powerSwitch应匹配");
        
        System.out.println("使用自定义数据创建的UniResource:");
        System.out.println("Data: " + resource.getData().toPrettyString());
    }
    
    @Test
    void testGenerateSampleDataWithNullSchema() {
        // 测试schema为null时的异常处理
        UniResourceType typeWithNullSchema = new UniResourceType();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            typeWithNullSchema.generateSampleData();
        });
        
        assertEquals("Schema is null, cannot generate sample data", exception.getMessage());
    }
    
    @Test
    void testCreateUniResourceWithNullId() {
        // 测试id为null时的异常处理
        UniResourceType typeWithNullId = new UniResourceType();
        typeWithNullId.setSchema(temperatureSensorType.getSchema());
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            typeWithNullId.createUniResource("测试");
        });
        
        assertEquals("ResourceType id is null, cannot create UniResource", exception.getMessage());
    }
}