package com.uros.kernel.handle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ResourceResolutionResult 类的测试
 */
public class ResourceResolutionResultTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    void testSuccessResult() {
        String resourceId = "test-resource-id";
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ResourceInstance instance = new ResourceInstance("type-id", "TypeName", null);
        
        ResourceResolutionResult result = ResourceResolutionResult.success(resourceId, instance, type);
        
        assertTrue(result.isSuccess());
        assertEquals(resourceId, result.getResourceId());
        assertEquals(instance, result.getInstance());
        assertEquals(type, result.getType());
        assertNull(result.getErrorMessage());
    }
    
    @Test
    void testNotFoundResult() {
        String resourceId = "non-existent-id";
        
        ResourceResolutionResult result = ResourceResolutionResult.notFound(resourceId);
        
        assertFalse(result.isSuccess());
        assertEquals(resourceId, result.getResourceId());
        assertNull(result.getInstance());
        assertNull(result.getType());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains(resourceId));
        assertTrue(result.getErrorMessage().contains("not found"));
    }
    
    @Test
    void testTypeNotFoundResult() {
        String resourceId = "test-resource-id";
        String typeId = "non-existent-type-id";
        
        ResourceResolutionResult result = ResourceResolutionResult.typeNotFound(resourceId, typeId);
        
        assertFalse(result.isSuccess());
        assertEquals(resourceId, result.getResourceId());
        assertNull(result.getInstance());
        assertNull(result.getType());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains(resourceId));
        assertTrue(result.getErrorMessage().contains(typeId));
        assertTrue(result.getErrorMessage().contains("type"));
        assertTrue(result.getErrorMessage().contains("not found"));
    }
    
    @Test
    void testGetters() {
        String resourceId = "test-resource-id";
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ResourceInstance instance = new ResourceInstance("type-id", "TypeName", null);
        
        ResourceResolutionResult result = ResourceResolutionResult.success(resourceId, instance, type);
        
        assertEquals(resourceId, result.getResourceId());
        assertTrue(result.isSuccess());
        assertEquals(instance, result.getInstance());
        assertEquals(type, result.getType());
        assertNull(result.getErrorMessage());
    }
    
    @Test
    void testMultipleResults() {
        // 测试成功结果
        ResourceType type1 = new ResourceType("Type1", "Description1", null);
        ResourceInstance instance1 = new ResourceInstance("type1-id", "Type1", null);
        ResourceResolutionResult successResult = ResourceResolutionResult.success("id1", instance1, type1);
        
        assertTrue(successResult.isSuccess());
        assertEquals("id1", successResult.getResourceId());
        
        // 测试未找到结果
        ResourceResolutionResult notFoundResult = ResourceResolutionResult.notFound("id2");
        
        assertFalse(notFoundResult.isSuccess());
        assertEquals("id2", notFoundResult.getResourceId());
        
        // 测试类型未找到结果
        ResourceResolutionResult typeNotFoundResult = ResourceResolutionResult.typeNotFound("id3", "type3-id");
        
        assertFalse(typeNotFoundResult.isSuccess());
        assertEquals("id3", typeNotFoundResult.getResourceId());
    }
    
    @Test
    void testResultImmutability() {
        String resourceId = "test-resource-id";
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ResourceInstance instance = new ResourceInstance("type-id", "TypeName", null);
        
        ResourceResolutionResult result = ResourceResolutionResult.success(resourceId, instance, type);
        
        // 验证结果是不可变的
        String originalResourceId = result.getResourceId();
        boolean originalSuccess = result.isSuccess();
        
        // 即使修改了原始对象，结果应该保持不变
        instance.setStatus("DELETED");
        type.setDescription("Modified Description");
        
        assertEquals(originalResourceId, result.getResourceId());
        assertEquals(originalSuccess, result.isSuccess());
    }
}
