package com.uros.kernel.handle.exception;

import com.uros.kernel.handle.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/** 全局异常处理器 统一处理应用中的异常 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /** 处理 IllegalArgumentException */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {

    return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
  }

  /** 处理通用异常 */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<String>> handleGenericException(
      Exception ex, WebRequest request) {

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("服务器内部错误: " + ex.getMessage()));
  }
}
