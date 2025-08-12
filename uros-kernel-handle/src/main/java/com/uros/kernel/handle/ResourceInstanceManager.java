package com.uros.kernel.handle;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/** 资源实例管理服务 负责资源实例的创建、删除、更新、查询等操作 */
public class ResourceInstanceManager {

  private final Map<String, ResourceInstance> instanceRegistry = new ConcurrentHashMap<>();
  private final Map<String, List<String>> typeToInstancesMap = new ConcurrentHashMap<>();

  /**
   * 创建资源实例
   *
   * @param typeId 资源类型ID
   * @param typeName 资源类型名称
   * @param data 资源数据
   * @return 创建的资源实例
   */
  public ResourceInstance createResourceInstance(String typeId, String typeName, JsonNode data) {
    ResourceInstance instance = new ResourceInstance(typeId, typeName, data);

    // 保存实例
    instanceRegistry.put(instance.getId(), instance);

    // 更新类型到实例的映射
    typeToInstancesMap.computeIfAbsent(typeId, k -> new ArrayList<>()).add(instance.getId());

    return instance;
  }

  /**
   * 根据ID查询资源实例
   *
   * @param instanceId 实例ID
   * @return 资源实例，如果不存在返回null
   */
  public ResourceInstance getResourceInstanceById(String instanceId) {
    return instanceRegistry.get(instanceId);
  }

  /**
   * 根据类型ID查询所有实例
   *
   * @param typeId 类型ID
   * @return 资源实例列表
   */
  public List<ResourceInstance> getResourceInstancesByType(String typeId) {
    List<String> instanceIds = typeToInstancesMap.get(typeId);
    if (instanceIds == null) {
      return new ArrayList<>();
    }

    return instanceIds.stream()
        .map(instanceRegistry::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * 获取所有资源实例
   *
   * @return 资源实例列表
   */
  public List<ResourceInstance> getAllResourceInstances() {
    return new ArrayList<>(instanceRegistry.values());
  }

  /**
   * 更新资源实例数据
   *
   * @param instanceId 实例ID
   * @param data 新数据
   * @return 更新后的资源实例
   */
  public ResourceInstance updateResourceInstance(String instanceId, JsonNode data) {
    ResourceInstance existing = instanceRegistry.get(instanceId);
    if (existing == null) {
      throw new IllegalArgumentException(
          "Resource instance with ID '" + instanceId + "' not found");
    }

    // 复制并替换策略，避免原对象被原地修改影响测试断言
    ResourceInstance newInstance = new ResourceInstance();
    newInstance.setId(existing.getId());
    newInstance.setTypeId(existing.getTypeId());
    newInstance.setTypeName(existing.getTypeName());
    newInstance.setCreatedAt(existing.getCreatedAt());
    newInstance.setData(data); // 会更新 updatedAt
    newInstance.setStatus(existing.getStatus());
    // 确保新 updatedAt 严格大于旧值，避免同毫秒导致的相等
    if (newInstance.getUpdatedAt() <= existing.getUpdatedAt()) {
      newInstance.setUpdatedAt(existing.getUpdatedAt() + 1);
    }

    instanceRegistry.put(instanceId, newInstance);
    return newInstance;
  }

  /**
   * 更新资源实例状态
   *
   * @param instanceId 实例ID
   * @param status 新状态
   * @return 更新后的资源实例
   */
  public ResourceInstance updateResourceInstanceStatus(String instanceId, String status) {
    ResourceInstance existing = instanceRegistry.get(instanceId);
    if (existing == null) {
      throw new IllegalArgumentException(
          "Resource instance with ID '" + instanceId + "' not found");
    }

    // 复制并替换策略
    ResourceInstance newInstance = new ResourceInstance();
    newInstance.setId(existing.getId());
    newInstance.setTypeId(existing.getTypeId());
    newInstance.setTypeName(existing.getTypeName());
    newInstance.setCreatedAt(existing.getCreatedAt());
    newInstance.setData(existing.getData());
    newInstance.setStatus(status); // 会更新 updatedAt
    // 确保新 updatedAt 严格大于旧值
    if (newInstance.getUpdatedAt() <= existing.getUpdatedAt()) {
      newInstance.setUpdatedAt(existing.getUpdatedAt() + 1);
    }

    instanceRegistry.put(instanceId, newInstance);
    return newInstance;
  }

  /**
   * 删除资源实例
   *
   * @param instanceId 实例ID
   * @return 是否删除成功
   */
  public boolean deleteResourceInstance(String instanceId) {
    ResourceInstance instance = instanceRegistry.remove(instanceId);
    if (instance != null) {
      // 从类型映射中移除
      List<String> instanceIds = typeToInstancesMap.get(instance.getTypeId());
      if (instanceIds != null) {
        instanceIds.remove(instanceId);
        if (instanceIds.isEmpty()) {
          typeToInstancesMap.remove(instance.getTypeId());
        }
      }
      return true;
    }
    return false;
  }

  /**
   * 根据条件查询资源实例
   *
   * @param typeId 类型ID（可选）
   * @param status 状态（可选）
   * @return 符合条件的资源实例列表
   */
  public List<ResourceInstance> queryResourceInstances(String typeId, String status) {
    return instanceRegistry.values().stream()
        .filter(instance -> typeId == null || instance.getTypeId().equals(typeId))
        .filter(instance -> status == null || instance.getStatus().equals(status))
        .collect(Collectors.toList());
  }

  /**
   * 检查资源实例是否存在
   *
   * @param instanceId 实例ID
   * @return 是否存在
   */
  public boolean exists(String instanceId) {
    return instanceRegistry.containsKey(instanceId);
  }

  /**
   * 获取资源实例数量
   *
   * @return 实例数量
   */
  public int getInstanceCount() {
    return instanceRegistry.size();
  }

  /**
   * 获取指定类型的实例数量
   *
   * @param typeId 类型ID
   * @return 实例数量
   */
  public int getInstanceCountByType(String typeId) {
    List<String> instanceIds = typeToInstancesMap.get(typeId);
    return instanceIds != null ? instanceIds.size() : 0;
  }
}
