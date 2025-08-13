package com.uros.kernel.handle.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.uros.kernel.handle.model.UniResourceType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 泛在资源类型服务
 * 提供UniResourceType的创建、更新、删除和查询功能
 */
@Service
public class UniResourceTypeService {
    
    // 使用内存存储，实际项目中应该使用数据库
    private final Map<String, UniResourceType> typeStore = new ConcurrentHashMap<>();
    
    /**
     * 创建资源类型
     * 
     * @param resourceType 资源类型对象
     * @return 创建的资源类型
     */
    public UniResourceType createResourceType(UniResourceType resourceType) {
        if (resourceType.getId() == null || resourceType.getId().trim().isEmpty()) {
            resourceType.setId(generateId());
        }
        
        // 检查ID是否已存在
        if (typeStore.containsKey(resourceType.getId())) {
            throw new IllegalArgumentException("Resource type with ID " + resourceType.getId() + " already exists");
        }
        
        // 验证必要字段
        validateResourceType(resourceType);
        
        resourceType.setCreatedAt(LocalDateTime.now());
        resourceType.setUpdatedAt(LocalDateTime.now());
        
        typeStore.put(resourceType.getId(), resourceType);
        return resourceType;
    }
    
    /**
     * 根据ID获取资源类型
     * 
     * @param id 资源类型ID
     * @return 资源类型对象，如果不存在则返回null
     */
    public UniResourceType getResourceTypeById(String id) {
        return typeStore.get(id);
    }
    
    /**
     * 获取所有资源类型
     * 
     * @return 所有资源类型列表
     */
    public List<UniResourceType> getAllResourceTypes() {
        return new ArrayList<>(typeStore.values());
    }
    
    /**
     * 根据名称查询资源类型
     * 
     * @param name 资源类型名称
     * @return 匹配的资源类型列表
     */
    public List<UniResourceType> getResourceTypesByName(String name) {
        return typeStore.values().stream()
                .filter(type -> type.getName() != null && type.getName().contains(name))
                .toList();
    }
    
    /**
     * 根据启用状态查询资源类型
     * 
     * @param enabled 是否启用
     * @return 匹配的资源类型列表
     */
    public List<UniResourceType> getResourceTypesByEnabled(Boolean enabled) {
        return typeStore.values().stream()
                .filter(type -> Objects.equals(type.getEnabled(), enabled))
                .toList();
    }
    
    /**
     * 更新资源类型
     * 
     * @param id 资源类型ID
     * @param updatedResourceType 更新的资源类型数据
     * @return 更新后的资源类型
     */
    public UniResourceType updateResourceType(String id, UniResourceType updatedResourceType) {
        UniResourceType existingType = typeStore.get(id);
        if (existingType == null) {
            throw new IllegalArgumentException("Resource type with ID " + id + " not found");
        }
        
        // 验证更新数据
        validateResourceType(updatedResourceType);
        
        // 保留原有的创建信息
        updatedResourceType.setId(id);
        updatedResourceType.setCreatedAt(existingType.getCreatedAt());
        updatedResourceType.setCreatedBy(existingType.getCreatedBy());
        updatedResourceType.setUpdatedAt(LocalDateTime.now());
        
        typeStore.put(id, updatedResourceType);
        return updatedResourceType;
    }
    
    /**
     * 部分更新资源类型
     * 
     * @param id 资源类型ID
     * @param updates 要更新的字段
     * @return 更新后的资源类型
     */
    public UniResourceType patchResourceType(String id, Map<String, Object> updates) {
        UniResourceType existingType = typeStore.get(id);
        if (existingType == null) {
            throw new IllegalArgumentException("Resource type with ID " + id + " not found");
        }
        
        // 应用更新
        if (updates.containsKey("name")) {
            existingType.setName((String) updates.get("name"));
        }
        if (updates.containsKey("description")) {
            existingType.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("version")) {
            existingType.setVersion((String) updates.get("version"));
        }
        if (updates.containsKey("schema")) {
            existingType.setSchema((JsonNode) updates.get("schema"));
        }
        if (updates.containsKey("enabled")) {
            existingType.setEnabled((Boolean) updates.get("enabled"));
        }
        if (updates.containsKey("metadata")) {
            existingType.setMetadata((Map<String, Object>) updates.get("metadata"));
        }
        
        existingType.setUpdatedAt(LocalDateTime.now());
        
        return existingType;
    }
    
    /**
     * 删除资源类型
     * 
     * @param id 资源类型ID
     * @return 是否删除成功
     */
    public boolean deleteResourceType(String id) {
        return typeStore.remove(id) != null;
    }
    
    /**
     * 检查资源类型是否存在
     * 
     * @param id 资源类型ID
     * @return 是否存在
     */
    public boolean existsById(String id) {
        return typeStore.containsKey(id);
    }
    
    /**
     * 获取资源类型总数
     * 
     * @return 总数
     */
    public long count() {
        return typeStore.size();
    }
    
    /**
     * 验证资源类型数据
     * 
     * @param resourceType 资源类型对象
     */
    private void validateResourceType(UniResourceType resourceType) {
        if (resourceType.getName() == null || resourceType.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Resource type name cannot be null or empty");
        }
        
        if (resourceType.getSchema() == null) {
            throw new IllegalArgumentException("Resource type schema cannot be null");
        }
        
        if (resourceType.getVersion() == null || resourceType.getVersion().trim().isEmpty()) {
            throw new IllegalArgumentException("Resource type version cannot be null or empty");
        }
    }
    
    /**
     * 生成唯一ID
     * 
     * @return 唯一ID
     */
    private String generateId() {
        return "rt-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}