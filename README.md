# uros-kernel

这是一个多模块的 Spring Boot 项目，包含基础内核与资源处理两个模块。

## 模块说明

- `uros-kernel-base`: 基础内核模块，提供内核基础能力与 `UrosKernelApplication`（测试用）。
- `uros-kernel-handle`: 资源处理模块，主类为 `HandleKernel`（SpringBootApplication），提供资源类型与实例的管理与 REST 接口。

## 环境要求

- JDK 21（CI 使用 Temurin 21）
- Maven 3.9+

## 快速开始

- 全量构建与测试

```bash
mvn clean test
```

- 启动资源处理模块（Handle）

```bash
# 在项目根目录运行 Handle 模块（会自动构建依赖）
mvn spring-boot:run -pl uros-kernel-handle -am
```

- 直接运行主类
  - 在 IDE 中运行 `com.uros.kernel.handle.HandleKernel#main`

## 常用命令

```bash
# 仅格式检查（Spotless）
mvn spotless:check

# 自动格式化
mvn spotless:apply

# 仅构建 handle 模块
mvn -pl uros-kernel-handle -am package
```

## CI

仓库包含 GitHub Actions 工作流，在 push/PR 到 `main` 时自动执行：
- `spotless:check`
- `mvn test`

## 目录结构（简化）

```
uros-kernel/
├── uros-kernel-base/
│   └── src/main/java/com/uros/kernel/base/
│       ├── BaseKernel.java
│       └── UrosKernelApplication.java
├── uros-kernel-handle/
│   └── src/main/java/com/uros/kernel/handle/
│       ├── HandleKernel.java        # SpringBootApplication 入口
│       ├── controller/
│       ├── config/
│       └── ...
└── .github/workflows/ci.yml
```
