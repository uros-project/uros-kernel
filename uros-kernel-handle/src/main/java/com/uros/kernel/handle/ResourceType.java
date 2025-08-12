package com.uros.kernel.handle;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;

/** 资源类型定义 描述资源的元数据结构和约束 */
public class ResourceType {

  private String id;
  private String name;
  private String description;
  private JsonNode schema; // JSON Schema 定义
  private long createdAt;
  private long updatedAt;

  public ResourceType() {
    this.id = UUID.randomUUID().toString();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
  }

  public ResourceType(String name, String description, JsonNode schema) {
    this();
    this.name = name;
    this.description = description;
    this.schema = schema;
  }

  // Getters and Setters
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

  public JsonNode getSchema() {
    return schema;
  }

  public void setSchema(JsonNode schema) {
    this.schema = schema;
    this.updatedAt = System.currentTimeMillis();
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(long updatedAt) {
    this.updatedAt = updatedAt;
  }
}
