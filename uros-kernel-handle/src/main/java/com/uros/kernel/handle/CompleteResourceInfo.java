package com.uros.kernel.handle;

import com.fasterxml.jackson.databind.JsonNode;

/** 完整资源信息 包含资源实例、类型定义和schema信息 */
public class CompleteResourceInfo {

  private final ResourceInstance instance;
  private final ResourceType type;
  private final JsonNode schema;

  public CompleteResourceInfo(ResourceInstance instance, ResourceType type, JsonNode schema) {
    this.instance = instance;
    this.type = type;
    this.schema = schema;
  }

  // Getters
  public ResourceInstance getInstance() {
    return instance;
  }

  public ResourceType getType() {
    return type;
  }

  public JsonNode getSchema() {
    return schema;
  }

  /** 获取资源ID */
  public String getResourceId() {
    return instance != null ? instance.getId() : null;
  }

  /** 获取资源类型名称 */
  public String getTypeName() {
    return type != null ? type.getName() : null;
  }

  /** 获取资源数据 */
  public JsonNode getResourceData() {
    return instance != null ? instance.getData() : null;
  }

  /** 获取资源状态 */
  public String getResourceStatus() {
    return instance != null ? instance.getStatus() : null;
  }

  /** 检查资源是否处于活动状态 */
  public boolean isActive() {
    return "ACTIVE".equals(getResourceStatus());
  }
}
