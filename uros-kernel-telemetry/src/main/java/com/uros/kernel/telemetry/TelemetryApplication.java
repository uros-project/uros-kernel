package com.uros.kernel.telemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 遥测服务应用程序入口
 */
@SpringBootApplication
@ComponentScan({"com.uros.kernel.telemetry", "com.uros.kernel.handle"})
public class TelemetryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelemetryApplication.class, args);
    }
}