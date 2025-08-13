package com.uros.kernel.handle.controller;

import com.uros.kernel.handle.model.UniResource;
import com.uros.kernel.handle.service.UniResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 泛在资源控制器
 * 提供UniResource的REST API接口
 */
@RestController
@RequestMapping("/api/v1/resources")
@CrossOrigin(origins = "*")
public class UniResourceController extends ResolvableController {
    
    // resourceService 已在父类中定义
    
    /**
     * 创建资源
     * 
     * @param resource 资源对象
     * @return 创建的资源
     */
    @PostMapping
    public ResponseEntity<UniResource> createResource(@RequestBody UniResource resource) {
        try {
            UniResource created = resourceService.createResource(resource);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取所有资源
     * 
     * @return 所有资源列表
     */
    @GetMapping
    public ResponseEntity<List<UniResource>> getAllResources(
            @RequestParam(required = false) String typeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean enabled) {
        try {
            List<UniResource> resources;
            
            if (typeId != null) {
                resources = resourceService.getResourcesByTypeId(typeId);
            } else if (name != null) {
                resources = resourceService.getResourcesByName(name);
            } else if (status != null) {
                resources = resourceService.getResourcesByStatus(status);
            } else if (enabled != null) {
                resources = resourceService.getResourcesByEnabled(enabled);
            } else {
                resources = resourceService.getAllResources();
            }
            
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据ID获取资源
     * 
     * @param id 资源ID
     * @return 资源对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<UniResource> getResourceById(@PathVariable String id) {
        try {
            UniResource resource = resourceService.getResourceById(id);
            if (resource != null) {
                return ResponseEntity.ok(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据标签查询资源
     * 
     * @param labels 标签参数
     * @return 匹配的资源列表
     */
    @GetMapping("/by-labels")
    public ResponseEntity<List<UniResource>> getResourcesByLabels(@RequestParam Map<String, String> labels) {
        try {
            List<UniResource> resources = resourceService.getResourcesByLabels(labels);
            return ResponseEntity.ok(resources);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 更新资源
     * 
     * @param id 资源ID
     * @param resource 更新的资源数据
     * @return 更新后的资源
     */
    @PutMapping("/{id}")
    public ResponseEntity<UniResource> updateResource(
            @PathVariable String id, 
            @RequestBody UniResource resource) {
        try {
            UniResource updated = resourceService.updateResource(id, resource);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 部分更新资源
     * 
     * @param id 资源ID
     * @param updates 要更新的字段
     * @return 更新后的资源
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UniResource> patchResource(
            @PathVariable String id, 
            @RequestBody Map<String, Object> updates) {
        try {
            UniResource updated = resourceService.patchResource(id, updates);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 删除资源
     * 
     * @param id 资源ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable String id) {
        try {
            boolean deleted = resourceService.deleteResource(id);
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
     * 检查资源是否存在
     * 
     * @param id 资源ID
     * @return 是否存在
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> checkResourceExists(@PathVariable String id) {
        try {
            boolean exists = resourceService.existsById(id);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取资源统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getResourceStats() {
        try {
            long totalCount = resourceService.count();
            long activeCount = resourceService.getResourcesByStatus("ACTIVE").size();
            long inactiveCount = resourceService.getResourcesByStatus("INACTIVE").size();
            long enabledCount = resourceService.getResourcesByEnabled(true).size();
            long disabledCount = resourceService.getResourcesByEnabled(false).size();
            
            Map<String, Object> stats = Map.of(
                "total", totalCount,
                "active", activeCount,
                "inactive", inactiveCount,
                "enabled", enabledCount,
                "disabled", disabledCount
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据资源类型获取统计信息
     * 
     * @param typeId 资源类型ID
     * @return 该类型的统计信息
     */
    @GetMapping("/stats/by-type/{typeId}")
    public ResponseEntity<Map<String, Object>> getResourceStatsByType(@PathVariable String typeId) {
        try {
            long count = resourceService.countByTypeId(typeId);
            List<UniResource> resources = resourceService.getResourcesByTypeId(typeId);
            
            long activeCount = resources.stream()
                    .filter(r -> "ACTIVE".equals(r.getStatus()))
                    .count();
            long enabledCount = resources.stream()
                    .filter(r -> Boolean.TRUE.equals(r.getEnabled()))
                    .count();
            
            Map<String, Object> stats = Map.of(
                "typeId", typeId,
                "total", count,
                "active", activeCount,
                "enabled", enabledCount
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}