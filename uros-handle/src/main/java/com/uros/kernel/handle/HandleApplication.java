package com.uros.kernel.handle;

import com.uros.kernel.handle.model.Resolvable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

/**
 * UROS Handle Application
 * Spring Boot主应用类
 */
@SpringBootApplication
public class HandleApplication {

    public static void main(String[] args) {
        SpringApplication.run(HandleApplication.class, args);
    }
    
    /**
     * Handle应用实例组件
     * 继承Resolvable以获得统一的标识符管理能力
     */
    @Component
    public static class HandleInstance extends Resolvable implements CommandLineRunner {
        
        @Value("${handle.id}")
        private String handleId;
        
        @Override
        public void run(String... args) throws Exception {
            // 在应用启动时设置自己的ID
            this.setId(handleId);
            System.out.println("Handle应用已启动，ID: " + this.getId());
        }
    }
}