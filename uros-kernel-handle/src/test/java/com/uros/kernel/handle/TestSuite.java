package com.uros.kernel.handle;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/** Handle 模块测试套件 运行所有测试类 */
@Suite
@SuiteDisplayName("Uros Kernel Handle 测试套件")
@SelectClasses({
  // 核心类测试
  ResourceTypeTest.class,
  ResourceInstanceTest.class,
  ResourceTypeRegistryTest.class,
  ResourceInstanceManagerTest.class,
  ResourceResolverTest.class,
  ResourceResolutionResultTest.class,
  ValidationResultTest.class,
  CompleteResourceInfoTest.class,

  // DTO 测试
  com.uros.kernel.handle.dto.ApiResponseTest.class,

  // 集成测试
  com.uros.kernel.handle.integration.HandleIntegrationTest.class,

  // 主服务测试
  KernelHandleTest.class
})
public class TestSuite {
  // 测试套件配置
}
