package com.uros.kernel.handle.config;

import com.uros.kernel.handle.KernelHandle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Handle 应用配置类
 */
@Configuration
public class HandleApplicationConfig {
    
    /**
     * 配置 KernelHandle Bean
     */
    @Bean
    public KernelHandle kernelHandle() {
        return new KernelHandle();
    }
}
