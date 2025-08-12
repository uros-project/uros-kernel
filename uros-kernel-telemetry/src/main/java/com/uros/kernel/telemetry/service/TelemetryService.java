package com.uros.kernel.telemetry.service;

import java.util.List;
import java.util.Map;

/**
 * 遥测服务接口，定义遥测数据的操作方法
 */
public interface TelemetryService {
    
    /**
     * 记录单个指标
     * 
     * @param sourceId 数据源ID
     * @param metricName 指标名称
     * @param value 指标值
     * @return 是否成功
     */
    boolean recordMetric(String sourceId, String metricName, double value);
    
    /**
     * 记录单个指标（带时间戳）
     * 
     * @param sourceId 数据源ID
     * @param metricName 指标名称
     * @param value 指标值
     * @param timestamp 时间戳
     * @return 是否成功
     */
    boolean recordMetric(String sourceId, String metricName, double value, long timestamp);
    
    /**
     * 批量记录指标
     * 
     * @param sourceId 数据源ID
     * @param metrics 指标集合，key为指标名称，value为指标值
     * @return 成功记录的指标数量
     */
    int recordMetrics(String sourceId, Map<String, Double> metrics);
    
    /**
     * 批量记录指标（带时间戳）
     * 
     * @param sourceId 数据源ID
     * @param metrics 指标集合，key为指标名称，value为指标值
     * @param timestamp 时间戳
     * @return 成功记录的指标数量
     */
    int recordMetrics(String sourceId, Map<String, Double> metrics, long timestamp);
    
    /**
     * 获取最新指标
     * 
     * @param sourceId 数据源ID
     * @param metricName 指标名称
     * @return 指标数据，包含当前值和时间戳
     */
    Map<String, Object> getLatestMetric(String sourceId, String metricName);
    
    /**
     * 查询指定时间范围内的指标
     * 
     * @param sourceId 数据源ID
     * @param metricName 指标名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标数据列表
     */
    List<Map<String, Object>> queryMetrics(String sourceId, String metricName, long startTime, long endTime);
    
    /**
     * 计算统计数据
     * 
     * @param sourceId 数据源ID
     * @param metricName 指标名称
     * @return 统计数据，包含平均值、最大值、最小值等
     */
    Map<String, Object> calculateStatistics(String sourceId, String metricName);
    
    /**
     * 分析趋势
     * 
     * @param sourceId 数据源ID
     * @param metricName 指标名称
     * @param period 分析周期（毫秒）
     * @return 趋势类型："rising"（上升）, "falling"（下降）, "stable"（稳定）
     */
    String analyzeTrend(String sourceId, String metricName, long period);
    
    /**
     * 清除历史数据
     * 
     * @param sourceId 数据源ID
     * @param metricName 指标名称
     * @param retentionPeriod 保留周期（毫秒）
     * @return 清除的数据点数量
     */
    int cleanupHistory(String sourceId, String metricName, long retentionPeriod);
    
    /**
     * 清除所有历史数据
     * 
     * @param retentionPeriod 保留周期（毫秒）
     * @return 清除的数据点总数
     */
    int cleanupAllHistory(long retentionPeriod);
}