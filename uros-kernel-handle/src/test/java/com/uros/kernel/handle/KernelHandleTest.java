package com.uros.kernel.handle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * KernelHandle类的测试类
 */
public class KernelHandleTest {
    
    private KernelHandle kernelHandle;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        kernelHandle = new KernelHandle();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testRegisterResourceType() {
        // 测试注册资源类型
        ObjectNode schema = objectMapper.createObjectNode();
        
        ResourceType type = kernelHandle.registerResourceType("User", "用户资源类型", schema);
        
        assertNotNull(type);
        assertEquals("User", type.getName());
        assertEquals("用户资源类型", type.getDescription());
        assertEquals(1, kernelHandle.getResourceTypeCount());
    }
    
    @Test
    void testCreateResourceInstance() {
        // 先注册资源类型
        ObjectNode schema = objectMapper.createObjectNode();
        kernelHandle.registerResourceType("User", "用户资源类型", schema);
        
        // 创建资源实例
        ObjectNode userData = objectMapper.createObjectNode();
        userData.put("name", "张三");
        userData.put("age", 25);
        
        ResourceInstance instance = kernelHandle.createResourceInstance("User", userData);
        
        assertNotNull(instance);
        assertEquals("User", instance.getTypeName());
        assertEquals("张三", instance.getData().get("name").asText());
        assertEquals(25, instance.getData().get("age").asInt());
        assertEquals(1, kernelHandle.getResourceInstanceCount());
    }
    
    @Test
    void testResolveResource() {
        // 先注册资源类型
        ObjectNode schema = objectMapper.createObjectNode();
        kernelHandle.registerResourceType("User", "用户资源类型", schema);
        
        // 创建资源实例
        ObjectNode userData = objectMapper.createObjectNode();
        userData.put("name", "李四");
        ResourceInstance instance = kernelHandle.createResourceInstance("User", userData);
        
        // 测试资源解析
        ResourceResolutionResult result = kernelHandle.resolveResource(instance.getId());
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getInstance());
        assertNotNull(result.getType());
        assertEquals("User", result.getType().getName());
        assertEquals("李四", result.getInstance().getData().get("name").asText());
    }
    
    @Test
    void testUpdateResourceInstance() {
        // 先注册资源类型
        ObjectNode schema = objectMapper.createObjectNode();
        kernelHandle.registerResourceType("User", "用户资源类型", schema);
        
        // 创建资源实例
        ObjectNode userData = objectMapper.createObjectNode();
        userData.put("name", "王五");
        ResourceInstance instance = kernelHandle.createResourceInstance("User", userData);
        
        // 更新资源实例
        ObjectNode newData = objectMapper.createObjectNode();
        newData.put("name", "王五更新");
        newData.put("age", 30);
        
        ResourceInstance updatedInstance = kernelHandle.updateResourceInstance(instance.getId(), newData);
        
        assertEquals("王五更新", updatedInstance.getData().get("name").asText());
        assertEquals(30, updatedInstance.getData().get("age").asInt());
    }
    
    @Test
    void testDeleteResourceInstance() {
        // 先注册资源类型
        ObjectNode schema = objectMapper.createObjectNode();
        kernelHandle.registerResourceType("User", "用户资源类型", schema);
        
        // 创建资源实例
        ObjectNode userData = objectMapper.createObjectNode();
        userData.put("name", "赵六");
        ResourceInstance instance = kernelHandle.createResourceInstance("User", userData);
        
        assertEquals(1, kernelHandle.getResourceInstanceCount());
        
        // 删除资源实例
        boolean deleted = kernelHandle.deleteResourceInstance(instance.getId());
        
        assertTrue(deleted);
        assertEquals(0, kernelHandle.getResourceInstanceCount());
    }
    
    @Test
    void testGetCompleteResourceInfo() {
        // 先注册资源类型
        ObjectNode schema = objectMapper.createObjectNode();
        kernelHandle.registerResourceType("User", "用户资源类型", schema);
        
        // 创建资源实例
        ObjectNode userData = objectMapper.createObjectNode();
        userData.put("name", "钱七");
        ResourceInstance instance = kernelHandle.createResourceInstance("User", userData);
        
        // 获取完整资源信息
        CompleteResourceInfo info = kernelHandle.getCompleteResourceInfo(instance.getId());
        
        assertNotNull(info);
        assertEquals(instance.getId(), info.getResourceId());
        assertEquals("User", info.getTypeName());
        assertEquals("钱七", info.getResourceData().get("name").asText());
        assertTrue(info.isActive());
    }
    
    @Test
    void testValidateResource() {
        // 先注册资源类型
        ObjectNode schema = objectMapper.createObjectNode();
        kernelHandle.registerResourceType("User", "用户资源类型", schema);
        
        // 创建资源实例
        ObjectNode userData = objectMapper.createObjectNode();
        userData.put("name", "孙八");
        ResourceInstance instance = kernelHandle.createResourceInstance("User", userData);
        
        // 验证资源
        ValidationResult result = kernelHandle.validateResource(instance.getId());
        
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }
    
    @Test
    void testResourceNotFound() {
        // 测试解析不存在的资源
        ResourceResolutionResult result = kernelHandle.resolveResource("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("not found"));
    }
}
