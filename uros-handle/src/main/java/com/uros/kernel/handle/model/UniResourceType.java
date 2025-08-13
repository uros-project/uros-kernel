package com.uros.kernel.handle.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.Random;
import java.util.Iterator;

/**
 * 泛在资源类型（UniResourceType）
 * 实际上是一个JSON Schema，用于定义UniResource的结构和验证规则
 */
public class UniResourceType extends Resolvable {
    
    /**
     * 资源类型名称
     */
    private String name;
    
    /**
     * 资源类型描述
     */
    private String description;
    
    /**
     * 版本号
     */
    private String version;
    
    /**
     * JSON Schema定义
     * 用于验证和生成UniResource实例
     */
    @JsonProperty("schema")
    private JsonNode schema;
    
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
    
    // 构造函数
    public UniResourceType() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = true;
    }
    
    public UniResourceType(String id, String name, String description, String version, JsonNode schema) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
        this.version = version;
        this.schema = schema;
    }
    
    // Getter和Setter方法
    
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
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public JsonNode getSchema() {
        return schema;
    }
    
    public void setSchema(JsonNode schema) {
        this.schema = schema;
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
    
    /**
     * 基于当前ResourceType的schema生成示例JSON数据
     * @return 符合schema定义的示例JSON对象
     */
    public JsonNode generateSampleData() {
        if (schema == null) {
            throw new IllegalStateException("Schema is null, cannot generate sample data");
        }
        
        ObjectMapper mapper = new ObjectMapper();
        return generateSampleFromSchema(schema, mapper);
    }
    
    /**
     * 基于当前ResourceType的schema创建一个UniResource实例
     * @param resourceName 资源名称
     * @return 新创建的UniResource实例
     */
    public UniResource createUniResource(String resourceName) {
        return createUniResource(resourceName, null);
    }
    
    /**
     * 基于当前ResourceType的schema创建一个UniResource实例
     * @param resourceName 资源名称
     * @param customData 自定义数据，如果为null则使用生成的示例数据
     * @return 新创建的UniResource实例
     */
    public UniResource createUniResource(String resourceName, JsonNode customData) {
        if (this.id == null) {
            throw new IllegalStateException("ResourceType id is null, cannot create UniResource");
        }
        
        String resourceId = UUID.randomUUID().toString();
        JsonNode data = customData != null ? customData : generateSampleData();
        
        UniResource resource = new UniResource(resourceId, this.id, resourceName, data);
        resource.setDescription("基于 " + this.name + " 类型创建的资源实例");
        
        return resource;
    }
    
    /**
     * 递归地从JSON Schema生成示例数据
     * @param schemaNode JSON Schema节点
     * @param mapper ObjectMapper实例
     * @return 生成的示例数据
     */
    private JsonNode generateSampleFromSchema(JsonNode schemaNode, ObjectMapper mapper) {
        if (schemaNode == null) {
            return mapper.nullNode();
        }
        
        JsonNode typeNode = schemaNode.get("type");
        if (typeNode == null) {
            return mapper.nullNode();
        }
        
        String type = typeNode.asText();
        Random random = new Random();
        
        switch (type) {
            case "object":
                return generateObjectSample(schemaNode, mapper, random);
            case "array":
                return generateArraySample(schemaNode, mapper, random);
            case "string":
                return generateStringSample(schemaNode, mapper, random);
            case "number":
            case "integer":
                return generateNumberSample(schemaNode, mapper, random, type);
            case "boolean":
                return generateBooleanSample(schemaNode, mapper, random);
            default:
                return mapper.nullNode();
        }
    }
    
    /**
     * 生成对象类型的示例数据
     */
    private JsonNode generateObjectSample(JsonNode schemaNode, ObjectMapper mapper, Random random) {
        ObjectNode objectNode = mapper.createObjectNode();
        
        JsonNode propertiesNode = schemaNode.get("properties");
        JsonNode requiredNode = schemaNode.get("required");
        
        if (propertiesNode != null && propertiesNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = propertiesNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey();
                JsonNode fieldSchema = field.getValue();
                
                // 检查是否为必需字段，或者随机决定是否包含可选字段
                boolean isRequired = requiredNode != null && requiredNode.isArray() && 
                    containsValue(requiredNode, fieldName);
                boolean shouldInclude = isRequired || random.nextBoolean();
                
                if (shouldInclude) {
                    // 检查是否有默认值
                    JsonNode defaultValue = fieldSchema.get("default");
                    if (defaultValue != null) {
                        objectNode.set(fieldName, defaultValue);
                    } else {
                        JsonNode fieldValue = generateSampleFromSchema(fieldSchema, mapper);
                        objectNode.set(fieldName, fieldValue);
                    }
                }
            }
        }
        
        return objectNode;
    }
    
    /**
     * 生成数组类型的示例数据
     */
    private JsonNode generateArraySample(JsonNode schemaNode, ObjectMapper mapper, Random random) {
        ArrayNode arrayNode = mapper.createArrayNode();
        
        JsonNode itemsNode = schemaNode.get("items");
        if (itemsNode != null) {
            // 生成1-3个示例元素
            int arraySize = random.nextInt(3) + 1;
            for (int i = 0; i < arraySize; i++) {
                JsonNode itemValue = generateSampleFromSchema(itemsNode, mapper);
                arrayNode.add(itemValue);
            }
        }
        
        return arrayNode;
    }
    
    /**
     * 生成字符串类型的示例数据
     */
    private JsonNode generateStringSample(JsonNode schemaNode, ObjectMapper mapper, Random random) {
        JsonNode enumNode = schemaNode.get("enum");
        if (enumNode != null && enumNode.isArray() && enumNode.size() > 0) {
            // 如果有枚举值，随机选择一个
            int index = random.nextInt(enumNode.size());
            return enumNode.get(index);
        }
        
        JsonNode descriptionNode = schemaNode.get("description");
        String description = descriptionNode != null ? descriptionNode.asText() : "";
        
        // 根据描述生成相应的示例值
        if (description.contains("标识") || description.contains("ID") || description.contains("id")) {
            return mapper.valueToTree("sample-" + UUID.randomUUID().toString().substring(0, 8));
        } else if (description.contains("名称") || description.contains("name")) {
            return mapper.valueToTree("示例名称");
        } else if (description.contains("房间")) {
            String[] rooms = {"客厅", "卧室", "厨房", "书房", "阳台"};
            return mapper.valueToTree(rooms[random.nextInt(rooms.length)]);
        } else if (description.contains("位置")) {
            String[] positions = {"北墙", "南墙", "东墙", "西墙", "中央"};
            return mapper.valueToTree(positions[random.nextInt(positions.length)]);
        } else {
            return mapper.valueToTree("示例文本");
        }
    }
    
    /**
     * 生成数字类型的示例数据
     */
    private JsonNode generateNumberSample(JsonNode schemaNode, ObjectMapper mapper, Random random, String type) {
        JsonNode minimumNode = schemaNode.get("minimum");
        JsonNode maximumNode = schemaNode.get("maximum");
        
        double min = minimumNode != null ? minimumNode.asDouble() : 0;
        double max = maximumNode != null ? maximumNode.asDouble() : 100;
        
        if ("integer".equals(type)) {
            long value = (long) (min + random.nextDouble() * (max - min));
            return mapper.valueToTree(value);
        } else {
            double value = min + random.nextDouble() * (max - min);
            // 保留两位小数
            value = Math.round(value * 100.0) / 100.0;
            return mapper.valueToTree(value);
        }
    }
    
    /**
     * 生成布尔类型的示例数据
     */
    private JsonNode generateBooleanSample(JsonNode schemaNode, ObjectMapper mapper, Random random) {
        return mapper.valueToTree(random.nextBoolean());
    }
    
    /**
     * 检查数组节点是否包含指定的字符串值
     */
    private boolean containsValue(JsonNode arrayNode, String value) {
        if (arrayNode == null || !arrayNode.isArray()) {
            return false;
        }
        for (JsonNode element : arrayNode) {
            if (element.isTextual() && element.asText().equals(value)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "UniResourceType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}