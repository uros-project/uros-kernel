package com.uros.kernel.handle.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ApiResponse 类的测试
 */
public class ApiResponseTest {
    
    @Test
    void testDefaultConstructor() {
        ApiResponse<String> response = new ApiResponse<>();
        
        assertNotNull(response.getTimestamp());
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getData());
    }
    
    @Test
    void testParameterizedConstructor() {
        String data = "test data";
        ApiResponse<String> response = new ApiResponse<>(true, "Success message", data);
        
        assertTrue(response.isSuccess());
        assertEquals("Success message", response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
    }
    
    @Test
    void testSuccessStaticMethod() {
        String data = "success data";
        ApiResponse<String> response = ApiResponse.success(data);
        
        assertTrue(response.isSuccess());
        assertEquals("操作成功", response.getMessage());
        assertEquals(data, response.getData());
    }
    
    @Test
    void testSuccessWithMessageStaticMethod() {
        String data = "success data";
        String message = "Custom success message";
        ApiResponse<String> response = ApiResponse.success(message, data);
        
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
    }
    
    @Test
    void testErrorStaticMethod() {
        String errorMessage = "Error occurred";
        ApiResponse<String> response = ApiResponse.error(errorMessage);
        
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
    }
    
    @Test
    void testErrorWithDataStaticMethod() {
        String errorMessage = "Error occurred";
        String errorData = "error details";
        ApiResponse<String> response = ApiResponse.error(errorMessage, errorData);
        
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertEquals(errorData, response.getData());
    }
    
    @Test
    void testSetters() {
        ApiResponse<String> response = new ApiResponse<>();
        
        response.setSuccess(true);
        response.setMessage("New message");
        response.setData("New data");
        
        assertTrue(response.isSuccess());
        assertEquals("New message", response.getMessage());
        assertEquals("New data", response.getData());
    }
}
