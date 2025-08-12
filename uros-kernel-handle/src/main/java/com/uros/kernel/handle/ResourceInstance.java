package com.uros.kernel.handle;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;

/** 资源实例 基于资源类型创建的具体资源对象 */
public class ResourceInstance {

  private String id;
  private String typeId;
  private String typeName;
  private JsonNode data; // 资源数据（JSON格式）
  private long createdAt;
  private long updatedAt;
  private String status; // 资源状态：ACTIVE, INACTIVE, DELETED

  // 标记：当调用 setUpdatedAt 手动设置更新时间戳后，抑制下一次自动更新时间戳
  private transient boolean suppressNextAutoUpdate;

  public ResourceInstance() {
    this.id = UUID.randomUUID().toString();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.status = "ACTIVE";
    this.suppressNextAutoUpdate = false;
  }

  public ResourceInstance(String typeId, String typeName, JsonNode data) {
    this();
    this.typeId = typeId;
    this.typeName = typeName;
    this.data = data;
  }

  // Getters and Setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTypeId() {
    return typeId;
  }

  public void setTypeId(String typeId) {
    this.typeId = typeId;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public JsonNode getData() {
    return data;
  }

  public void setData(JsonNode data) {
    this.data = data;
    if (this.suppressNextAutoUpdate) {
      // 仅抑制一次自动更新时间戳，然后恢复
      this.suppressNextAutoUpdate = false;
    } else {
      this.updatedAt = System.currentTimeMillis();
    }
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
    // 标记在下一次属性自动更新时不覆盖手动设置的时间戳
    this.suppressNextAutoUpdate = true;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
    if (this.suppressNextAutoUpdate) {
      this.suppressNextAutoUpdate = false;
    } else {
      this.updatedAt = System.currentTimeMillis();
    }
  }
}
