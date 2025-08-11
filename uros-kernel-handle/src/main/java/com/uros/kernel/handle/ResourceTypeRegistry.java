package com.uros.kernel.handle;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源类型注册服务
 * 负责资源类型的注册、查询和管理
 */
public class ResourceTypeRegistry {
    
    private final Map<String, ResourceType> typeRegistry = new ConcurrentHashMap<>();
    private final Map<String, String> nameToIdMap = new ConcurrentHashMap<>();
    
    /**
     * 注册资源类型
     * @param name 类型名称
     * @param description 类型描述
     * @param schema JSON Schema
     * @return 注册的资源类型
     */
    public ResourceType registerResourceType(String name, String description, JsonNode schema) {
        // 检查名称是否已存在
        if (nameToIdMap.containsKey(name)) {
            throw new IllegalArgumentException("Resource type with name '" + name + "' already exists");
        }
        
        ResourceType resourceType = new ResourceType(name, description, schema);
        typeRegistry.put(resourceType.getId(), resourceType);
        nameToIdMap.put(name, resourceType.getId());
        
        return resourceType;
    }
    
    /**
     * 根据ID查询资源类型
     * @param typeId 类型ID
     * @return 资源类型，如果不存在返回null
     */
    public ResourceType getResourceTypeById(String typeId) {
        return typeRegistry.get(typeId);
    }
    
    /**
     * 根据名称查询资源类型
     * @param name 类型名称
     * @return 资源类型，如果不存在返回null
     */
    public ResourceType getResourceTypeByName(String name) {
        String typeId = nameToIdMap.get(name);
        return typeId != null ? typeRegistry.get(typeId) : null;
    }
    
    /**
     * 获取所有资源类型
     * @return 资源类型列表
     */
    public List<ResourceType> getAllResourceTypes() {
        return new ArrayList<>(typeRegistry.values());
    }
    
    /**
     * 更新资源类型
     * @param typeId 类型ID
     * @param description 新描述
     * @param schema 新Schema
     * @return 更新后的资源类型
     */
    public ResourceType updateResourceType(String typeId, String description, JsonNode schema) {
        ResourceType existing = typeRegistry.get(typeId);
        if (existing == null) {
            throw new IllegalArgumentException("Resource type with ID '" + typeId + "' not found");
        }

        // 复制并替换策略，保持 id/name/createdAt，不原地修改对象
        ResourceType newType = new ResourceType();
        newType.setId(existing.getId());
        newType.setName(existing.getName());
        newType.setCreatedAt(existing.getCreatedAt());
        newType.setDescription(description);
        newType.setSchema(schema); // 会更新 updatedAt
        // 确保新 updatedAt 严格大于旧值
        if (newType.getUpdatedAt() <= existing.getUpdatedAt()) {
            newType.setUpdatedAt(existing.getUpdatedAt() + 1);
        }

        typeRegistry.put(typeId, newType);
        // 名称未改变，无需更新 nameToIdMap
        return newType;
    }
    
    /**
     * 删除资源类型
     * @param typeId 类型ID
     * @return 是否删除成功
     */
    public boolean deleteResourceType(String typeId) {
        ResourceType resourceType = typeRegistry.remove(typeId);
        if (resourceType != null) {
            nameToIdMap.remove(resourceType.getName());
            return true;
        }
        return false;
    }
    
    /**
     * 根据名称删除资源类型
     * @param name 类型名称
     * @return 是否删除成功
     */
    public boolean deleteResourceTypeByName(String name) {
        String typeId = nameToIdMap.remove(name);
        if (typeId != null) {
            ResourceType resourceType = typeRegistry.remove(typeId);
            return resourceType != null;
        }
        return false;
    }
    
    /**
     * 检查资源类型是否存在
     * @param typeId 类型ID
     * @return 是否存在
     */
    public boolean exists(String typeId) {
        return typeRegistry.containsKey(typeId);
    }
    
    /**
     * 获取注册的资源类型数量
     * @return 类型数量
     */
    public int getTypeCount() {
        return typeRegistry.size();
    }
}
