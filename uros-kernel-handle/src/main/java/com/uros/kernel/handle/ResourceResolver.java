package com.uros.kernel.handle;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;

/**
 * 资源解析服务
 * 核心服务，基于资源唯一编号进行资源解析
 */
public class ResourceResolver {
    
    private final ResourceTypeRegistry typeRegistry;
    private final ResourceInstanceManager instanceManager;
    
    public ResourceResolver(ResourceTypeRegistry typeRegistry, ResourceInstanceManager instanceManager) {
        this.typeRegistry = typeRegistry;
        this.instanceManager = instanceManager;
    }
    
    /**
     * 根据资源ID解析资源
     * 这是核心服务，提供基于资源唯一编号的资源解析
     * 
     * @param resourceId 资源唯一编号
     * @return 解析结果，包含资源实例和类型信息
     */
    public ResourceResolutionResult resolveResource(String resourceId) {
        // 查找资源实例
        ResourceInstance instance = instanceManager.getResourceInstanceById(resourceId);
        if (instance == null) {
            return ResourceResolutionResult.notFound(resourceId);
        }
        
        // 查找资源类型
        ResourceType type = typeRegistry.getResourceTypeById(instance.getTypeId());
        if (type == null) {
            return ResourceResolutionResult.typeNotFound(resourceId, instance.getTypeId());
        }
        
        // 返回完整的解析结果
        return ResourceResolutionResult.success(resourceId, instance, type);
    }
    
    /**
     * 根据资源ID和字段路径解析特定数据
     * 
     * @param resourceId 资源唯一编号
     * @param fieldPath 字段路径（如 "user.profile.name"）
     * @return 字段值
     */
    public Optional<JsonNode> resolveResourceField(String resourceId, String fieldPath) {
        ResourceResolutionResult result = resolveResource(resourceId);
        if (!result.isSuccess()) {
            return Optional.empty();
        }
        
        // 这里可以实现字段路径解析逻辑
        // 当前简化实现，直接返回整个数据
        return Optional.of(result.getInstance().getData());
    }
    
    /**
     * 验证资源数据是否符合类型定义
     * 
     * @param resourceId 资源唯一编号
     * @return 验证结果
     */
    public ValidationResult validateResource(String resourceId) {
        ResourceResolutionResult result = resolveResource(resourceId);
        if (!result.isSuccess()) {
            return ValidationResult.failure(result.getErrorMessage());
        }
        
        // 这里可以实现JSON Schema验证逻辑
        // 当前简化实现，假设数据有效
        return ValidationResult.success();
    }
    
    /**
     * 获取资源的完整信息（包括类型定义和实例数据）
     * 
     * @param resourceId 资源唯一编号
     * @return 完整资源信息
     */
    public CompleteResourceInfo getCompleteResourceInfo(String resourceId) {
        ResourceResolutionResult result = resolveResource(resourceId);
        if (!result.isSuccess()) {
            return null;
        }
        
        return new CompleteResourceInfo(
            result.getInstance(),
            result.getType(),
            result.getType().getSchema()
        );
    }
    
    /**
     * 检查资源是否存在且可访问
     * 
     * @param resourceId 资源唯一编号
     * @return 是否可访问
     */
    public boolean isResourceAccessible(String resourceId) {
        ResourceInstance instance = instanceManager.getResourceInstanceById(resourceId);
        return instance != null && "ACTIVE".equals(instance.getStatus());
    }
}
