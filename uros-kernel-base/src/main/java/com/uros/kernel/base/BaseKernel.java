package com.uros.kernel.base;

/** 基础内核类 提供内核的基本功能 */
public class BaseKernel {

  /** 初始化内核 */
  public void initialize() {
    System.out.println("BaseKernel initialized");
  }

  /** 清理内核资源 */
  public void cleanup() {
    System.out.println("BaseKernel cleaned up");
  }
}
