package com.uros.kernel.handle.exception;

import com.uros.kernel.handle.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * 全局异常处理器
 * 统一处理应用中的异常并返回标准格式的API响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理IllegalArgumentException异常
     * 
     * @param ex 异常对象
     * @param request 请求对象
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logger.warn("IllegalArgumentException: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.badRequest(ex.getMessage());
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理ResourceNotFoundException异常
     * 
     * @param ex 异常对象
     * @param request 请求对象
     * @return 错误响应
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        logger.warn("ResourceNotFoundException: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.notFound(ex.getMessage());
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * 处理ResourceValidationException异常
     * 
     * @param ex 异常对象
     * @param request 请求对象
     * @return 错误响应
     */
    @ExceptionHandler(ResourceValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceValidationException(
            ResourceValidationException ex, WebRequest request) {
        logger.warn("ResourceValidationException: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.badRequest(ex.getMessage());
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理ResourceConflictException异常
     * 
     * @param ex 异常对象
     * @param request 请求对象
     * @return 错误响应
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceConflictException(
            ResourceConflictException ex, WebRequest request) {
        logger.warn("ResourceConflictException: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(409, "Conflict", ex.getMessage());
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    /**
     * 处理RuntimeException异常
     * 
     * @param ex 异常对象
     * @param request 请求对象
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        logger.error("RuntimeException: {}", ex.getMessage(), ex);
        
        ApiResponse<Void> response = ApiResponse.internalServerError("An unexpected error occurred");
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理所有其他异常
     * 
     * @param ex 异常对象
     * @param request 请求对象
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Unexpected exception: {}", ex.getMessage(), ex);
        
        ApiResponse<Void> response = ApiResponse.internalServerError("Internal server error");
        response.setPath(request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}