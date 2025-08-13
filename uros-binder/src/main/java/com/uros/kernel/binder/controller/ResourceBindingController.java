package com.uros.kernel.binder.controller;

import com.uros.kernel.binder.model.ResourceBinding;
import com.uros.kernel.binder.service.ResourceBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源绑定控制器
 * 提供ResourceBinding的REST API接口
 */
@RestController
@RequestMapping("/api/v1/bindings")
@CrossOrigin(origins = "*")
public class ResourceBindingController {
    
    @Autowired
    private ResourceBindingService bindingService;
    
    /**
     * 创建绑定关系
     * 
     * @param binding 绑定对象
     * @return 创建的绑定关系
     */
    @PostMapping
    public ResponseEntity<ResourceBinding> createBinding(@RequestBody ResourceBinding binding) {
        try {
            ResourceBinding created = bindingService.createBinding(binding);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取所有绑定关系
     * 
     * @return 所有绑定关系列表
     */
    @GetMapping
    public ResponseEntity<List<ResourceBinding>> getAllBindings(
            @RequestParam(required = false) String sourceResourceId,
            @RequestParam(required = false) String targetResourceId,
            @RequestParam(required = false) String bindingType) {
        
        try {
            List<ResourceBinding> bindings;
            
            if (sourceResourceId != null) {
                bindings = bindingService.getBindingsBySourceResourceId(sourceResourceId);
            } else if (targetResourceId != null) {
                bindings = bindingService.getBindingsByTargetResourceId(targetResourceId);
            } else if (bindingType != null) {
                bindings = bindingService.getBindingsByType(bindingType);
            } else {
                bindings = bindingService.getAllBindings();
            }
            
            return ResponseEntity.ok(bindings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据ID获取绑定关系
     * 
     * @param id 绑定ID
     * @return 绑定关系
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceBinding> getBindingById(@PathVariable String id) {
        try {
            ResourceBinding binding = bindingService.getBindingById(id);
            if (binding != null) {
                return ResponseEntity.ok(binding);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 更新绑定关系
     * 
     * @param id 绑定ID
     * @param binding 更新的绑定对象
     * @return 更新后的绑定关系
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResourceBinding> updateBinding(
            @PathVariable String id, 
            @RequestBody ResourceBinding binding) {
        try {
            ResourceBinding updated = bindingService.updateBinding(id, binding);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 删除绑定关系
     * 
     * @param id 绑定ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBinding(@PathVariable String id) {
        try {
            boolean deleted = bindingService.deleteBinding(id);
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
     * 检查绑定关系是否存在
     * 
     * @param id 绑定ID
     * @return 存在性检查结果
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> checkBindingExists(@PathVariable String id) {
        try {
            boolean exists = bindingService.existsById(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取绑定关系统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getBindingStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalBindings", bindingService.count());
            stats.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 清理过期的绑定关系
     * 
     * @return 清理结果
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupExpiredBindings() {
        try {
            int cleanedCount = bindingService.cleanupExpiredBindings();
            Map<String, Object> response = new HashMap<>();
            response.put("cleanedCount", cleanedCount);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}