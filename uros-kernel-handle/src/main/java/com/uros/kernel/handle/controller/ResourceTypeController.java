package com.uros.kernel.handle.controller;

import com.uros.kernel.handle.KernelHandle;
import com.uros.kernel.handle.ResourceType;
import com.uros.kernel.handle.dto.ApiResponse;
import com.uros.kernel.handle.dto.ResourceTypeRequest;
import com.uros.kernel.handle.dto.ResourceTypeUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源类型 REST API 控制器
 * 提供资源类型管理的所有 REST 接口
 */
@RestController
@RequestMapping("/api/resource-types")
@CrossOrigin(origins = "*")
public class ResourceTypeController {
    
    private final KernelHandle kernelHandle;
    
    @Autowired
    public ResourceTypeController(KernelHandle kernelHandle) {
        this.kernelHandle = kernelHandle;
    }
    
    /**
     * 注册资源类型
     * POST /api/resource-types
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ResourceType>> registerResourceType(@RequestBody ResourceTypeRequest request) {
        try {
            ResourceType resourceType = kernelHandle.registerResourceType(
                request.getName(), 
                request.getDescription(), 
                request.getSchema()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("资源类型注册成功", resourceType));
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("资源类型注册失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID查询资源类型
     * GET /api/resource-types/{typeId}
     */
    @GetMapping("/{typeId}")
    public ResponseEntity<ApiResponse<ResourceType>> getResourceTypeById(@PathVariable String typeId) {
        try {
            ResourceType resourceType = kernelHandle.getResourceTypeById(typeId);
            
            if (resourceType == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(ApiResponse.success(resourceType));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("查询资源类型失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据名称查询资源类型
     * GET /api/resource-types/name/{name}
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<ResourceType>> getResourceTypeByName(@PathVariable String name) {
        try {
            ResourceType resourceType = kernelHandle.getResourceTypeByName(name);
            
            if (resourceType == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(ApiResponse.success(resourceType));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("查询资源类型失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取所有资源类型
     * GET /api/resource-types
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ResourceType>>> getAllResourceTypes() {
        try {
            List<ResourceType> resourceTypes = kernelHandle.getAllResourceTypes();
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", resourceTypes));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("查询资源类型列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新资源类型
     * PUT /api/resource-types/{typeId}
     */
    @PutMapping("/{typeId}")
    public ResponseEntity<ApiResponse<ResourceType>> updateResourceType(
            @PathVariable String typeId,
            @RequestBody ResourceTypeUpdateRequest request) {
        try {
            ResourceType resourceType = kernelHandle.getResourceTypeById(typeId);
            
            if (resourceType == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 更新资源类型
            resourceType.setDescription(request.getDescription());
            resourceType.setSchema(request.getSchema());
            
            return ResponseEntity.ok(ApiResponse.success("资源类型更新成功", resourceType));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("更新资源类型失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除资源类型
     * DELETE /api/resource-types/{typeId}
     */
    @DeleteMapping("/{typeId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteResourceType(@PathVariable String typeId) {
        try {
            boolean deleted = kernelHandle.deleteResourceType(typeId);
            
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("资源类型删除成功", true));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("删除资源类型失败: " + e.getMessage()));
        }
    }
    
    /**
     * 检查资源类型是否存在
     * GET /api/resource-types/{typeId}/exists
     */
    @GetMapping("/{typeId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkResourceTypeExists(@PathVariable String typeId) {
        try {
            ResourceType resourceType = kernelHandle.getResourceTypeById(typeId);
            boolean exists = resourceType != null;
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", exists));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("检查资源类型存在性失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取资源类型数量
     * GET /api/resource-types/count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getResourceTypeCount() {
        try {
            int count = kernelHandle.getResourceTypeCount();
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", count));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取资源类型数量失败: " + e.getMessage()));
        }
    }
}
