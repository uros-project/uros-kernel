package com.uros.kernel.handle.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uros.kernel.handle.model.UniResource;
import com.uros.kernel.handle.model.UniResourceType;
import com.uros.kernel.handle.service.UniResourceService;
import com.uros.kernel.handle.service.UniResourceTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 温度传感器示例测试类
 * 演示如何使用UniResourceType的新功能来生成示例数据和创建UniResource实例
 */
public class TemperatureSensorExampleTest {
    
    private UniResourceTypeService resourceTypeService;
    private UniResourceService resourceService;
    
    @BeforeEach
    public void setUp() {
        // 手动创建服务实例
        resourceTypeService = new UniResourceTypeService();
        resourceService = new UniResourceService();
        
        // 手动设置依赖关系（通过反射）
        try {
            java.lang.reflect.Field field = UniResourceService.class.getDeclaredField("resourceTypeService");
            field.setAccessible(true);
            field.set(resourceService, resourceTypeService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependency", e);
        }
    }
    
    @Test
    public void testTemperatureSensorExample() {
        try {
            // 1. 读取温度传感器的JSON Schema
            String schemaPath = "src/test/resources/schemas/temperature_sensor.schema.json";
            String schemaContent = Files.readString(Paths.get(schemaPath));
            
            // 2. 解析JSON Schema
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode schemaNode = objectMapper.readTree(schemaContent);
            
            // 3. 创建温度传感器资源类型
            UniResourceType temperatureSensorType = new UniResourceType(
                "temperature-sensor-v1",
                "Temperature Sensor",
                "IoT temperature sensor device type",
                "1.0",
                schemaNode
            );
            
            // 4. 保存资源类型
            UniResourceType savedType = resourceTypeService.createResourceType(temperatureSensorType);
            System.out.println("Created resource type: " + savedType.getName());
            
            // 5. 使用新功能生成示例数据
            JsonNode sampleData = savedType.generateSampleData();
            System.out.println("Generated sample data: " + sampleData);
            
            // 6. 基于示例数据创建UniResource实例
            UniResource sampleResource = savedType.createUniResource("Living Room Temperature Sensor");
            sampleResource.setId("temp-sensor-001");
            sampleResource.setDescription("Temperature sensor in the living room");
            
            // 7. 保存资源实例
            UniResource savedResource = resourceService.createResource(sampleResource);
            System.out.println("Created resource: " + savedResource.getName());
            System.out.println("Resource data: " + savedResource.getData());
            
            // 8. 创建多个示例实例
            createMultipleSensors(savedType);
            
            // 9. 查询和显示所有温度传感器
            displayAllTemperatureSensors();
            
            // 验证测试结果
            assert resourceTypeService.count() > 0 : "应该至少有一个资源类型";
            assert resourceService.count() > 0 : "应该至少有一个资源实例";
            
            System.out.println("TemperatureSensorExample测试通过！");
            
        } catch (IOException e) {
            System.err.println("Error reading schema file: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.err.println("Error running example: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 创建多个温度传感器实例
     */
    private void createMultipleSensors(UniResourceType sensorType) {
        String[] locations = {"Kitchen", "Bedroom", "Office", "Garage"};
        
        for (int i = 0; i < locations.length; i++) {
            try {
                UniResource sensor = sensorType.createUniResource(locations[i] + " Temperature Sensor");
                sensor.setId("temp-sensor-00" + (i + 2));
                sensor.setDescription("Temperature sensor in the " + locations[i].toLowerCase());
                
                UniResource savedSensor = resourceService.createResource(sensor);
                System.out.println("Created sensor: " + savedSensor.getName());
                
            } catch (Exception e) {
                System.err.println("Error creating sensor for " + locations[i] + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * 显示所有温度传感器
     */
    private void displayAllTemperatureSensors() {
        try {
            // 查找温度传感器类型
            List<UniResourceType> types = resourceTypeService.getResourceTypesByName("Temperature Sensor");
            
            for (UniResourceType type : types) {
                System.out.println("\n=== Temperature Sensors of type: " + type.getName() + " ===");
                
                // 查找该类型的所有资源
                List<UniResource> resources = resourceService.getResourcesByTypeId(type.getId());
                
                for (UniResource resource : resources) {
                    System.out.println("- " + resource.getName() + " (" + resource.getId() + ")");
                    System.out.println("  Status: " + resource.getStatus());
                    System.out.println("  Data: " + resource.getData());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error displaying sensors: " + e.getMessage());
        }
    }
}