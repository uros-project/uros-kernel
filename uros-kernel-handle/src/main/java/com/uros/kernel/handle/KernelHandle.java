package com.uros.kernel.handle;

import com.uros.kernel.base.BaseKernel;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 内核处理器类
 * 提供资源类型注册与资源对象创建、删除、更新、查询等基本服务
 */
public class KernelHandle {
    
    private final BaseKernel baseKernel;
    private final ResourceTypeRegistry typeRegistry;
    private final ResourceInstanceManager instanceManager;
    private final ResourceResolver resourceResolver;
    
    public KernelHandle() {
        this.baseKernel = new BaseKernel();
        this.typeRegistry = new ResourceTypeRegistry();
        this.instanceManager = new ResourceInstanceManager();
        this.resourceResolver = new ResourceResolver(typeRegistry, instanceManager);
        
        // 初始化基础内核
        this.baseKernel.initialize();
    }
    
    // ==================== 资源类型管理 ====================
    
    /**
     * 注册资源类型
     * @param name 类型名称
     * @param description 类型描述
     * @param schema JSON Schema
     * @return 注册的资源类型
     */
    public ResourceType registerResourceType(String name, String description, JsonNode schema) {
        return typeRegistry.registerResourceType(name, description, schema);
    }
    
    /**
     * 根据名称查询资源类型
     * @param name 类型名称
     * @return 资源类型
     */
    public ResourceType getResourceTypeByName(String name) {
        return typeRegistry.getResourceTypeByName(name);
    }
    
    /**
     * 根据ID查询资源类型
     * @param typeId 类型ID
     * @return 资源类型
     */
    public ResourceType getResourceTypeById(String typeId) {
        return typeRegistry.getResourceTypeById(typeId);
    }
    
    /**
     * 获取所有资源类型
     * @return 资源类型列表
     */
    public java.util.List<ResourceType> getAllResourceTypes() {
        return typeRegistry.getAllResourceTypes();
    }
    
    /**
     * 删除资源类型
     * @param typeId 类型ID
     * @return 是否删除成功
     */
    public boolean deleteResourceType(String typeId) {
        return typeRegistry.deleteResourceType(typeId);
    }
    
    /**
     * 根据名称删除资源类型
     * @param name 类型名称
     * @return 是否删除成功
     */
    public boolean deleteResourceTypeByName(String name) {
        return typeRegistry.deleteResourceTypeByName(name);
    }
    
    // ==================== 资源实例管理 ====================
    
    /**
     * 创建资源实例
     * @param typeName 资源类型名称
     * @param data 资源数据
     * @return 创建的资源实例
     */
    public ResourceInstance createResourceInstance(String typeName, JsonNode data) {
        ResourceType type = typeRegistry.getResourceTypeByName(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Resource type '" + typeName + "' not found");
        }
        
        return instanceManager.createResourceInstance(type.getId(), type.getName(), data);
    }
    
    /**
     * 根据ID查询资源实例
     * @param instanceId 实例ID
     * @return 资源实例
     */
    public ResourceInstance getResourceInstanceById(String instanceId) {
        return instanceManager.getResourceInstanceById(instanceId);
    }
    
    /**
     * 更新资源实例数据
     * @param instanceId 实例ID
     * @param data 新数据
     * @return 更新后的资源实例
     */
    public ResourceInstance updateResourceInstance(String instanceId, JsonNode data) {
        return instanceManager.updateResourceInstance(instanceId, data);
    }
    
    /**
     * 更新资源实例状态
     * @param instanceId 实例ID
     * @param status 新状态
     * @return 更新后的资源实例
     */
    public ResourceInstance updateResourceInstanceStatus(String instanceId, String status) {
        return instanceManager.updateResourceInstanceStatus(instanceId, status);
    }
    
    /**
     * 删除资源实例
     * @param instanceId 实例ID
     * @return 是否删除成功
     */
    public boolean deleteResourceInstance(String instanceId) {
        return instanceManager.deleteResourceInstance(instanceId);
    }
    
    // ==================== 核心资源解析服务 ====================
    
    /**
     * 根据资源唯一编号进行资源解析
     * 这是核心服务，提供基于资源唯一编号的资源解析
     * 
     * @param resourceId 资源唯一编号
     * @return 解析结果
     */
    public ResourceResolutionResult resolveResource(String resourceId) {
        return resourceResolver.resolveResource(resourceId);
    }
    
    /**
     * 获取资源的完整信息
     * @param resourceId 资源ID
     * @return 完整资源信息
     */
    public CompleteResourceInfo getCompleteResourceInfo(String resourceId) {
        return resourceResolver.getCompleteResourceInfo(resourceId);
    }
    
    /**
     * 验证资源数据
     * @param resourceId 资源ID
     * @return 验证结果
     */
    public ValidationResult validateResource(String resourceId) {
        return resourceResolver.validateResource(resourceId);
    }
    
    // ==================== 统计信息 ====================
    
    /**
     * 获取资源类型数量
     * @return 类型数量
     */
    public int getResourceTypeCount() {
        return typeRegistry.getTypeCount();
    }
    
    /**
     * 获取资源实例总数
     * @return 实例总数
     */
    public int getResourceInstanceCount() {
        return instanceManager.getInstanceCount();
    }
    
    /**
     * 获取指定类型的实例数量
     * @param typeName 类型名称
     * @return 实例数量
     */
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
        baseKernel.cleanup();
    }
}
