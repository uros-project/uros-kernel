package com.uros.kernel.handle.exception;

/**
 * 资源验证异常
 * 当资源数据验证失败时抛出此异常
 */
public class ResourceValidationException extends RuntimeException {
    
    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public ResourceValidationException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 异常原因
     */
    public ResourceValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 创建字段验证失败异常
     * 
     * @param fieldName 字段名
     * @param reason 失败原因
     * @return 异常实例
     */
    public static ResourceValidationException forField(String fieldName, String reason) {
        return new ResourceValidationException(String.format("Validation failed for field '%s': %s", fieldName, reason));
    }
    
    /**
     * 创建Schema验证失败异常
     * 
     * @param schemaError Schema错误信息
     * @return 异常实例
     */
    public static ResourceValidationException forSchema(String schemaError) {
        return new ResourceValidationException(String.format("Schema validation failed: %s", schemaError));
    }
    
    /**
     * 创建必填字段缺失异常
     * 
     * @param fieldName 字段名
     * @return 异常实例
     */
    public static ResourceValidationException forMissingField(String fieldName) {
        return new ResourceValidationException(String.format("Required field '%s' is missing", fieldName));
    }
    
    /**
     * 创建字段值无效异常
     * 
     * @param fieldName 字段名
     * @param value 无效值
     * @return 异常实例
     */
    public static ResourceValidationException forInvalidValue(String fieldName, Object value) {
        return new ResourceValidationException(String.format("Invalid value '%s' for field '%s'", value, fieldName));
    }
}