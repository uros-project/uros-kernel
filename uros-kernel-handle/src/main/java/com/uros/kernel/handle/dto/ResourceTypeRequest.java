package com.uros.kernel.handle.dto;

import com.fasterxml.jackson.databind.JsonNode;

/** 资源类型请求DTO */
public class ResourceTypeRequest {

  private String name;
  private String description;
  private JsonNode schema;

  public ResourceTypeRequest() {}

  public ResourceTypeRequest(String name, String description, JsonNode schema) {
    this.name = name;
    this.description = description;
    this.schema = schema;
  }

  // Getters and Setters
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
  }
}
