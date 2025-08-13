package com.uros.kernel.handle.exception;

/**
 * 资源冲突异常
 * 当资源操作产生冲突时抛出此异常
 */
public class ResourceConflictException extends RuntimeException {
    
    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public ResourceConflictException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 异常原因
     */
    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 创建资源已存在异常
     * 
     * @param resourceType 资源类型
     * @param id 资源ID
     * @return 异常实例
     */
    public static ResourceConflictException forExistingResource(String resourceType, String id) {
        return new ResourceConflictException(String.format("%s with ID '%s' already exists", resourceType, id));
    }
    
    /**
     * 创建资源名称冲突异常
     * 
     * @param resourceType 资源类型
     * @param name 资源名称
     * @return 异常实例
     */
    public static ResourceConflictException forDuplicateName(String resourceType, String name) {
        return new ResourceConflictException(String.format("%s with name '%s' already exists", resourceType, name));
    }
    
    /**
     * 创建版本冲突异常
     * 
     * @param resourceType 资源类型
     * @param id 资源ID
     * @param expectedVersion 期望版本
     * @param actualVersion 实际版本
     * @return 异常实例
     */
    public static ResourceConflictException forVersionMismatch(String resourceType, String id, 
                                                               Long expectedVersion, Long actualVersion) {
        return new ResourceConflictException(String.format(
            "Version conflict for %s '%s': expected version %d, but actual version is %d", 
            resourceType, id, expectedVersion, actualVersion));
    }
    
    /**
     * 创建资源状态冲突异常
     * 
     * @param resourceType 资源类型
     * @param id 资源ID
     * @param currentStatus 当前状态
     * @param operation 操作
     * @return 异常实例
     */
    public static ResourceConflictException forInvalidState(String resourceType, String id, 
                                                            String currentStatus, String operation) {
        return new ResourceConflictException(String.format(
            "Cannot perform operation '%s' on %s '%s' in status '%s'", 
            operation, resourceType, id, currentStatus));
    }
}