package com.uros.kernel.handle.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.uros.kernel.handle.model.UniResource;
import com.uros.kernel.handle.model.UniResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 泛在资源服务
 * 提供UniResource的创建、更新、删除和查询功能
 */
@Service
public class UniResourceService {
    
    @Autowired
    private UniResourceTypeService resourceTypeService;
    
    // 使用内存存储，实际项目中应该使用数据库
    private final Map<String, UniResource> resourceStore = new ConcurrentHashMap<>();
    
    /**
     * 创建资源实例
     * 
     * @param resource 资源对象
     * @return 创建的资源
     */
    public UniResource createResource(UniResource resource) {
        if (resource.getId() == null || resource.getId().trim().isEmpty()) {
            resource.setId(generateId());
        }
        
        // 检查ID是否已存在
        if (resourceStore.containsKey(resource.getId())) {
            throw new IllegalArgumentException("Resource with ID " + resource.getId() + " already exists");
        }
        
        // 验证必要字段和数据格式
        validateResource(resource);
        
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setVersion(1L);
        
        resourceStore.put(resource.getId(), resource);
        return resource;
    }
    
    /**
     * 根据ID获取资源
     * 
     * @param id 资源ID
     * @return 资源对象，如果不存在则返回null
     */
    public UniResource getResourceById(String id) {
        return resourceStore.get(id);
    }
    
    /**
     * 获取所有资源
     * 
     * @return 所有资源列表
     */
    public List<UniResource> getAllResources() {
        return new ArrayList<>(resourceStore.values());
    }
    
    /**
     * 根据资源类型ID查询资源
     * 
     * @param typeId 资源类型ID
     * @return 匹配的资源列表
     */
    public List<UniResource> getResourcesByTypeId(String typeId) {
        return resourceStore.values().stream()
                .filter(resource -> Objects.equals(resource.getTypeId(), typeId))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据名称查询资源
     * 
     * @param name 资源名称
     * @return 匹配的资源列表
     */
    public List<UniResource> getResourcesByName(String name) {
        return resourceStore.values().stream()
                .filter(resource -> resource.getName() != null && resource.getName().contains(name))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据状态查询资源
     * 
     * @param status 资源状态
     * @return 匹配的资源列表
     */
    public List<UniResource> getResourcesByStatus(String status) {
        return resourceStore.values().stream()
                .filter(resource -> Objects.equals(resource.getStatus(), status))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据标签查询资源
     * 
     * @param labels 标签键值对
     * @return 匹配的资源列表
     */
    public List<UniResource> getResourcesByLabels(Map<String, String> labels) {
        return resourceStore.values().stream()
                .filter(resource -> {
                    if (resource.getLabels() == null) {
                        return false;
                    }
                    return labels.entrySet().stream()
                            .allMatch(entry -> Objects.equals(resource.getLabels().get(entry.getKey()), entry.getValue()));
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 根据启用状态查询资源
     * 
     * @param enabled 是否启用
     * @return 匹配的资源列表
     */
    public List<UniResource> getResourcesByEnabled(Boolean enabled) {
        return resourceStore.values().stream()
                .filter(resource -> Objects.equals(resource.getEnabled(), enabled))
                .collect(Collectors.toList());
    }
    
    /**
     * 更新资源
     * 
     * @param id 资源ID
     * @param updatedResource 更新的资源数据
     * @return 更新后的资源
     */
    public UniResource updateResource(String id, UniResource updatedResource) {
        UniResource existingResource = resourceStore.get(id);
        if (existingResource == null) {
            throw new IllegalArgumentException("Resource with ID " + id + " not found");
        }
        
        // 验证更新数据
        validateResource(updatedResource);
        
        // 保留原有的创建信息和版本
        updatedResource.setId(id);
        updatedResource.setCreatedAt(existingResource.getCreatedAt());
        updatedResource.setCreatedBy(existingResource.getCreatedBy());
        updatedResource.setUpdatedAt(LocalDateTime.now());
        updatedResource.setVersion(existingResource.getVersion() + 1);
        
        resourceStore.put(id, updatedResource);
        return updatedResource;
    }
    
    /**
     * 部分更新资源
     * 
     * @param id 资源ID
     * @param updates 要更新的字段
     * @return 更新后的资源
     */
    public UniResource patchResource(String id, Map<String, Object> updates) {
        UniResource existingResource = resourceStore.get(id);
        if (existingResource == null) {
            throw new IllegalArgumentException("Resource with ID " + id + " not found");
        }
        
        // 应用更新
        if (updates.containsKey("name")) {
            existingResource.setName((String) updates.get("name"));
        }
        if (updates.containsKey("description")) {
            existingResource.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("data")) {
            JsonNode newData = (JsonNode) updates.get("data");
            // 验证新数据是否符合schema
            validateResourceData(existingResource.getTypeId(), newData);
            existingResource.setData(newData);
        }
        if (updates.containsKey("status")) {
            existingResource.setStatus((String) updates.get("status"));
        }
        if (updates.containsKey("labels")) {
            existingResource.setLabels((Map<String, String>) updates.get("labels"));
        }
        if (updates.containsKey("enabled")) {
            existingResource.setEnabled((Boolean) updates.get("enabled"));
        }
        if (updates.containsKey("metadata")) {
            existingResource.setMetadata((Map<String, Object>) updates.get("metadata"));
        }
        
        existingResource.setUpdatedAt(LocalDateTime.now());
        existingResource.setVersion(existingResource.getVersion() + 1);
        
        return existingResource;
    }
    
    /**
     * 删除资源
     * 
     * @param id 资源ID
     * @return 是否删除成功
     */
    public boolean deleteResource(String id) {
        return resourceStore.remove(id) != null;
    }
    
    /**
     * 检查资源是否存在
     * 
     * @param id 资源ID
     * @return 是否存在
     */
    public boolean existsById(String id) {
        return resourceStore.containsKey(id);
    }
    
    /**
     * 获取资源总数
     * 
     * @return 总数
     */
    public long count() {
        return resourceStore.size();
    }
    
    /**
     * 根据资源类型统计资源数量
     * 
     * @param typeId 资源类型ID
     * @return 该类型的资源数量
     */
    public long countByTypeId(String typeId) {
        return resourceStore.values().stream()
                .filter(resource -> Objects.equals(resource.getTypeId(), typeId))
                .count();
    }
    
    /**
     * 验证资源数据
     * 
     * @param resource 资源对象
     */
    private void validateResource(UniResource resource) {
        if (resource.getName() == null || resource.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Resource name cannot be null or empty");
        }
        
        if (resource.getTypeId() == null || resource.getTypeId().trim().isEmpty()) {
            throw new IllegalArgumentException("Resource type ID cannot be null or empty");
        }
        
        // 验证资源类型是否存在
        UniResourceType resourceType = resourceTypeService.getResourceTypeById(resource.getTypeId());
        if (resourceType == null) {
            throw new IllegalArgumentException("Resource type with ID " + resource.getTypeId() + " not found");
        }
        
        // 验证资源数据是否符合schema
        if (resource.getData() != null) {
            validateResourceData(resource.getTypeId(), resource.getData());
        }
    }
    
    /**
     * 验证资源数据是否符合指定类型的schema
     * 
     * @param typeId 资源类型ID
     * @param data 资源数据
     */
    private void validateResourceData(String typeId, JsonNode data) {
        UniResourceType resourceType = resourceTypeService.getResourceTypeById(typeId);
        if (resourceType == null) {
            throw new IllegalArgumentException("Resource type with ID " + typeId + " not found");
        }
        
        // TODO: 实现JSON Schema验证逻辑
        // 这里可以使用JSON Schema验证库，如everit-org/json-schema
        // 暂时只做基本的非空验证
        if (data == null) {
            throw new IllegalArgumentException("Resource data cannot be null");
        }
    }
    
    /**
     * 生成唯一ID
     * 
     * @return 唯一ID
     */
    private String generateId() {
        return "res-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}