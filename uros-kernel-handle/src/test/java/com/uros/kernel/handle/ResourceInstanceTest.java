package com.uros.kernel.handle;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

/** ResourceInstance 类的测试 */
public class ResourceInstanceTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testDefaultConstructor() {
    ResourceInstance instance = new ResourceInstance();

    assertNotNull(instance.getId());
    assertNull(instance.getTypeId());
    assertNull(instance.getTypeName());
    assertNull(instance.getData());
    assertTrue(instance.getCreatedAt() > 0);
    assertTrue(instance.getUpdatedAt() > 0);
    assertEquals(instance.getCreatedAt(), instance.getUpdatedAt());
    assertEquals("ACTIVE", instance.getStatus());
  }

  @Test
  void testParameterizedConstructor() {
    String typeId = "user-type-id";
    String typeName = "User";
    ObjectNode data = objectMapper.createObjectNode();
    data.put("name", "张三");
    data.put("age", 25);

    ResourceInstance instance = new ResourceInstance(typeId, typeName, data);

    assertNotNull(instance.getId());
    assertEquals(typeId, instance.getTypeId());
    assertEquals(typeName, instance.getTypeName());
    assertEquals(data, instance.getData());
    assertEquals("ACTIVE", instance.getStatus());
  }

  @Test
  void testSettersAndGetters() {
    ResourceInstance instance = new ResourceInstance();

    String id = "test-instance-id";
    String typeId = "test-type-id";
    String typeName = "TestType";
    ObjectNode data = objectMapper.createObjectNode();
    data.put("test", "value");
    long createdAt = 1234567890L;
    long updatedAt = 1234567891L;
    String status = "INACTIVE";

    instance.setId(id);
    instance.setTypeId(typeId);
    instance.setTypeName(typeName);
    instance.setData(data);
    instance.setCreatedAt(createdAt);
    instance.setUpdatedAt(updatedAt);
    instance.setStatus(status);

    assertEquals(id, instance.getId());
    assertEquals(typeId, instance.getTypeId());
    assertEquals(typeName, instance.getTypeName());
    assertEquals(data, instance.getData());
    assertEquals(createdAt, instance.getCreatedAt());
    assertEquals(updatedAt, instance.getUpdatedAt());
    assertEquals(status, instance.getStatus());
  }

  @Test
  void testDataUpdateUpdatesTimestamp() {
    ResourceInstance instance = new ResourceInstance();
    long originalUpdatedAt = instance.getUpdatedAt();

    // 等待一小段时间确保时间戳不同
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    ObjectNode newData = objectMapper.createObjectNode();
    newData.put("newField", "newValue");
    instance.setData(newData);

    assertTrue(instance.getUpdatedAt() > originalUpdatedAt);
    assertEquals(newData, instance.getData());
  }

  @Test
  void testStatusUpdateUpdatesTimestamp() {
    ResourceInstance instance = new ResourceInstance();
    long originalUpdatedAt = instance.getUpdatedAt();

    // 等待一小段时间确保时间戳不同
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    instance.setStatus("DELETED");

    assertTrue(instance.getUpdatedAt() > originalUpdatedAt);
    assertEquals("DELETED", instance.getStatus());
  }

  @Test
  void testIdUniqueness() {
    ResourceInstance instance1 = new ResourceInstance();
    ResourceInstance instance2 = new ResourceInstance();

    assertNotEquals(instance1.getId(), instance2.getId());
    assertNotNull(instance1.getId());
    assertNotNull(instance2.getId());
    assertTrue(instance1.getId().length() > 0);
    assertTrue(instance2.getId().length() > 0);
  }

  @Test
  void testTimestampFormat() {
    ResourceInstance instance = new ResourceInstance();

    long currentTime = System.currentTimeMillis();
    long createdAt = instance.getCreatedAt();
    long updatedAt = instance.getUpdatedAt();

    // 时间戳应该在合理范围内（当前时间前后1秒内）
    assertTrue(Math.abs(createdAt - currentTime) < 1000);
    assertTrue(Math.abs(updatedAt - currentTime) < 1000);
  }

  @Test
  void testStatusValues() {
    ResourceInstance instance = new ResourceInstance();

    // 测试默认状态
    assertEquals("ACTIVE", instance.getStatus());

    // 测试设置不同状态
    instance.setStatus("INACTIVE");
    assertEquals("INACTIVE", instance.getStatus());

    instance.setStatus("DELETED");
    assertEquals("DELETED", instance.getStatus());

    instance.setStatus("ACTIVE");
    assertEquals("ACTIVE", instance.getStatus());
  }

  @Test
  void testDataContent() {
    ObjectNode data = objectMapper.createObjectNode();
    data.put("stringField", "string value");
    data.put("intField", 42);
    data.put("boolField", true);
    data.put("nullField", (String) null);

    ResourceInstance instance = new ResourceInstance("type-id", "TypeName", data);

    assertEquals("string value", instance.getData().get("stringField").asText());
    assertEquals(42, instance.getData().get("intField").asInt());
    assertTrue(instance.getData().get("boolField").asBoolean());
    assertTrue(instance.getData().get("nullField").isNull());
  }
}
