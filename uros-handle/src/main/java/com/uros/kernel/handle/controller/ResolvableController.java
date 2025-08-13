package com.uros.kernel.handle.controller;

import com.uros.kernel.handle.model.Resolvable;
import com.uros.kernel.handle.service.UniResourceService;
import com.uros.kernel.handle.service.UniResourceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 可解析对象控制器基类
 * 提供统一的resolve方法来根据ID查找Resolvable对象
 */
@RestController
public abstract class ResolvableController {
    
    @Autowired
    protected UniResourceService resourceService;
    
    @Autowired
    protected UniResourceTypeService resourceTypeService;
    
    /**
     * 根据ID解析对象
     * 先尝试从UniResource中查找，如果没找到则从UniResourceType中查找
     * 
     * @param id 对象ID
     * @return 找到的Resolvable对象，如果都没找到则返回404
     */
    @GetMapping("/resolve/{id}")
    public ResponseEntity<Resolvable> resolve(@PathVariable String id) {
        try {
            // 首先尝试从UniResource中查找
            var resource = resourceService.getResourceById(id);
            if (resource != null) {
                return ResponseEntity.ok(resource);
            }
            
            // 如果没找到，尝试从UniResourceType中查找
            var resourceType = resourceTypeService.getResourceTypeById(id);
            if (resourceType != null) {
                return ResponseEntity.ok(resourceType);
            }
            
            // 都没找到，返回404
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}