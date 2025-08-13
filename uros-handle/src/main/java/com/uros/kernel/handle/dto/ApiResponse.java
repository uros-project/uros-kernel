package com.uros.kernel.handle.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * 统一API响应格式
 * 
 * @param <T> 响应数据类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * 响应状态码
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 请求路径
     */
    private String path;
    
    /**
     * 错误详情（仅在出错时返回）
     */
    private String error;
    
    // 构造函数
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ApiResponse(int code, String message) {
        this();
        this.code = code;
        this.message = message;
    }
    
    public ApiResponse(int code, String message, T data) {
        this(code, message);
        this.data = data;
    }
    
    // 静态工厂方法
    
    /**
     * 成功响应
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }
    
    /**
     * 成功响应（无数据）
     * 
     * @return API响应
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "Success");
    }
    
    /**
     * 成功响应（自定义消息）
     * 
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }
    
    /**
     * 创建响应
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "Created", data);
    }
    
    /**
     * 无内容响应
     * 
     * @return API响应
     */
    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(204, "No Content");
    }
    
    /**
     * 错误请求响应
     * 
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        ApiResponse<T> response = new ApiResponse<>(400, "Bad Request");
        response.setError(message);
        return response;
    }
    
    /**
     * 未找到响应
     * 
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResponse<T> notFound(String message) {
        ApiResponse<T> response = new ApiResponse<>(404, "Not Found");
        response.setError(message);
        return response;
    }
    
    /**
     * 内部服务器错误响应
     * 
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResponse<T> internalServerError(String message) {
        ApiResponse<T> response = new ApiResponse<>(500, "Internal Server Error");
        response.setError(message);
        return response;
    }
    
    /**
     * 自定义错误响应
     * 
     * @param code 状态码
     * @param message 响应消息
     * @param error 错误详情
     * @return API响应
     */
    public static <T> ApiResponse<T> error(int code, String message, String error) {
        ApiResponse<T> response = new ApiResponse<>(code, message);
        response.setError(error);
        return response;
    }
    
    // Getter和Setter方法
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}