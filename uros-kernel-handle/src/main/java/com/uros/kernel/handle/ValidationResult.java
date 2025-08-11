package com.uros.kernel.handle;

import java.util.List;
import java.util.ArrayList;

/**
 * 验证结果
 * 封装资源验证的结果信息
 */
public class ValidationResult {
    
    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;
    
    private ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
        this.valid = valid;
        this.errors = errors != null ? errors : new ArrayList<>();
        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }
    
    /**
     * 创建验证成功的结果
     */
    public static ValidationResult success() {
        return new ValidationResult(true, new ArrayList<>(), new ArrayList<>());
    }
    
    /**
     * 创建验证失败的结果
     */
    public static ValidationResult failure(String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ValidationResult(false, errors, new ArrayList<>());
    }
    
    /**
     * 创建验证失败的结果（多个错误）
     */
    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, errors, new ArrayList<>());
    }
    
    /**
     * 创建带警告的结果
     */
    public static ValidationResult withWarnings(boolean valid, List<String> errors, List<String> warnings) {
        return new ValidationResult(valid, errors, warnings);
    }
    
    // Getters
    public boolean isValid() { return valid; }
    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
    
    /**
     * 获取第一个错误信息
     */
    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }
    
    /**
     * 检查是否有错误
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * 检查是否有警告
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}
