package com.uros.kernel.handle;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** ResourceTypeRegistry 类的测试 */
public class ResourceTypeRegistryTest {

  private ResourceTypeRegistry registry;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    registry = new ResourceTypeRegistry();
    objectMapper = new ObjectMapper();
  }

  @Test
  void testRegisterResourceType() {
    String name = "User";
    String description = "用户资源类型";
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    ResourceType resourceType = registry.registerResourceType(name, description, schema);

    assertNotNull(resourceType);
    assertEquals(name, resourceType.getName());
    assertEquals(description, resourceType.getDescription());
    assertEquals(schema, resourceType.getSchema());
    assertEquals(1, registry.getTypeCount());
  }

  @Test
  void testRegisterResourceTypeWithDuplicateName() {
    String name = "User";
    String description = "用户资源类型";
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    // 第一次注册
    ResourceType firstType = registry.registerResourceType(name, description, schema);
    assertNotNull(firstType);

    // 第二次注册同名类型应该抛出异常
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          registry.registerResourceType(name, "Another description", schema);
        });

    assertEquals(1, registry.getTypeCount());
  }

  @Test
  void testGetResourceTypeById() {
    String name = "User";
    String description = "用户资源类型";
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    ResourceType registeredType = registry.registerResourceType(name, description, schema);
    String typeId = registeredType.getId();

    ResourceType retrievedType = registry.getResourceTypeById(typeId);

    assertNotNull(retrievedType);
    assertEquals(registeredType.getId(), retrievedType.getId());
    assertEquals(registeredType.getName(), retrievedType.getName());
  }

  @Test
  void testGetResourceTypeByIdNotFound() {
    ResourceType retrievedType = registry.getResourceTypeById("non-existent-id");
    assertNull(retrievedType);
  }

  @Test
  void testGetResourceTypeByName() {
    String name = "User";
    String description = "用户资源类型";
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    ResourceType registeredType = registry.registerResourceType(name, description, schema);

    ResourceType retrievedType = registry.getResourceTypeByName(name);

    assertNotNull(retrievedType);
    assertEquals(registeredType.getId(), retrievedType.getId());
    assertEquals(registeredType.getName(), retrievedType.getName());
  }

  @Test
  void testGetResourceTypeByNameNotFound() {
    ResourceType retrievedType = registry.getResourceTypeByName("non-existent-name");
    assertNull(retrievedType);
  }

  @Test
  void testGetAllResourceTypes() {
    assertEquals(0, registry.getAllResourceTypes().size());

    // 注册多个类型
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    registry.registerResourceType("User", "用户类型", schema);
    registry.registerResourceType("Product", "产品类型", schema);
    registry.registerResourceType("Order", "订单类型", schema);

    assertEquals(3, registry.getAllResourceTypes().size());

    // 验证返回的类型列表
    var allTypes = registry.getAllResourceTypes();
    assertTrue(allTypes.stream().anyMatch(type -> "User".equals(type.getName())));
    assertTrue(allTypes.stream().anyMatch(type -> "Product".equals(type.getName())));
    assertTrue(allTypes.stream().anyMatch(type -> "Order".equals(type.getName())));
  }

  @Test
  void testUpdateResourceType() {
    String name = "User";
    String description = "用户资源类型";
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    ResourceType resourceType = registry.registerResourceType(name, description, schema);
    String typeId = resourceType.getId();

    // 更新类型
    String newDescription = "更新后的用户资源类型";
    ObjectNode newSchema = objectMapper.createObjectNode();
    newSchema.put("type", "object");
    newSchema.put("properties", "new properties");

    ResourceType updatedType = registry.updateResourceType(typeId, newDescription, newSchema);

    assertEquals(newDescription, updatedType.getDescription());
    assertEquals(newSchema, updatedType.getSchema());
    assertTrue(updatedType.getUpdatedAt() > resourceType.getUpdatedAt());
  }

  @Test
  void testUpdateResourceTypeNotFound() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          registry.updateResourceType("non-existent-id", "New description", null);
        });
  }

  @Test
  void testDeleteResourceType() {
    String name = "User";
    String description = "用户资源类型";
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    ResourceType resourceType = registry.registerResourceType(name, description, schema);
    String typeId = resourceType.getId();

    assertEquals(1, registry.getTypeCount());

    boolean deleted = registry.deleteResourceType(typeId);

    assertTrue(deleted);
    assertEquals(0, registry.getTypeCount());
    assertNull(registry.getResourceTypeById(typeId));
    assertNull(registry.getResourceTypeByName(name));
  }

  @Test
  void testDeleteResourceTypeByName() {
    String name = "User";
    String description = "用户资源类型";
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    ResourceType resourceType = registry.registerResourceType(name, description, schema);

    assertEquals(1, registry.getTypeCount());

    boolean deleted = registry.deleteResourceTypeByName(name);

    assertTrue(deleted);
    assertEquals(0, registry.getTypeCount());
    assertNull(registry.getResourceTypeById(resourceType.getId()));
    assertNull(registry.getResourceTypeByName(name));
  }

  @Test
  void testDeleteNonExistentResourceType() {
    boolean deleted = registry.deleteResourceType("non-existent-id");
    assertFalse(deleted);

    boolean deletedByName = registry.deleteResourceTypeByName("non-existent-name");
    assertFalse(deletedByName);
  }

  @Test
  void testExists() {
    String name = "User";
    String description = "用户资源类型";
    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    ResourceType resourceType = registry.registerResourceType(name, description, schema);

    assertTrue(registry.exists(resourceType.getId()));
    assertFalse(registry.exists("non-existent-id"));
  }

  @Test
  void testGetTypeCount() {
    assertEquals(0, registry.getTypeCount());

    ObjectNode schema = objectMapper.createObjectNode();
    schema.put("type", "object");

    registry.registerResourceType("User", "用户类型", schema);
    assertEquals(1, registry.getTypeCount());

    registry.registerResourceType("Product", "产品类型", schema);
    assertEquals(2, registry.getTypeCount());

    registry.deleteResourceTypeByName("User");
    assertEquals(1, registry.getTypeCount());
  }
}
