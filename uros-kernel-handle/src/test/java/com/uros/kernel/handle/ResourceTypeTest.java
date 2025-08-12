package com.uros.kernel.handle;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

/** ResourceType 类的测试 */
public class ResourceTypeTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testDefaultConstructor() {
    ResourceType resourceType = new ResourceType();

    assertNotNull(resourceType.getId());
    assertNull(resourceType.getName());
    assertNull(resourceType.getDescription());
    assertNull(resourceType.getSchema());
    assertTrue(resourceType.getCreatedAt() > 0);
    assertTrue(resourceType.getUpdatedAt() > 0);
    assertEquals(resourceType.getCreatedAt(), resourceType.getUpdatedAt());
  }

  @Test
  void testParameterizedConstructor() {
    String name = "TestType";
    String description = "Test Description";
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    ResourceType resourceType = new ResourceType(name, description, schema);

    assertNotNull(resourceType.getId());
    assertEquals(name, resourceType.getName());
    assertEquals(description, resourceType.getDescription());
    assertEquals(schema, resourceType.getSchema());
    assertTrue(resourceType.getCreatedAt() > 0);
    assertTrue(resourceType.getUpdatedAt() > 0);
  }

  @Test
  void testSettersAndGetters() {
    ResourceType resourceType = new ResourceType();

    String id = "test-id";
    String name = "TestName";
    String description = "Test Description";
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "string");
    long createdAt = 1234567890L;
    long updatedAt = 1234567891L;

    resourceType.setId(id);
    resourceType.setName(name);
    resourceType.setDescription(description);
    resourceType.setSchema(schema);
    resourceType.setCreatedAt(createdAt);
    resourceType.setUpdatedAt(updatedAt);

    assertEquals(id, resourceType.getId());
    assertEquals(name, resourceType.getName());
    assertEquals(description, resourceType.getDescription());
    assertEquals(schema, resourceType.getSchema());
    assertEquals(createdAt, resourceType.getCreatedAt());
    assertEquals(updatedAt, resourceType.getUpdatedAt());
  }

  @Test
  void testSchemaUpdateUpdatesTimestamp() {
    ResourceType resourceType = new ResourceType();
    long originalUpdatedAt = resourceType.getUpdatedAt();

    // 等待一小段时间确保时间戳不同
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    ObjectNode newSchema = objectMapper.createObjectNode();
    newSchema.put("type", "integer");
    resourceType.setSchema(newSchema);

    assertTrue(resourceType.getUpdatedAt() > originalUpdatedAt);
    assertEquals(newSchema, resourceType.getSchema());
  }

  @Test
  void testIdUniqueness() {
    ResourceType type1 = new ResourceType();
    ResourceType type2 = new ResourceType();

    assertNotEquals(type1.getId(), type2.getId());
    assertNotNull(type1.getId());
    assertNotNull(type2.getId());
    assertTrue(type1.getId().length() > 0);
    assertTrue(type2.getId().length() > 0);
  }

  @Test
  void testTimestampFormat() {
    ResourceType resourceType = new ResourceType();

    long currentTime = System.currentTimeMillis();
    long createdAt = resourceType.getCreatedAt();
    long updatedAt = resourceType.getUpdatedAt();

    // 时间戳应该在合理范围内（当前时间前后1秒内）
    assertTrue(Math.abs(createdAt - currentTime) < 1000);
    assertTrue(Math.abs(updatedAt - currentTime) < 1000);
  }
}
