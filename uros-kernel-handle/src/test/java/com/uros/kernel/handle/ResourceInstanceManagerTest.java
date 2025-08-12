package com.uros.kernel.handle;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** ResourceInstanceManager 类的测试 */
public class ResourceInstanceManagerTest {

  private ResourceInstanceManager manager;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    manager = new ResourceInstanceManager();
    objectMapper = new ObjectMapper();
  }

  @Test
  void testCreateResourceInstance() {
    String typeId = "user-type-id";
    String typeName = "User";
    ObjectNode data = objectMapper.createObjectNode();
    data.put("name", "张三");
    data.put("age", 25);

    ResourceInstance instance = manager.createResourceInstance(typeId, typeName, data);

    assertNotNull(instance);
    assertEquals(typeId, instance.getTypeId());
    assertEquals(typeName, instance.getTypeName());
    assertEquals(data, instance.getData());
    assertEquals("ACTIVE", instance.getStatus());
    assertEquals(1, manager.getInstanceCount());
  }

  @Test
  void testGetResourceInstanceById() {
    String typeId = "user-type-id";
    String typeName = "User";
    ObjectNode data = objectMapper.createObjectNode();
    data.put("name", "李四");

    ResourceInstance createdInstance = manager.createResourceInstance(typeId, typeName, data);
    String instanceId = createdInstance.getId();

    ResourceInstance retrievedInstance = manager.getResourceInstanceById(instanceId);

    assertNotNull(retrievedInstance);
    assertEquals(createdInstance.getId(), retrievedInstance.getId());
    assertEquals(createdInstance.getTypeId(), retrievedInstance.getTypeId());
    assertEquals(createdInstance.getTypeName(), retrievedInstance.getTypeName());
  }

  @Test
  void testGetResourceInstanceByIdNotFound() {
    ResourceInstance instance = manager.getResourceInstanceById("non-existent-id");
    assertNull(instance);
  }

  @Test
  void testGetResourceInstancesByType() {
    String typeId = "user-type-id";
    String typeName = "User";
    ObjectNode data1 = objectMapper.createObjectNode();
    data1.put("name", "张三");
    ObjectNode data2 = objectMapper.createObjectNode();
    data2.put("name", "李四");

    manager.createResourceInstance(typeId, typeName, data1);
    manager.createResourceInstance(typeId, typeName, data2);

    var instances = manager.getResourceInstancesByType(typeId);
    assertEquals(2, instances.size());

    // 验证实例数据
    assertTrue(
        instances.stream()
            .anyMatch(instance -> "张三".equals(instance.getData().get("name").asText())));
    assertTrue(
        instances.stream()
            .anyMatch(instance -> "李四".equals(instance.getData().get("name").asText())));
  }

  @Test
  void testGetResourceInstancesByTypeEmpty() {
    var instances = manager.getResourceInstancesByType("non-existent-type");
    assertTrue(instances.isEmpty());
  }

  @Test
  void testGetAllResourceInstances() {
    assertEquals(0, manager.getAllResourceInstances().size());

    // 创建多个实例
    ObjectNode data = objectMapper.createObjectNode();
    data.put("name", "test");

    manager.createResourceInstance("type1", "Type1", data);
    manager.createResourceInstance("type2", "Type2", data);
    manager.createResourceInstance("type3", "Type3", data);

    assertEquals(3, manager.getAllResourceInstances().size());
  }

  @Test
  void testUpdateResourceInstance() {
    String typeId = "user-type-id";
    String typeName = "User";
    ObjectNode data = objectMapper.createObjectNode();
    data.put("name", "王五");

    ResourceInstance instance = manager.createResourceInstance(typeId, typeName, data);
    String instanceId = instance.getId();

    // 更新数据
    ObjectNode newData = objectMapper.createObjectNode();
    newData.put("name", "王五更新");
    newData.put("age", 30);

    ResourceInstance updatedInstance = manager.updateResourceInstance(instanceId, newData);

    assertEquals(newData, updatedInstance.getData());
    assertTrue(updatedInstance.getUpdatedAt() > instance.getUpdatedAt());
  }

  @Test
  void testUpdateResourceInstanceNotFound() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          ObjectNode newData = objectMapper.createObjectNode();
          manager.updateResourceInstance("non-existent-id", newData);
        });
  }

  @Test
  void testUpdateResourceInstanceStatus() {
    String typeId = "user-type-id";
    String typeName = "User";
    ObjectNode data = objectMapper.createObjectNode();
    data.put("name", "赵六");

    ResourceInstance instance = manager.createResourceInstance(typeId, typeName, data);
    String instanceId = instance.getId();

    ResourceInstance updatedInstance = manager.updateResourceInstanceStatus(instanceId, "INACTIVE");

    assertEquals("INACTIVE", updatedInstance.getStatus());
    assertTrue(updatedInstance.getUpdatedAt() > instance.getUpdatedAt());
  }

  @Test
  void testUpdateResourceInstanceStatusNotFound() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          manager.updateResourceInstanceStatus("non-existent-id", "INACTIVE");
        });
  }

  @Test
  void testDeleteResourceInstance() {
    String typeId = "user-type-id";
    String typeName = "User";
    ObjectNode data = objectMapper.createObjectNode();
    data.put("name", "钱七");

    ResourceInstance instance = manager.createResourceInstance(typeId, typeName, data);
    String instanceId = instance.getId();

    assertEquals(1, manager.getInstanceCount());
    assertEquals(1, manager.getInstanceCountByType(typeId));

    boolean deleted = manager.deleteResourceInstance(instanceId);

    assertTrue(deleted);
    assertEquals(0, manager.getInstanceCount());
    assertEquals(0, manager.getInstanceCountByType(typeId));
    assertNull(manager.getResourceInstanceById(instanceId));
  }

  @Test
  void testDeleteResourceInstanceNotFound() {
    boolean deleted = manager.deleteResourceInstance("non-existent-id");
    assertFalse(deleted);
  }

  @Test
  void testQueryResourceInstances() {
    String typeId = "user-type-id";
    String typeName = "User";
    ObjectNode data1 = objectMapper.createObjectNode();
    data1.put("name", "张三");
    ObjectNode data2 = objectMapper.createObjectNode();
    data2.put("name", "李四");

    manager.createResourceInstance(typeId, typeName, data1);
    manager.createResourceInstance(typeId, typeName, data2);

    // 查询所有实例
    var allInstances = manager.queryResourceInstances(null, null);
    assertEquals(2, allInstances.size());

    // 按类型查询
    var typeInstances = manager.queryResourceInstances(typeId, null);
    assertEquals(2, typeInstances.size());

    // 按状态查询
    var activeInstances = manager.queryResourceInstances(null, "ACTIVE");
    assertEquals(2, activeInstances.size());

    // 按类型和状态查询
    var typeAndStatusInstances = manager.queryResourceInstances(typeId, "ACTIVE");
    assertEquals(2, typeAndStatusInstances.size());
  }

  @Test
  void testExists() {
    String typeId = "user-type-id";
    String typeName = "User";
    ObjectNode data = objectMapper.createObjectNode();
    data.put("name", "孙八");

    ResourceInstance instance = manager.createResourceInstance(typeId, typeName, data);

    assertTrue(manager.exists(instance.getId()));
    assertFalse(manager.exists("non-existent-id"));
  }

  @Test
  void testGetInstanceCount() {
    assertEquals(0, manager.getInstanceCount());

    ObjectNode data = objectMapper.createObjectNode();
    data.put("name", "test");

    manager.createResourceInstance("type1", "Type1", data);
    assertEquals(1, manager.getInstanceCount());

    manager.createResourceInstance("type2", "Type2", data);
    assertEquals(2, manager.getInstanceCount());

    manager.deleteResourceInstance(manager.getAllResourceInstances().get(0).getId());
    assertEquals(1, manager.getInstanceCount());
  }

  @Test
  void testGetInstanceCountByType() {
    String typeId = "user-type-id";
    String typeName = "User";
    ObjectNode data = objectMapper.createObjectNode();
    data.put("name", "test");

    assertEquals(0, manager.getInstanceCountByType(typeId));

    manager.createResourceInstance(typeId, typeName, data);
    assertEquals(1, manager.getInstanceCountByType(typeId));

    manager.createResourceInstance(typeId, typeName, data);
    assertEquals(2, manager.getInstanceCountByType(typeId));

    // 删除一个实例
    var instances = manager.getResourceInstancesByType(typeId);
    manager.deleteResourceInstance(instances.get(0).getId());
    assertEquals(1, manager.getInstanceCountByType(typeId));
  }
}
