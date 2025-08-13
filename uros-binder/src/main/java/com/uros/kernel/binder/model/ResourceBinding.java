package com.uros.kernel.binder.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 资源绑定关系模型
 * 表示两个或多个资源之间的绑定关系
 */
public class ResourceBinding {
    
    /**
     * 绑定关系唯一标识符
     */
    private String id;
    
    /**
     * 绑定名称
     */
    private String name;
    
    /**
     * 绑定描述
     */
    private String description;
    
    /**
     * 源资源ID
     */
    @JsonProperty("sourceResourceId")
    private String sourceResourceId;
    
    /**
     * 目标资源ID
     */
    @JsonProperty("targetResourceId")
    private String targetResourceId;
    
    /**
     * 绑定类型
     */
    @JsonProperty("bindingType")
    private String bindingType;
    
    /**
     * 绑定状态
     */
    private String status;
    
    /**
     * 绑定属性
     */
    private Map<String, Object> properties;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 创建者
     */
    private String createdBy;
    
    /**
     * 更新者
     */
    private String updatedBy;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 过期时间（可选）
     */
    private LocalDateTime expiresAt;
    
    // 构造函数
    public ResourceBinding() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = true;
        this.status = "ACTIVE";
    }
    
    public ResourceBinding(String sourceResourceId, String targetResourceId, String bindingType) {
        this();
        this.sourceResourceId = sourceResourceId;
        this.targetResourceId = targetResourceId;
        this.bindingType = bindingType;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSourceResourceId() {
        return sourceResourceId;
    }
    
    public void setSourceResourceId(String sourceResourceId) {
        this.sourceResourceId = sourceResourceId;
    }
    
    public String getTargetResourceId() {
        return targetResourceId;
    }
    
    public void setTargetResourceId(String targetResourceId) {
        this.targetResourceId = targetResourceId;
    }
    
    public String getBindingType() {
        return bindingType;
    }
    
    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    @Override
    public String toString() {
        return "ResourceBinding{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sourceResourceId='" + sourceResourceId + '\'' +
                ", targetResourceId='" + targetResourceId + '\'' +
                ", bindingType='" + bindingType + '\'' +
                ", status='" + status + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}