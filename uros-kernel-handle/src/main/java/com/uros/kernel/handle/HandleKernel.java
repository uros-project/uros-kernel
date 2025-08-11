package com.uros.kernel.handle;

import com.uros.kernel.base.BaseKernel;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 内核处理器类（重命名：HandleKernel）
 * 继承 BaseKernel，提供资源类型注册与资源对象创建、删除、更新、查询等基本服务
 */
public class HandleKernel extends BaseKernel {

    private final ResourceTypeRegistry typeRegistry;
    private final ResourceInstanceManager instanceManager;
    private final ResourceResolver resourceResolver;

    public HandleKernel() {
        super.initialize();
        this.typeRegistry = new ResourceTypeRegistry();
        this.instanceManager = new ResourceInstanceManager();
        this.resourceResolver = new ResourceResolver(typeRegistry, instanceManager);
    }

    // ==================== 资源类型管理 ====================

    public ResourceType registerResourceType(String name, String description, JsonNode schema) {
        return typeRegistry.registerResourceType(name, description, schema);
    }

    public ResourceType getResourceTypeByName(String name) {
        return typeRegistry.getResourceTypeByName(name);
    }

    public ResourceType getResourceTypeById(String typeId) {
        return typeRegistry.getResourceTypeById(typeId);
    }

    public java.util.List<ResourceType> getAllResourceTypes() {
        return typeRegistry.getAllResourceTypes();
    }

    public boolean deleteResourceType(String typeId) {
        return typeRegistry.deleteResourceType(typeId);
    }

    public boolean deleteResourceTypeByName(String name) {
        return typeRegistry.deleteResourceTypeByName(name);
    }

    // ==================== 资源实例管理 ====================

    public ResourceInstance createResourceInstance(String typeName, JsonNode data) {
        ResourceType type = typeRegistry.getResourceTypeByName(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Resource type '" + typeName + "' not found");
        }
        return instanceManager.createResourceInstance(type.getId(), type.getName(), data);
    }

    public ResourceInstance getResourceInstanceById(String instanceId) {
        return instanceManager.getResourceInstanceById(instanceId);
    }

    public ResourceInstance updateResourceInstance(String instanceId, JsonNode data) {
        return instanceManager.updateResourceInstance(instanceId, data);
    }

    public ResourceInstance updateResourceInstanceStatus(String instanceId, String status) {
        return instanceManager.updateResourceInstanceStatus(instanceId, status);
    }

    public boolean deleteResourceInstance(String instanceId) {
        return instanceManager.deleteResourceInstance(instanceId);
    }

    // ==================== 核心资源解析服务 ====================

    public ResourceResolutionResult resolveResource(String resourceId) {
        return resourceResolver.resolveResource(resourceId);
    }

    public CompleteResourceInfo getCompleteResourceInfo(String resourceId) {
        return resourceResolver.getCompleteResourceInfo(resourceId);
    }

    public ValidationResult validateResource(String resourceId) {
        return resourceResolver.validateResource(resourceId);
    }

    // ==================== 统计信息 ====================

    public int getResourceTypeCount() {
        return typeRegistry.getTypeCount();
    }

    public int getResourceInstanceCount() {
        return instanceManager.getInstanceCount();
    }

    public int getInstanceCountByType(String typeName) {
        ResourceType type = typeRegistry.getResourceTypeByName(typeName);
        if (type == null) {
            return 0;
        }
        return instanceManager.getInstanceCountByType(type.getId());
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        super.cleanup();
    }
}
