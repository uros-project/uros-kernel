#!/bin/bash

echo "启动 Uros Kernel Handle 服务..."

# 检查 Java 版本
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
echo "Java 版本: $java_version"

# 检查 Maven 版本
maven_version=$(mvn -version 2>&1 | head -n 1)
echo "Maven 版本: $maven_version"

echo ""
echo "正在编译项目..."
mvn clean compile -q

if [ $? -eq 0 ]; then
    echo "编译成功！"
    echo ""
    echo "正在启动服务..."
    echo "服务地址: http://localhost:8081/handle"
    echo "API 文档: http://localhost:8081/handle/swagger-ui.html"
    echo "健康检查: http://localhost:8081/handle/health"
    echo ""
    echo "按 Ctrl+C 停止服务"
    echo ""
    
    # 启动 Spring Boot 应用
    mvn spring-boot:run -pl uros-kernel-handle
else
    echo "编译失败，请检查错误信息"
    exit 1
fi
