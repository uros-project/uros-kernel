package com.uros.kernel.handle.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger API 文档配置 */
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Uros Kernel Handle API")
                .description("资源类型管理的 REST API 接口文档")
                .version("1.0.0")
                .contact(
                    new Contact()
                        .name("Uros Team")
                        .email("support@uros.com")
                        .url("https://uros.com"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server().url("http://localhost:8081/handle").description("本地开发环境"),
                new Server().url("https://api.uros.com/handle").description("生产环境")));
  }
}
