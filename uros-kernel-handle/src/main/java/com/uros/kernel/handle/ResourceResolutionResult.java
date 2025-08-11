package com.uros.kernel.handle;

/**
 * 资源解析结果
 * 封装资源解析的结果信息
 */
public class ResourceResolutionResult {
    
    private final String resourceId;
    private final boolean success;
    private final ResourceInstance instance;
    private final ResourceType type;
    private final String errorMessage;
    
    private ResourceResolutionResult(String resourceId, boolean success, ResourceInstance instance, 
                                   ResourceType type, String errorMessage) {
        this.resourceId = resourceId;
        this.success = success;
        this.instance = instance;
        this.type = type;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 创建成功的结果
     */
    public static ResourceResolutionResult success(String resourceId, ResourceInstance instance, ResourceType type) {
        return new ResourceResolutionResult(resourceId, true, instance, type, null);
    }
    
    /**
     * 创建资源未找到的结果
     */
    public static ResourceResolutionResult notFound(String resourceId) {
        return new ResourceResolutionResult(resourceId, false, null, null, 
            "Resource with ID '" + resourceId + "' not found");
    }
    
    /**
     * 创建类型未找到的结果
     */
    public static ResourceResolutionResult typeNotFound(String resourceId, String typeId) {
        return new ResourceResolutionResult(resourceId, false, null, null, 
            "Resource type with ID '" + typeId + "' not found for resource '" + resourceId + "'");
    }
    
    // Getters
    public String getResourceId() { return resourceId; }
    public boolean isSuccess() { return success; }
    public ResourceInstance getInstance() { return instance; }
    public ResourceType getType() { return type; }
    public String getErrorMessage() { return errorMessage; }
}
