package com.uros.kernel.telemetry.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.*;

/**
 * 遥测数据Schema测试类
 * 根据telemetry.schema.json动态生成符合schema定义的JSON对象
 */
public class TelemetrySchemaTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    
    @Test
    public void testGenerateTelemetryObjects() throws Exception {
        System.out.println("=== 遥测数据Schema测试用例 ===");
        System.out.println("根据telemetry.schema.json动态生成符合schema定义的JSON对象\n");
        
        // 加载schema文件
        JsonNode schemaNode = loadSchemaFromFile();
        if (schemaNode == null) {
            System.err.println("无法加载schema文件");
            return;
        }
        
        System.out.println("Schema加载成功，开始生成测试对象...\n");
        
        // 生成多个测试对象
        List<JsonNode> testObjects = generateTestObjectsFromSchema(schemaNode, 5);
        
        for (int i = 0; i < testObjects.size(); i++) {
            System.out.println("=== 测试对象 " + (i + 1) + " ===");
            String json = objectMapper.writeValueAsString(testObjects.get(i));
            System.out.println(json);
            System.out.println();
        }
        
        System.out.println("总共生成了 " + testObjects.size() + " 个符合schema的遥测数据对象");
    }
    
    /**
     * 从资源文件加载schema
     */
    private JsonNode loadSchemaFromFile() {
        try {
            InputStream schemaStream = getClass().getClassLoader()
                    .getResourceAsStream("schemas/telemetry.schema.json");
            if (schemaStream == null) {
                System.err.println("找不到schema文件: schemas/telemetry.schema.json");
                return null;
            }
            return objectMapper.readTree(schemaStream);
        } catch (Exception e) {
            System.err.println("加载schema文件失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 根据schema动态生成测试对象
     */
    private List<JsonNode> generateTestObjectsFromSchema(JsonNode schemaNode, int count) {
        List<JsonNode> objects = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            JsonNode sampleObject = generateSampleFromSchema(schemaNode, objectMapper);
            objects.add(sampleObject);
        }
        
        return objects;
    }
    
    /**
     * 根据schema生成示例数据
     */
    private JsonNode generateSampleFromSchema(JsonNode schemaNode, ObjectMapper mapper) {
        if (schemaNode == null) {
            return mapper.nullNode();
        }
        
        // 处理$ref引用
        JsonNode refNode = schemaNode.get("$ref");
        if (refNode != null) {
            JsonNode resolvedSchema = resolveReference(refNode.asText());
            if (resolvedSchema != null) {
                // 如果解析成功，合并解析的schema和当前schema
                JsonNode mergedSchema = mergeSchemas(resolvedSchema, schemaNode, mapper);
                return generateSampleFromSchema(mergedSchema, mapper);
            } else {
                // 如果无法解析引用，继续处理当前schema的其他部分
                System.out.println("警告: 无法解析$ref引用: " + refNode.asText() + "，继续处理当前schema");
            }
        }
        
        // 处理oneOf
        JsonNode oneOfNode = schemaNode.get("oneOf");
        if (oneOfNode != null && oneOfNode.isArray() && oneOfNode.size() > 0) {
            Random random = new Random();
            int index = random.nextInt(oneOfNode.size());
            JsonNode selectedSchema = oneOfNode.get(index);
            return generateSampleFromSchema(selectedSchema, mapper);
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
                        // 处理$ref引用
                        JsonNode refNode = fieldSchema.get("$ref");
                        if (refNode != null) {
                            JsonNode resolvedSchema = resolveReference(refNode.asText());
                            if (resolvedSchema != null) {
                                JsonNode fieldValue = generateSampleFromSchema(resolvedSchema, mapper);
                                objectNode.set(fieldName, fieldValue);
                            } else {
                                 // 如果无法解析引用，则报错
                                 throw new RuntimeException("无法解析$ref引用: " + refNode.asText() + ", 字段: " + fieldName);
                             }
                        } else {
                            JsonNode fieldValue = generateSampleFromSchema(fieldSchema, mapper);
                            objectNode.set(fieldName, fieldValue);
                        }
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
        
        JsonNode constNode = schemaNode.get("const");
        if (constNode != null) {
            return constNode;
        }
        
        JsonNode descriptionNode = schemaNode.get("description");
        String description = descriptionNode != null ? descriptionNode.asText() : "";
        
        // 根据描述生成相应的示例值
        if (description.contains("标识") || description.contains("ID") || description.contains("id")) {
            return mapper.valueToTree("sample-" + UUID.randomUUID().toString().substring(0, 8));
        } else if (description.contains("名称") || description.contains("name")) {
            String[] names = {"温度传感器", "湿度传感器", "压力传感器", "光照传感器", "噪音传感器"};
            return mapper.valueToTree(names[random.nextInt(names.length)]);
        } else if (description.contains("量纲") || description.contains("单位")) {
            String[] units = {"°C", "%", "Pa", "lux", "dB", "V", "A", "Hz"};
            return mapper.valueToTree(units[random.nextInt(units.length)]);
        } else if (description.contains("Base64") || description.contains("二进制")) {
            // 生成模拟的Base64数据
            byte[] data = new byte[32];
            random.nextBytes(data);
            return mapper.valueToTree(Base64.getEncoder().encodeToString(data));
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
        JsonNode defaultNode = schemaNode.get("default");
        
        // 如果有默认值，有50%概率使用默认值
        if (defaultNode != null && random.nextBoolean()) {
            return defaultNode;
        }
        
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
        JsonNode defaultNode = schemaNode.get("default");
        
        // 如果有默认值，有50%概率使用默认值
        if (defaultNode != null && random.nextBoolean()) {
            return defaultNode;
        }
        
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
    
    /**
     * 解析$ref引用
     */
    private JsonNode resolveReference(String ref) {
        try {
            // 解析引用路径
            if (ref.contains("#")) {
                String[] parts = ref.split("#");
                String filePath = parts[0];
                String jsonPointer = parts.length > 1 ? parts[1] : "";
                
                // 加载外部schema文件
                JsonNode externalSchema = loadExternalSchema(filePath);
                if (externalSchema == null) {
                    System.err.println("无法加载外部schema: " + filePath);
                    return null;
                }
                
                // 解析JSON Pointer
                if (!jsonPointer.isEmpty()) {
                    return resolveJsonPointer(externalSchema, jsonPointer);
                } else {
                    return externalSchema;
                }
            } else {
                // 直接文件引用
                return loadExternalSchema(ref);
            }
        } catch (Exception e) {
            System.err.println("解析$ref引用失败: " + ref + ", 错误: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 加载外部schema文件
     */
    private JsonNode loadExternalSchema(String relativePath) {
        try {
            // 将相对路径转换为资源路径
            String resourcePath = convertToResourcePath(relativePath);
            
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (schemaStream == null) {
                System.err.println("找不到外部schema文件: " + resourcePath);
                return null;
            }
            
            return objectMapper.readTree(schemaStream);
        } catch (Exception e) {
            System.err.println("加载外部schema失败: " + relativePath + ", 错误: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 将相对路径转换为资源路径
     */
    private String convertToResourcePath(String relativePath) {
        // 处理相对路径 "../../../uros-handle/src/main/resources/schemas/resolvable.schema.json"
        // 转换为资源路径 "schemas/resolvable.schema.json"
        if (relativePath.contains("main/resources/")) {
            int index = relativePath.indexOf("main/resources/");
            return relativePath.substring(index + "main/resources/".length());
        }
        return relativePath;
    }
    
    /**
      * 解析JSON Pointer
      */
    private JsonNode resolveJsonPointer(JsonNode rootNode, String pointer) {
        if (pointer.isEmpty() || pointer.equals("/")) {
            return rootNode;
        }
        
        // 移除开头的 '/'
        if (pointer.startsWith("/")) {
            pointer = pointer.substring(1);
        }
        
        String[] parts = pointer.split("/");
        JsonNode currentNode = rootNode;
        
        for (String part : parts) {
            if (currentNode == null) {
                return null;
            }
            
            // 解码JSON Pointer转义字符
            part = part.replace("~1", "/").replace("~0", "~");
            
            if (currentNode.isObject()) {
                currentNode = currentNode.get(part);
            } else if (currentNode.isArray()) {
                try {
                    int index = Integer.parseInt(part);
                    currentNode = currentNode.get(index);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
        
        return currentNode;
    }
    
    /**
     * 合并两个schema节点
     */
    private JsonNode mergeSchemas(JsonNode resolvedSchema, JsonNode currentSchema, ObjectMapper mapper) {
        if (resolvedSchema == null) {
            return currentSchema;
        }
        if (currentSchema == null) {
            return resolvedSchema;
        }
        
        // 创建合并后的schema
        ObjectNode mergedSchema = mapper.createObjectNode();
        
        // 首先复制解析的schema的所有字段
        if (resolvedSchema.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = resolvedSchema.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                mergedSchema.set(field.getKey(), field.getValue());
            }
        }
        
        // 然后覆盖/添加当前schema的字段（除了$ref）
        if (currentSchema.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = currentSchema.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey();
                
                // 跳过$ref字段，因为已经解析过了
                if (!"$ref".equals(fieldName)) {
                    mergedSchema.set(fieldName, field.getValue());
                }
            }
        }
        
        return mergedSchema;
    }
}