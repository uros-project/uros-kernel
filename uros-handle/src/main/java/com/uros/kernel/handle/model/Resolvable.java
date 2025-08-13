package com.uros.kernel.handle.model;

/**
 * 可解析对象的抽象基类
 * 提供统一的标识符管理
 */
public abstract class Resolvable {
    
    /**
     * 唯一标识符
     */
    protected String id;
    
    /**
     * 获取唯一标识符
     * 
     * @return 唯一标识符
     */
    public String getId() {
        return id;
    }
    
    /**
     * 设置唯一标识符
     * 
     * @param id 唯一标识符
     */
    public void setId(String id) {
        this.id = id;
    }
}