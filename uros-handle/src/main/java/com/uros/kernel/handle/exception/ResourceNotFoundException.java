package com.uros.kernel.handle.exception;

/**
 * 资源未找到异常
 * 当请求的资源不存在时抛出此异常
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 异常原因
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 根据资源类型和ID创建异常
     * 
     * @param resourceType 资源类型
     * @param id 资源ID
     * @return 异常实例
     */
    public static ResourceNotFoundException forResource(String resourceType, String id) {
        return new ResourceNotFoundException(String.format("%s with ID '%s' not found", resourceType, id));
    }
    
    /**
     * 根据资源类型创建异常
     * 
     * @param resourceType 资源类型
     * @return 异常实例
     */
    public static ResourceNotFoundException forResourceType(String resourceType) {
        return new ResourceNotFoundException(String.format("%s not found", resourceType));
    }
}