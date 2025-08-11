package com.uros.kernel.handle.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.uros.kernel.handle.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Handle 模块集成测试
 * 测试整个系统的协作功能
 */
public class HandleIntegrationTest {
    
    private HandleKernel kernelHandle;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        kernelHandle = new HandleKernel();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testCompleteWorkflow() {
        // 1. 注册资源类型
        String typeName = "User";
        String description = "用户资源类型";
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        ObjectNode properties = schema.putObject("properties");
        properties.putObject("name").put("type", "string");
        properties.putObject("age").put("type", "integer");
        properties.putObject("email").put("type", "string");
        
        ResourceType userType = kernelHandle.registerResourceType(typeName, description, schema);
        
        assertNotNull(userType);
        assertEquals(typeName, userType.getName());
        assertEquals(1, kernelHandle.getResourceTypeCount());
        
        // 2. 创建资源实例
        ObjectNode userData = objectMapper.createObjectNode();
        userData.put("name", "张三");
        userData.put("age", 25);
        userData.put("email", "zhangsan@example.com");
        
        ResourceInstance userInstance = kernelHandle.createResourceInstance(typeName, userData);
        
        assertNotNull(userInstance);
        assertEquals(userType.getId(), userInstance.getTypeId());
        assertEquals(typeName, userInstance.getTypeName());
        assertEquals("张三", userInstance.getData().get("name").asText());
        assertEquals(25, userInstance.getData().get("age").asInt());
        assertEquals(1, kernelHandle.getResourceInstanceCount());
        
        // 3. 解析资源
        ResourceResolutionResult resolutionResult = kernelHandle.resolveResource(userInstance.getId());
        
        assertTrue(resolutionResult.isSuccess());
        assertNotNull(resolutionResult.getInstance());
        assertNotNull(resolutionResult.getType());
        assertEquals(userInstance.getId(), resolutionResult.getResourceId());
        assertEquals(userType.getName(), resolutionResult.getType().getName());
        
        // 4. 获取完整资源信息
        CompleteResourceInfo completeInfo = kernelHandle.getCompleteResourceInfo(userInstance.getId());
        
        assertNotNull(completeInfo);
        assertEquals(userInstance.getId(), completeInfo.getResourceId());
        assertEquals(userType.getName(), completeInfo.getTypeName());
        assertEquals(userInstance.getData(), completeInfo.getResourceData());
        assertEquals(userType.getSchema(), completeInfo.getSchema());
        assertTrue(completeInfo.isActive());
        
        // 5. 验证资源
        ValidationResult validationResult = kernelHandle.validateResource(userInstance.getId());
        
        assertTrue(validationResult.isValid());
        assertFalse(validationResult.hasErrors());
        
        // 6. 更新资源实例
        ObjectNode updatedData = objectMapper.createObjectNode();
        updatedData.put("name", "张三更新");
        updatedData.put("age", 26);
        updatedData.put("email", "zhangsan.updated@example.com");
        
        ResourceInstance updatedInstance = kernelHandle.updateResourceInstance(userInstance.getId(), updatedData);
        
        assertEquals("张三更新", updatedInstance.getData().get("name").asText());
        assertEquals(26, updatedInstance.getData().get("age").asInt());
        assertTrue(updatedInstance.getUpdatedAt() > userInstance.getUpdatedAt());
        
        // 7. 查询统计信息
        assertEquals(1, kernelHandle.getResourceTypeCount());
        assertEquals(1, kernelHandle.getResourceInstanceCount());
        assertEquals(1, kernelHandle.getInstanceCountByType(typeName));
    }
    
    @Test
    void testMultipleResourceTypesAndInstances() {
        // 注册多个资源类型
        ObjectNode userSchema = objectMapper.createObjectNode();
        userSchema.put("type", "object");
        userSchema.putObject("properties").putObject("name").put("type", "string");
        
        ObjectNode productSchema = objectMapper.createObjectNode();
        productSchema.put("type", "object");
        productSchema.putObject("properties").putObject("title").put("type", "string");
        
        ResourceType userType = kernelHandle.registerResourceType("User", "用户类型", userSchema);
        ResourceType productType = kernelHandle.registerResourceType("Product", "产品类型", productSchema);
        
        assertEquals(2, kernelHandle.getResourceTypeCount());
        
        // 创建多个资源实例
        ObjectNode userData1 = objectMapper.createObjectNode();
        userData1.put("name", "用户1");
        ObjectNode userData2 = objectMapper.createObjectNode();
        userData2.put("name", "用户2");
        
        ObjectNode productData1 = objectMapper.createObjectNode();
        productData1.put("title", "产品1");
        ObjectNode productData2 = objectMapper.createObjectNode();
        productData2.put("title", "产品2");
        
        ResourceInstance user1 = kernelHandle.createResourceInstance("User", userData1);
        ResourceInstance user2 = kernelHandle.createResourceInstance("User", userData2);
        ResourceInstance product1 = kernelHandle.createResourceInstance("Product", productData1);
        ResourceInstance product2 = kernelHandle.createResourceInstance("Product", productData2);
        
        assertEquals(4, kernelHandle.getResourceInstanceCount());
        assertEquals(2, kernelHandle.getInstanceCountByType("User"));
        assertEquals(2, kernelHandle.getInstanceCountByType("Product"));
        
        // 验证所有实例都可以正确解析
        assertTrue(kernelHandle.resolveResource(user1.getId()).isSuccess());
        assertTrue(kernelHandle.resolveResource(user2.getId()).isSuccess());
        assertTrue(kernelHandle.resolveResource(product1.getId()).isSuccess());
        assertTrue(kernelHandle.resolveResource(product2.getId()).isSuccess());
    }
    
    @Test
    void testResourceLifecycle() {
        // 1. 创建资源类型和实例
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        
        ResourceType type = kernelHandle.registerResourceType("TestType", "测试类型", schema);
        ObjectNode data = objectMapper.createObjectNode();
        data.put("value", "test");
        
        ResourceInstance instance = kernelHandle.createResourceInstance("TestType", data);
        
        // 2. 验证资源存在且可访问
        assertTrue(kernelHandle.resolveResource(instance.getId()).isSuccess());
        assertTrue(kernelHandle.getCompleteResourceInfo(instance.getId()).isActive());
        
        // 3. 更新资源状态
        kernelHandle.updateResourceInstanceStatus(instance.getId(), "INACTIVE");
        
        // 4. 验证状态更新
        ResourceInstance updatedInstance = kernelHandle.getResourceInstanceById(instance.getId());
        assertEquals("INACTIVE", updatedInstance.getStatus());
        
        // 5. 删除资源实例
        boolean deleted = kernelHandle.deleteResourceInstance(instance.getId());
        assertTrue(deleted);
        
        // 6. 验证资源已被删除
        assertNull(kernelHandle.getResourceInstanceById(instance.getId()));
        assertEquals(0, kernelHandle.getResourceInstanceCount());
        
        // 7. 删除资源类型
        boolean typeDeleted = kernelHandle.deleteResourceType(type.getId());
        assertTrue(typeDeleted);
        
        // 8. 验证类型已被删除
        assertNull(kernelHandle.getResourceTypeById(type.getId()));
        assertEquals(0, kernelHandle.getResourceTypeCount());
    }
    
    @Test
    void testErrorHandling() {
        // 测试创建不存在的资源类型
        ObjectNode data = objectMapper.createObjectNode();
        data.put("name", "test");
        
        assertThrows(IllegalArgumentException.class, () -> {
            kernelHandle.createResourceInstance("NonExistentType", data);
        });
        
        // 测试解析不存在的资源
        ResourceResolutionResult result = kernelHandle.resolveResource("non-existent-id");
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
        
        // 测试获取不存在的资源信息
        CompleteResourceInfo info = kernelHandle.getCompleteResourceInfo("non-existent-id");
        assertNull(info);
        
        // 测试更新不存在的资源
        assertThrows(IllegalArgumentException.class, () -> {
            kernelHandle.updateResourceInstance("non-existent-id", data);
        });
        
        // 测试删除不存在的资源
        boolean deleted = kernelHandle.deleteResourceInstance("non-existent-id");
        assertFalse(deleted);
    }
    
    @Test
    void testConcurrentOperations() throws InterruptedException {
        // 创建资源类型
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        
        ResourceType type = kernelHandle.registerResourceType("ConcurrentType", "并发测试类型", schema);
        
        // 创建多个线程同时操作
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    ObjectNode data = objectMapper.createObjectNode();
                    data.put("index", index);
                    data.put("thread", Thread.currentThread().getName());
                    
                    ResourceInstance instance = kernelHandle.createResourceInstance("ConcurrentType", data);
                    assertNotNull(instance);
                    
                    // 验证可以正确解析
                    ResourceResolutionResult result = kernelHandle.resolveResource(instance.getId());
                    assertTrue(result.isSuccess());
                    
                } catch (Exception e) {
                    fail("线程 " + index + " 执行失败: " + e.getMessage());
                }
            });
        }
        
        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 验证所有实例都创建成功
        assertEquals(threadCount, kernelHandle.getResourceInstanceCount());
        assertEquals(threadCount, kernelHandle.getInstanceCountByType("ConcurrentType"));
    }
}
