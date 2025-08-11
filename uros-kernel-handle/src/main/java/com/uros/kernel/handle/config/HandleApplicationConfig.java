package com.uros.kernel.handle.config;

import com.uros.kernel.handle.HandleKernel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Handle 应用配置类
 */
@Configuration
public class HandleApplicationConfig {
    
    /**
     * 配置 HandleKernel Bean
     */
    @Bean
    public HandleKernel kernelHandle() {
        return new HandleKernel();
    }
}
