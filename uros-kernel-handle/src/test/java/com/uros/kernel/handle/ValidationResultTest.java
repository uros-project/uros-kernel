package com.uros.kernel.handle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ValidationResult 类的测试
 */
public class ValidationResultTest {
    
    @Test
    void testSuccessStaticMethod() {
        ValidationResult result = ValidationResult.success();
        
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertTrue(result.getErrors().isEmpty());
        assertTrue(result.getWarnings().isEmpty());
    }
    
    @Test
    void testFailureWithSingleError() {
        String errorMessage = "Validation failed";
        ValidationResult result = ValidationResult.failure(errorMessage);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertEquals(1, result.getErrors().size());
        assertEquals(errorMessage, result.getFirstError());
        assertTrue(result.getErrors().contains(errorMessage));
    }
    
    @Test
    void testFailureWithMultipleErrors() {
        var errors = java.util.List.of("Error 1", "Error 2", "Error 3");
        ValidationResult result = ValidationResult.failure(errors);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertEquals(3, result.getErrors().size());
        assertEquals("Error 1", result.getFirstError());
        assertTrue(result.getErrors().containsAll(errors));
    }
    
    @Test
    void testWithWarnings() {
        var errors = java.util.List.of("Error 1");
        var warnings = java.util.List.of("Warning 1", "Warning 2");
        
        ValidationResult result = ValidationResult.withWarnings(false, errors, warnings);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertEquals(1, result.getErrors().size());
        assertEquals(2, result.getWarnings().size());
        assertEquals("Error 1", result.getFirstError());
        assertTrue(result.getWarnings().contains("Warning 1"));
        assertTrue(result.getWarnings().contains("Warning 2"));
    }
    
    @Test
    void testErrorHandling() {
        ValidationResult result = ValidationResult.success();
        
        // 测试空错误列表
        assertFalse(result.hasErrors());
        assertNull(result.getFirstError());
        
        // 添加错误
        var errors = java.util.List.of("New Error");
        result = ValidationResult.failure(errors);
        
        assertTrue(result.hasErrors());
        assertEquals("New Error", result.getFirstError());
    }
    
    @Test
    void testWarningHandling() {
        ValidationResult result = ValidationResult.success();
        
        // 测试空警告列表
        assertFalse(result.hasWarnings());
        
        // 添加警告
        var warnings = java.util.List.of("New Warning");
        result = ValidationResult.withWarnings(true, java.util.List.of(), warnings);
        
        assertTrue(result.hasWarnings());
        assertEquals(1, result.getWarnings().size());
        assertTrue(result.getWarnings().contains("New Warning"));
    }
    
    @Test
    void testImmutableLists() {
        var errors = java.util.List.of("Error 1");
        var warnings = java.util.List.of("Warning 1");
        
        ValidationResult result = ValidationResult.withWarnings(false, errors, warnings);
        
        // 验证返回的列表是不可修改的
        assertThrows(UnsupportedOperationException.class, () -> {
            result.getErrors().add("New Error");
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            result.getWarnings().add("New Warning");
        });
    }
}
