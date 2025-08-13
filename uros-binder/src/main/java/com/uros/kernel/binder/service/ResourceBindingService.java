package com.uros.kernel.binder.service;

import com.uros.kernel.binder.model.ResourceBinding;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 资源绑定服务
 * 提供资源绑定关系的管理功能
 */
@Service
public class ResourceBindingService {
    
    // 使用内存存储，实际项目中应该使用数据库
    private final Map<String, ResourceBinding> bindingStore = new ConcurrentHashMap<>();
    
    /**
     * 创建绑定关系
     * 
     * @param binding 绑定对象
     * @return 创建的绑定关系
     */
    public ResourceBinding createBinding(ResourceBinding binding) {
        if (binding.getId() == null || binding.getId().trim().isEmpty()) {
            binding.setId(generateId());
        }
        
        // 检查ID是否已存在
        if (bindingStore.containsKey(binding.getId())) {
            throw new IllegalArgumentException("Binding with ID " + binding.getId() + " already exists");
        }
        
        // 验证必要字段
        validateBinding(binding);
        
        binding.setCreatedAt(LocalDateTime.now());
        binding.setUpdatedAt(LocalDateTime.now());
        
        bindingStore.put(binding.getId(), binding);
        return binding;
    }
    
    /**
     * 根据ID获取绑定关系
     * 
     * @param id 绑定ID
     * @return 绑定对象，如果不存在则返回null
     */
    public ResourceBinding getBindingById(String id) {
        return bindingStore.get(id);
    }
    
    /**
     * 获取所有绑定关系
     * 
     * @return 所有绑定关系列表
     */
    public List<ResourceBinding> getAllBindings() {
        return new ArrayList<>(bindingStore.values());
    }
    
    /**
     * 根据源资源ID查询绑定关系
     * 
     * @param sourceResourceId 源资源ID
     * @return 匹配的绑定关系列表
     */
    public List<ResourceBinding> getBindingsBySourceResourceId(String sourceResourceId) {
        return bindingStore.values().stream()
                .filter(binding -> Objects.equals(binding.getSourceResourceId(), sourceResourceId))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据目标资源ID查询绑定关系
     * 
     * @param targetResourceId 目标资源ID
     * @return 匹配的绑定关系列表
     */
    public List<ResourceBinding> getBindingsByTargetResourceId(String targetResourceId) {
        return bindingStore.values().stream()
                .filter(binding -> Objects.equals(binding.getTargetResourceId(), targetResourceId))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据绑定类型查询绑定关系
     * 
     * @param bindingType 绑定类型
     * @return 匹配的绑定关系列表
     */
    public List<ResourceBinding> getBindingsByType(String bindingType) {
        return bindingStore.values().stream()
                .filter(binding -> Objects.equals(binding.getBindingType(), bindingType))
                .collect(Collectors.toList());
    }
    
    /**
     * 更新绑定关系
     * 
     * @param id 绑定ID
     * @param updatedBinding 更新的绑定对象
     * @return 更新后的绑定关系
     */
    public ResourceBinding updateBinding(String id, ResourceBinding updatedBinding) {
        ResourceBinding existing = bindingStore.get(id);
        if (existing == null) {
            throw new IllegalArgumentException("Binding with ID " + id + " not found");
        }
        
        updatedBinding.setId(id);
        updatedBinding.setCreatedAt(existing.getCreatedAt());
        updatedBinding.setUpdatedAt(LocalDateTime.now());
        
        validateBinding(updatedBinding);
        
        bindingStore.put(id, updatedBinding);
        return updatedBinding;
    }
    
    /**
     * 删除绑定关系
     * 
     * @param id 绑定ID
     * @return 是否删除成功
     */
    public boolean deleteBinding(String id) {
        return bindingStore.remove(id) != null;
    }
    
    /**
     * 检查绑定关系是否存在
     * 
     * @param id 绑定ID
     * @return 是否存在
     */
    public boolean existsById(String id) {
        return bindingStore.containsKey(id);
    }
    
    /**
     * 获取绑定关系总数
     * 
     * @return 绑定关系总数
     */
    public long count() {
        return bindingStore.size();
    }
    
    /**
     * 清理过期的绑定关系
     * 
     * @return 清理的绑定关系数量
     */
    public int cleanupExpiredBindings() {
        LocalDateTime now = LocalDateTime.now();
        List<String> expiredIds = bindingStore.values().stream()
                .filter(binding -> binding.getExpiresAt() != null && binding.getExpiresAt().isBefore(now))
                .map(ResourceBinding::getId)
                .collect(Collectors.toList());
        
        expiredIds.forEach(bindingStore::remove);
        return expiredIds.size();
    }
    
    /**
     * 验证绑定关系
     * 
     * @param binding 绑定对象
     */
    private void validateBinding(ResourceBinding binding) {
        if (binding.getSourceResourceId() == null || binding.getSourceResourceId().trim().isEmpty()) {
            throw new IllegalArgumentException("Source resource ID is required");
        }
        
        if (binding.getTargetResourceId() == null || binding.getTargetResourceId().trim().isEmpty()) {
            throw new IllegalArgumentException("Target resource ID is required");
        }
        
        if (binding.getBindingType() == null || binding.getBindingType().trim().isEmpty()) {
            throw new IllegalArgumentException("Binding type is required");
        }
        
        // 检查是否尝试绑定自己
        if (Objects.equals(binding.getSourceResourceId(), binding.getTargetResourceId())) {
            throw new IllegalArgumentException("Cannot bind resource to itself");
        }
    }
    
    /**
     * 生成唯一ID
     * 
     * @return 唯一ID
     */
    private String generateId() {
        return "binding-" + UUID.randomUUID().toString();
    }
}