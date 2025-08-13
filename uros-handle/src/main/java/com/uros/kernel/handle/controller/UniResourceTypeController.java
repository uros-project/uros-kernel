package com.uros.kernel.handle.controller;

import com.uros.kernel.handle.model.UniResourceType;
import com.uros.kernel.handle.service.UniResourceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 泛在资源类型控制器
 * 提供UniResourceType的REST API接口
 */
@RestController
@RequestMapping("/api/v1/resource-types")
@CrossOrigin(origins = "*")
public class UniResourceTypeController extends ResolvableController {
    
    // resourceTypeService 已在父类中定义
    
    /**
     * 创建资源类型
     * 
     * @param resourceType 资源类型对象
     * @return 创建的资源类型
     */
    @PostMapping
    public ResponseEntity<UniResourceType> createResourceType(@RequestBody UniResourceType resourceType) {
        try {
            UniResourceType created = resourceTypeService.createResourceType(resourceType);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取所有资源类型
     * 
     * @return 所有资源类型列表
     */
    @GetMapping
    public ResponseEntity<List<UniResourceType>> getAllResourceTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean enabled) {
        try {
            List<UniResourceType> resourceTypes;
            
            if (name != null) {
                resourceTypes = resourceTypeService.getResourceTypesByName(name);
            } else if (enabled != null) {
                resourceTypes = resourceTypeService.getResourceTypesByEnabled(enabled);
            } else {
                resourceTypes = resourceTypeService.getAllResourceTypes();
            }
            
            return ResponseEntity.ok(resourceTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据ID获取资源类型
     * 
     * @param id 资源类型ID
     * @return 资源类型对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<UniResourceType> getResourceTypeById(@PathVariable String id) {
        try {
            UniResourceType resourceType = resourceTypeService.getResourceTypeById(id);
            if (resourceType != null) {
                return ResponseEntity.ok(resourceType);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 更新资源类型
     * 
     * @param id 资源类型ID
     * @param resourceType 更新的资源类型数据
     * @return 更新后的资源类型
     */
    @PutMapping("/{id}")
    public ResponseEntity<UniResourceType> updateResourceType(
            @PathVariable String id, 
            @RequestBody UniResourceType resourceType) {
        try {
            UniResourceType updated = resourceTypeService.updateResourceType(id, resourceType);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 部分更新资源类型
     * 
     * @param id 资源类型ID
     * @param updates 要更新的字段
     * @return 更新后的资源类型
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UniResourceType> patchResourceType(
            @PathVariable String id, 
            @RequestBody Map<String, Object> updates) {
        try {
            UniResourceType updated = resourceTypeService.patchResourceType(id, updates);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 删除资源类型
     * 
     * @param id 资源类型ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourceType(@PathVariable String id) {
        try {
            boolean deleted = resourceTypeService.deleteResourceType(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 检查资源类型是否存在
     * 
     * @param id 资源类型ID
     * @return 是否存在
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> checkResourceTypeExists(@PathVariable String id) {
        try {
            boolean exists = resourceTypeService.existsById(id);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取资源类型统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getResourceTypeStats() {
        try {
            long totalCount = resourceTypeService.count();
            long enabledCount = resourceTypeService.getResourceTypesByEnabled(true).size();
            long disabledCount = resourceTypeService.getResourceTypesByEnabled(false).size();
            
            Map<String, Object> stats = Map.of(
                "total", totalCount,
                "enabled", enabledCount,
                "disabled", disabledCount
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}