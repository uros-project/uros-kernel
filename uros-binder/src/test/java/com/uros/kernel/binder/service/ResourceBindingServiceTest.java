package com.uros.kernel.binder.service;

import com.uros.kernel.binder.model.ResourceBinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResourceBindingService 测试类
 */
public class ResourceBindingServiceTest {
    
    private ResourceBindingService bindingService;
    
    @BeforeEach
    public void setUp() {
        bindingService = new ResourceBindingService();
    }
    
    @Test
    public void testCreateBinding() {
        // 创建测试绑定关系
        ResourceBinding binding = new ResourceBinding("resource-001", "resource-002", "DEPENDENCY");
        binding.setName("Test Binding");
        binding.setDescription("Test binding relationship");
        
        // 执行创建操作
        ResourceBinding created = bindingService.createBinding(binding);
        
        // 验证结果
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("resource-001", created.getSourceResourceId());
        assertEquals("resource-002", created.getTargetResourceId());
        assertEquals("DEPENDENCY", created.getBindingType());
        assertEquals("Test Binding", created.getName());
        assertEquals("ACTIVE", created.getStatus());
        assertTrue(created.getEnabled());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
    }
    
    @Test
    public void testGetBindingById() {
        // 创建测试绑定关系
        ResourceBinding binding = new ResourceBinding("resource-001", "resource-002", "DEPENDENCY");
        ResourceBinding created = bindingService.createBinding(binding);
        
        // 根据ID查询
        ResourceBinding found = bindingService.getBindingById(created.getId());
        
        // 验证结果
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getSourceResourceId(), found.getSourceResourceId());
        assertEquals(created.getTargetResourceId(), found.getTargetResourceId());
    }
    
    @Test
    public void testGetBindingsBySourceResourceId() {
        // 创建多个测试绑定关系
        ResourceBinding binding1 = new ResourceBinding("resource-001", "resource-002", "DEPENDENCY");
        ResourceBinding binding2 = new ResourceBinding("resource-001", "resource-003", "ASSOCIATION");
        ResourceBinding binding3 = new ResourceBinding("resource-004", "resource-005", "DEPENDENCY");
        
        bindingService.createBinding(binding1);
        bindingService.createBinding(binding2);
        bindingService.createBinding(binding3);
        
        // 根据源资源ID查询
        List<ResourceBinding> bindings = bindingService.getBindingsBySourceResourceId("resource-001");
        
        // 验证结果
        assertEquals(2, bindings.size());
        assertTrue(bindings.stream().allMatch(b -> "resource-001".equals(b.getSourceResourceId())));
    }
    
    @Test
    public void testGetBindingsByTargetResourceId() {
        // 创建多个测试绑定关系
        ResourceBinding binding1 = new ResourceBinding("resource-001", "resource-002", "DEPENDENCY");
        ResourceBinding binding2 = new ResourceBinding("resource-003", "resource-002", "ASSOCIATION");
        ResourceBinding binding3 = new ResourceBinding("resource-004", "resource-005", "DEPENDENCY");
        
        bindingService.createBinding(binding1);
        bindingService.createBinding(binding2);
        bindingService.createBinding(binding3);
        
        // 根据目标资源ID查询
        List<ResourceBinding> bindings = bindingService.getBindingsByTargetResourceId("resource-002");
        
        // 验证结果
        assertEquals(2, bindings.size());
        assertTrue(bindings.stream().allMatch(b -> "resource-002".equals(b.getTargetResourceId())));
    }
    
    @Test
    public void testGetBindingsByType() {
        // 创建多个测试绑定关系
        ResourceBinding binding1 = new ResourceBinding("resource-001", "resource-002", "DEPENDENCY");
        ResourceBinding binding2 = new ResourceBinding("resource-003", "resource-004", "DEPENDENCY");
        ResourceBinding binding3 = new ResourceBinding("resource-005", "resource-006", "ASSOCIATION");
        
        bindingService.createBinding(binding1);
        bindingService.createBinding(binding2);
        bindingService.createBinding(binding3);
        
        // 根据绑定类型查询
        List<ResourceBinding> dependencyBindings = bindingService.getBindingsByType("DEPENDENCY");
        List<ResourceBinding> associationBindings = bindingService.getBindingsByType("ASSOCIATION");
        
        // 验证结果
        assertEquals(2, dependencyBindings.size());
        assertEquals(1, associationBindings.size());
        assertTrue(dependencyBindings.stream().allMatch(b -> "DEPENDENCY".equals(b.getBindingType())));
        assertTrue(associationBindings.stream().allMatch(b -> "ASSOCIATION".equals(b.getBindingType())));
    }
    
    @Test
    public void testUpdateBinding() {
        // 创建测试绑定关系
        ResourceBinding binding = new ResourceBinding("resource-001", "resource-002", "DEPENDENCY");
        ResourceBinding created = bindingService.createBinding(binding);
        
        // 更新绑定关系
        ResourceBinding updated = new ResourceBinding("resource-001", "resource-003", "ASSOCIATION");
        updated.setName("Updated Binding");
        updated.setDescription("Updated description");
        
        ResourceBinding result = bindingService.updateBinding(created.getId(), updated);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals("resource-003", result.getTargetResourceId());
        assertEquals("ASSOCIATION", result.getBindingType());
        assertEquals("Updated Binding", result.getName());
        assertEquals(created.getCreatedAt(), result.getCreatedAt());
        assertTrue(result.getUpdatedAt().isAfter(created.getUpdatedAt()));
    }
    
    @Test
    public void testDeleteBinding() {
        // 创建测试绑定关系
        ResourceBinding binding = new ResourceBinding("resource-001", "resource-002", "DEPENDENCY");
        ResourceBinding created = bindingService.createBinding(binding);
        
        // 验证绑定关系存在
        assertTrue(bindingService.existsById(created.getId()));
        
        // 删除绑定关系
        boolean deleted = bindingService.deleteBinding(created.getId());
        
        // 验证删除结果
        assertTrue(deleted);
        assertFalse(bindingService.existsById(created.getId()));
        assertNull(bindingService.getBindingById(created.getId()));
    }
    
    @Test
    public void testCleanupExpiredBindings() {
        // 创建测试绑定关系
        ResourceBinding binding1 = new ResourceBinding("resource-001", "resource-002", "DEPENDENCY");
        binding1.setExpiresAt(LocalDateTime.now().minusHours(1)); // 已过期
        
        ResourceBinding binding2 = new ResourceBinding("resource-003", "resource-004", "ASSOCIATION");
        binding2.setExpiresAt(LocalDateTime.now().plusHours(1)); // 未过期
        
        ResourceBinding binding3 = new ResourceBinding("resource-005", "resource-006", "DEPENDENCY");
        // 没有设置过期时间
        
        bindingService.createBinding(binding1);
        bindingService.createBinding(binding2);
        bindingService.createBinding(binding3);
        
        // 执行清理
        int cleanedCount = bindingService.cleanupExpiredBindings();
        
        // 验证结果
        assertEquals(1, cleanedCount);
        assertEquals(2, bindingService.count());
    }
    
    @Test
    public void testValidateBinding() {
        // 测试缺少源资源ID
        ResourceBinding binding1 = new ResourceBinding();
        binding1.setTargetResourceId("resource-002");
        binding1.setBindingType("DEPENDENCY");
        
        assertThrows(IllegalArgumentException.class, () -> {
            bindingService.createBinding(binding1);
        });
        
        // 测试缺少目标资源ID
        ResourceBinding binding2 = new ResourceBinding();
        binding2.setSourceResourceId("resource-001");
        binding2.setBindingType("DEPENDENCY");
        
        assertThrows(IllegalArgumentException.class, () -> {
            bindingService.createBinding(binding2);
        });
        
        // 测试缺少绑定类型
        ResourceBinding binding3 = new ResourceBinding();
        binding3.setSourceResourceId("resource-001");
        binding3.setTargetResourceId("resource-002");
        
        assertThrows(IllegalArgumentException.class, () -> {
            bindingService.createBinding(binding3);
        });
        
        // 测试绑定自己
        ResourceBinding binding4 = new ResourceBinding("resource-001", "resource-001", "DEPENDENCY");
        
        assertThrows(IllegalArgumentException.class, () -> {
            bindingService.createBinding(binding4);
        });
    }
}