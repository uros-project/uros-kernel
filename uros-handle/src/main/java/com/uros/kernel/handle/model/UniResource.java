package com.uros.kernel.handle.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 泛在资源（UniResource）
 * 根据指定的UniResourceType（JSON Schema）生成的具体资源实例
 */
public class UniResource extends Resolvable {
    
    /**
     * 资源类型ID，关联到UniResourceType
     */
    private String typeId;
    
    /**
     * 资源名称
     */
    private String name;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 资源数据内容（JSON格式）
     * 符合对应UniResourceType的schema定义
     */
    @JsonProperty("data")
    private JsonNode data;
    
    /**
     * 资源状态
     */
    private String status;
    
    /**
     * 资源标签
     */
    private Map<String, String> labels;
    
    /**
     * 扩展属性
     */
    private Map<String, Object> metadata;
    
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
     * 资源版本号
     */
    private Long version;
    
    // 构造函数
    public UniResource() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = true;
        this.status = "ACTIVE";
        this.version = 1L;
    }
    
    public UniResource(String id, String typeId, String name, JsonNode data) {
        this();
        this.id = id;
        this.typeId = typeId;
        this.name = name;
        this.data = data;
    }
    
    // Getter和Setter方法
    
    public String getTypeId() {
        return typeId;
    }
    
    public void setTypeId(String typeId) {
        this.typeId = typeId;
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
    
    public JsonNode getData() {
        return data;
    }
    
    public void setData(JsonNode data) {
        this.data = data;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Map<String, String> getLabels() {
        return labels;
    }
    
    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
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
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        return "UniResource{" +
                "id='" + id + '\'' +
                ", typeId='" + typeId + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", version=" + version +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}