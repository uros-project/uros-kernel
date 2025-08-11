package com.uros.kernel.handle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CompleteResourceInfo 类的测试
 */
public class CompleteResourceInfoTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    void testConstructor() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ResourceInstance instance = new ResourceInstance("type-id", "TypeName", null);
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        
        CompleteResourceInfo info = new CompleteResourceInfo(instance, type, schema);
        
        assertEquals(instance, info.getInstance());
        assertEquals(type, info.getType());
        assertEquals(schema, info.getSchema());
    }
    
    @Test
    void testGetResourceId() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ResourceInstance instance = new ResourceInstance("type-id", "TypeName", null);
        ObjectNode schema = objectMapper.createObjectNode();
        
        CompleteResourceInfo info = new CompleteResourceInfo(instance, type, schema);
        
        assertEquals(instance.getId(), info.getResourceId());
    }
    
    @Test
    void testGetResourceIdWithNullInstance() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ObjectNode schema = objectMapper.createObjectNode();
        
        CompleteResourceInfo info = new CompleteResourceInfo(null, type, schema);
        
        assertNull(info.getResourceId());
    }
    
    @Test
    void testGetTypeName() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ResourceInstance instance = new ResourceInstance("type-id", "TypeName", null);
        ObjectNode schema = objectMapper.createObjectNode();
        
        CompleteResourceInfo info = new CompleteResourceInfo(instance, type, schema);
        
        assertEquals(type.getName(), info.getTypeName());
    }
    
    @Test
    void testGetTypeNameWithNullType() {
        ResourceInstance instance = new ResourceInstance("type-id", "TypeName", null);
        ObjectNode schema = objectMapper.createObjectNode();
        
        CompleteResourceInfo info = new CompleteResourceInfo(instance, null, schema);
        
        assertNull(info.getTypeName());
    }
    
    @Test
    void testGetResourceData() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ObjectNode data = objectMapper.createObjectNode();
        data.put("name", "test");
        ResourceInstance instance = new ResourceInstance("type-id", "TypeName", data);
        ObjectNode schema = objectMapper.createObjectNode();
        
        CompleteResourceInfo info = new CompleteResourceInfo(instance, type, schema);
        
        assertEquals(data, info.getResourceData());
        assertEquals("test", info.getResourceData().get("name").asText());
    }
    
    @Test
    void testGetResourceDataWithNullInstance() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ObjectNode schema = objectMapper.createObjectNode();
        
        CompleteResourceInfo info = new CompleteResourceInfo(null, type, schema);
        
        assertNull(info.getResourceData());
    }
    
    @Test
    void testGetResourceStatus() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ResourceInstance instance = new ResourceInstance("type-id", "TypeName", null);
        ObjectNode schema = objectMapper.createObjectNode();
        
        CompleteResourceInfo info = new CompleteResourceInfo(instance, type, schema);
        
        assertEquals(instance.getStatus(), info.getResourceStatus());
        assertEquals("ACTIVE", info.getResourceStatus());
    }
    
    @Test
    void testGetResourceStatusWithNullInstance() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ObjectNode schema = objectMapper.createObjectNode();
        
        CompleteResourceInfo info = new CompleteResourceInfo(null, type, schema);
        
        assertNull(info.getResourceStatus());
    }
    
    @Test
    void testIsActive() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ObjectNode schema = objectMapper.createObjectNode();
        
        // 测试 ACTIVE 状态
        ResourceInstance activeInstance = new ResourceInstance("type-id", "TypeName", null);
        activeInstance.setStatus("ACTIVE");
        
        CompleteResourceInfo activeInfo = new CompleteResourceInfo(activeInstance, type, schema);
        assertTrue(activeInfo.isActive());
        
        // 测试 INACTIVE 状态
        ResourceInstance inactiveInstance = new ResourceInstance("type-id", "TypeName", null);
        inactiveInstance.setStatus("INACTIVE");
        
        CompleteResourceInfo inactiveInfo = new CompleteResourceInfo(inactiveInstance, type, schema);
        assertFalse(inactiveInfo.isActive());
        
        // 测试 DELETED 状态
        ResourceInstance deletedInstance = new ResourceInstance("type-id", "TypeName", null);
        deletedInstance.setStatus("DELETED");
        
        CompleteResourceInfo deletedInfo = new CompleteResourceInfo(deletedInstance, type, schema);
        assertFalse(deletedInfo.isActive());
    }
    
    @Test
    void testIsActiveWithNullInstance() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ObjectNode schema = objectMapper.createObjectNode();
        
        CompleteResourceInfo info = new CompleteResourceInfo(null, type, schema);
        
        assertFalse(info.isActive());
    }
    
    @Test
    void testNullHandling() {
        CompleteResourceInfo info = new CompleteResourceInfo(null, null, null);
        
        assertNull(info.getResourceId());
        assertNull(info.getTypeName());
        assertNull(info.getResourceData());
        assertNull(info.getResourceStatus());
        assertFalse(info.isActive());
        assertNull(info.getInstance());
        assertNull(info.getType());
        assertNull(info.getSchema());
    }
    
    @Test
    void testDataContent() {
        ResourceType type = new ResourceType("TestType", "Test Description", null);
        ObjectNode data = objectMapper.createObjectNode();
        data.put("stringField", "string value");
        data.put("intField", 42);
        data.put("boolField", true);
        data.put("nullField", (String) null);
        
        ResourceInstance instance = new ResourceInstance("type-id", "TypeName", data);
        ObjectNode schema = objectMapper.createObjectNode();
        
        CompleteResourceInfo info = new CompleteResourceInfo(instance, type, schema);
        
        assertEquals("string value", info.getResourceData().get("stringField").asText());
        assertEquals(42, info.getResourceData().get("intField").asInt());
        assertTrue(info.getResourceData().get("boolField").asBoolean());
        assertTrue(info.getResourceData().get("nullField").isNull());
    }
}
