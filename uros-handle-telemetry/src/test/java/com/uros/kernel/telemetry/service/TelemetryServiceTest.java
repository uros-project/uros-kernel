package com.uros.kernel.telemetry.service;

import com.uros.kernel.telemetry.service.impl.TelemetryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 遥测服务测试类
 */
public class TelemetryServiceTest {

    private TelemetryService telemetryService;
    private final String sourceId = "test-source";
    private final String metricName = "cpu-usage";

    @BeforeEach
    public void setUp() {
        telemetryService = new TelemetryServiceImpl();
    }

    @Test
    public void testRecordMetric() {
        // 测试记录单个指标
        boolean result = telemetryService.recordMetric(sourceId, metricName, 75.5);
        assertTrue(result);

        // 验证记录的数据
        Map<String, Object> latestMetric = telemetryService.getLatestMetric(sourceId, metricName);
        assertNotNull(latestMetric);
        assertEquals(75.5, latestMetric.get("value"));
        assertEquals(sourceId, latestMetric.get("sourceId"));
        assertEquals(metricName, latestMetric.get("metricName"));
    }

    @Test
    public void testRecordMetrics() {
        // 测试批量记录指标
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("cpu-usage", 75.5);
        metrics.put("memory-usage", 60.2);
        metrics.put("disk-usage", 45.8);

        int count = telemetryService.recordMetrics(sourceId, metrics);
        assertEquals(3, count);

        // 验证记录的数据
        Map<String, Object> cpuMetric = telemetryService.getLatestMetric(sourceId, "cpu-usage");
        assertNotNull(cpuMetric);
        assertEquals(75.5, cpuMetric.get("value"));

        Map<String, Object> memoryMetric = telemetryService.getLatestMetric(sourceId, "memory-usage");
        assertNotNull(memoryMetric);
        assertEquals(60.2, memoryMetric.get("value"));

        Map<String, Object> diskMetric = telemetryService.getLatestMetric(sourceId, "disk-usage");
        assertNotNull(diskMetric);
        assertEquals(45.8, diskMetric.get("value"));
    }

    @Test
    public void testQueryMetrics() {
        // 记录一些测试数据
        long now = System.currentTimeMillis();
        telemetryService.recordMetric(sourceId, metricName, 10.0, now - 5000);
        telemetryService.recordMetric(sourceId, metricName, 20.0, now - 4000);
        telemetryService.recordMetric(sourceId, metricName, 30.0, now - 3000);
        telemetryService.recordMetric(sourceId, metricName, 40.0, now - 2000);
        telemetryService.recordMetric(sourceId, metricName, 50.0, now - 1000);

        // 查询指定时间范围内的数据
        List<Map<String, Object>> results = telemetryService.queryMetrics(sourceId, metricName, now - 4500, now - 1500);
        assertNotNull(results);
        assertEquals(3, results.size());

        // 验证查询结果
        assertEquals(20.0, results.get(0).get("value"));
        assertEquals(30.0, results.get(1).get("value"));
        assertEquals(40.0, results.get(2).get("value"));
    }

    @Test
    public void testCalculateStatistics() {
        // 记录一些测试数据
        telemetryService.recordMetric(sourceId, metricName, 10.0);
        telemetryService.recordMetric(sourceId, metricName, 20.0);
        telemetryService.recordMetric(sourceId, metricName, 30.0);
        telemetryService.recordMetric(sourceId, metricName, 40.0);
        telemetryService.recordMetric(sourceId, metricName, 50.0);

        // 计算统计数据
        Map<String, Object> stats = telemetryService.calculateStatistics(sourceId, metricName);
        assertNotNull(stats);

        // 验证统计结果
        assertEquals(5, stats.get("count"));
        assertEquals(10.0, stats.get("min"));
        assertEquals(50.0, stats.get("max"));
        assertEquals(150.0, stats.get("sum"));
        assertEquals(30.0, stats.get("average"));
    }

    @Test
    public void testAnalyzeTrend() {
        // 记录上升趋势的数据
        long now = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            telemetryService.recordMetric("rising-source", metricName, i * 0.5, now - (10 - i) * 1000);
        }

        // 记录下降趋势的数据
        for (int i = 0; i < 10; i++) {
            telemetryService.recordMetric("falling-source", metricName, (10 - i) * 0.5, now - (10 - i) * 1000);
        }

        // 记录稳定趋势的数据
        for (int i = 0; i < 10; i++) {
            telemetryService.recordMetric("stable-source", metricName, 5.0, now - (10 - i) * 1000);
        }

        // 分析趋势
        String risingTrend = telemetryService.analyzeTrend("rising-source", metricName, 15000);
        String fallingTrend = telemetryService.analyzeTrend("falling-source", metricName, 15000);
        String stableTrend = telemetryService.analyzeTrend("stable-source", metricName, 15000);

        // 验证趋势分析结果
        assertEquals("rising", risingTrend);
        assertEquals("falling", fallingTrend);
        assertEquals("stable", stableTrend);
    }

    @Test
    public void testCleanupHistory() {
        // 记录一些测试数据
        long now = System.currentTimeMillis();
        telemetryService.recordMetric(sourceId, metricName, 10.0, now - 10000);
        telemetryService.recordMetric(sourceId, metricName, 20.0, now - 8000);
        telemetryService.recordMetric(sourceId, metricName, 30.0, now - 6000);
        telemetryService.recordMetric(sourceId, metricName, 40.0, now - 4000);
        telemetryService.recordMetric(sourceId, metricName, 50.0, now - 2000);

        // 清除5秒前的历史数据
        int removedCount = telemetryService.cleanupHistory(sourceId, metricName, 5000);
        assertEquals(3, removedCount);

        // 验证剩余数据
        List<Map<String, Object>> results = telemetryService.queryMetrics(sourceId, metricName, 0, now);
        assertEquals(2, results.size());
        assertEquals(40.0, results.get(0).get("value"));
        assertEquals(50.0, results.get(1).get("value"));
    }
}