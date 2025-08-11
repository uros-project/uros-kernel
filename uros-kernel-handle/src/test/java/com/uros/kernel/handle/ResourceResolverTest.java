package com.uros.kernel.handle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ResourceResolver 类的测试
 */
public class ResourceResolverTest {
    
    private ResourceResolver resolver;
    private ResourceTypeRegistry typeRegistry;
    private ResourceInstanceManager instanceManager;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        typeRegistry = new ResourceTypeRegistry();
        instanceManager = new ResourceInstanceManager();
        resolver = new ResourceResolver(typeRegistry, instanceManager);
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testResolveResourceSuccess() {
        // 注册资源类型
        String typeName = "User";
        String description = "用户资源类型";
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        
        ResourceType resourceType = typeRegistry.registerResourceType(typeName, description, schema);
        
        // 创建资源实例
        ObjectNode data = objectMapper.createObjectNode();
        data.put("name", "张三");
        data.put("age", 25);
        
        ResourceInstance instance = instanceManager.createResourceInstance(
            resourceType.getId(), resourceType.getName(), data);
        
        // 解析资源
        ResourceResolutionResult result = resolver.resolveResource(instance.getId());
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getInstance());
        assertNotNull(result.getType());
        assertEquals(instance.getId(), result.getResourceId());
        assertEquals(resourceType.getName(), result.getType().getName());
        assertEquals(instance.getData(), result.getInstance().getData());
    }
    
    @Test
    void testResolveResourceNotFound() {
        ResourceResolutionResult result = resolver.resolveResource("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("not found"));
        assertNull(result.getInstance());
        assertNull(result.getType());
    }
    
    @Test
    void testResolveResourceTypeNotFound() {
        // 创建资源实例但不注册类型
        ObjectNode data = objectMapper.createObjectNode();
        data.put("name", "test");
        
        ResourceInstance instance = instanceManager.createResourceInstance(
            "non-existent-type-id", "TestType", data);
        
        // 解析资源应该失败，因为类型不存在
        ResourceResolutionResult result = resolver.resolveResource(instance.getId());
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("type"));
        assertTrue(result.getErrorMessage().contains("not found"));
    }
    
    @Test
    void testResolveResourceField() {
        // 注册资源类型
        String typeName = "User";
        String description = "用户资源类型";
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        
        ResourceType resourceType = typeRegistry.registerResourceType(typeName, description, schema);
        
        // 创建资源实例
        ObjectNode data = objectMapper.createObjectNode();
        data.put("name", "李四");
        data.put("age", 30);
        
        ResourceInstance instance = instanceManager.createResourceInstance(
            resourceType.getId(), resourceType.getName(), data);
        
        // 解析字段
        var fieldValue = resolver.resolveResourceField(instance.getId(), "name");
        
        assertTrue(fieldValue.isPresent());
        assertEquals("李四", fieldValue.get().get("name").asText());
    }
    
    @Test
    void testResolveResourceFieldNotFound() {
        var fieldValue = resolver.resolveResourceField("non-existent-id", "field");
        
        assertFalse(fieldValue.isPresent());
    }
    
    @Test
    void testValidateResourceSuccess() {
        // 注册资源类型
        String typeName = "User";
        String description = "用户资源类型";
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        
        ResourceType resourceType = typeRegistry.registerResourceType(typeName, description, schema);
        
        // 创建资源实例
        ObjectNode data = objectMapper.createObjectNode();
        data.put("name", "王五");
        
        ResourceInstance instance = instanceManager.createResourceInstance(
            resourceType.getId(), resourceType.getName(), data);
        
        // 验证资源
        ValidationResult result = resolver.validateResource(instance.getId());
        
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }
    
    @Test
    void testValidateResourceNotFound() {
        ValidationResult result = resolver.validateResource("non-existent-id");
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.getFirstError().contains("not found"));
    }
    
    @Test
    void testGetCompleteResourceInfo() {
        // 注册资源类型
        String typeName = "User";
        String description = "用户资源类型";
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        schema.put("properties", "user properties");
        
        ResourceType resourceType = typeRegistry.registerResourceType(typeName, description, schema);
        
        // 创建资源实例
        ObjectNode data = objectMapper.createObjectNode();
        data.put("name", "赵六");
        data.put("email", "zhaoliu@example.com");
        
        ResourceInstance instance = instanceManager.createResourceInstance(
            resourceType.getId(), resourceType.getName(), data);
        
        // 获取完整资源信息
        CompleteResourceInfo info = resolver.getCompleteResourceInfo(instance.getId());
        
        assertNotNull(info);
        assertEquals(instance.getId(), info.getResourceId());
        assertEquals(resourceType.getName(), info.getTypeName());
        assertEquals(instance.getData(), info.getResourceData());
        assertEquals(resourceType.getSchema(), info.getSchema());
        assertTrue(info.isActive());
    }
    
    @Test
    void testGetCompleteResourceInfoNotFound() {
        CompleteResourceInfo info = resolver.getCompleteResourceInfo("non-existent-id");
        
        assertNull(info);
    }
    
    @Test
    void testIsResourceAccessible() {
        // 注册资源类型
        String typeName = "User";
        String description = "用户资源类型";
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        
        ResourceType resourceType = typeRegistry.registerResourceType(typeName, description, schema);
        
        // 创建资源实例
        ObjectNode data = objectMapper.createObjectNode();
        data.put("name", "钱七");
        
        ResourceInstance instance = instanceManager.createResourceInstance(
            resourceType.getId(), resourceType.getName(), data);
        
        // 测试可访问性
        assertTrue(resolver.isResourceAccessible(instance.getId()));
        
        // 测试不存在的资源
        assertFalse(resolver.isResourceAccessible("non-existent-id"));
    }
    
    @Test
    void testIsResourceAccessibleInactive() {
        // 注册资源类型
        String typeName = "User";
        String description = "用户资源类型";
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        
        ResourceType resourceType = typeRegistry.registerResourceType(typeName, description, schema);
        
        // 创建资源实例
        ObjectNode data = objectMapper.createObjectNode();
        data.put("name", "孙八");
        
        ResourceInstance instance = instanceManager.createResourceInstance(
            resourceType.getId(), resourceType.getName(), data);
        
        // 设置为非活动状态
        instance.setStatus("INACTIVE");
        
        // 测试不可访问性
        assertFalse(resolver.isResourceAccessible(instance.getId()));
    }
    
    @Test
    void testResolveMultipleResources() {
        // 注册资源类型
        String typeName = "User";
        String description = "用户资源类型";
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        
        ResourceType resourceType = typeRegistry.registerResourceType(typeName, description, schema);
        
        // 创建多个资源实例
        ObjectNode data1 = objectMapper.createObjectNode();
        data1.put("name", "用户1");
        ObjectNode data2 = objectMapper.createObjectNode();
        data2.put("name", "用户2");
        
        ResourceInstance instance1 = instanceManager.createResourceInstance(
            resourceType.getId(), resourceType.getName(), data1);
        ResourceInstance instance2 = instanceManager.createResourceInstance(
            resourceType.getId(), resourceType.getName(), data2);
        
        // 解析多个资源
        ResourceResolutionResult result1 = resolver.resolveResource(instance1.getId());
        ResourceResolutionResult result2 = resolver.resolveResource(instance2.getId());
        
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertEquals("用户1", result1.getInstance().getData().get("name").asText());
        assertEquals("用户2", result2.getInstance().getData().get("name").asText());
    }
}
