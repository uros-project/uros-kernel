package com.uros.kernel.telemetry.controller;

import com.uros.kernel.telemetry.service.TelemetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 遥测数据控制器，提供REST API接口
 */
@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    @Autowired
    private TelemetryService telemetryService;
    
    /**
     * 记录单个指标
     */
    @PostMapping("/record")
    public ResponseEntity<Map<String, Object>> recordMetric(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("metricName") String metricName,
            @RequestParam("value") double value) {
        
        boolean success = telemetryService.recordMetric(sourceId, metricName, value);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("sourceId", sourceId);
        response.put("metricName", metricName);
        response.put("value", value);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量记录指标
     */
    @PostMapping("/record-batch")
    public ResponseEntity<Map<String, Object>> recordMetrics(
            @RequestParam("sourceId") String sourceId,
            @RequestBody Map<String, Double> metrics) {
        
        int successCount = telemetryService.recordMetrics(sourceId, metrics);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", successCount > 0);
        response.put("sourceId", sourceId);
        response.put("totalMetrics", metrics.size());
        response.put("successCount", successCount);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取最新指标
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestMetric(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("metricName") String metricName) {
        
        Map<String, Object> metric = telemetryService.getLatestMetric(sourceId, metricName);
        
        if (metric.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(metric);
    }
    
    /**
     * 查询指定时间范围内的指标
     */
    @GetMapping("/query")
    public ResponseEntity<List<Map<String, Object>>> queryMetrics(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("metricName") String metricName,
            @RequestParam("startTime") long startTime,
            @RequestParam("endTime") long endTime) {
        
        List<Map<String, Object>> metrics = telemetryService.queryMetrics(sourceId, metricName, startTime, endTime);
        
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * 计算统计数据
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> calculateStatistics(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("metricName") String metricName) {
        
        Map<String, Object> statistics = telemetryService.calculateStatistics(sourceId, metricName);
        
        if (statistics.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 分析趋势
     */
    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> analyzeTrend(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("metricName") String metricName,
            @RequestParam(value = "period", defaultValue = "3600000") long period) {
        
        String trend = telemetryService.analyzeTrend(sourceId, metricName, period);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sourceId", sourceId);
        response.put("metricName", metricName);
        response.put("period", period);
        response.put("trend", trend);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 清除历史数据
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupHistory(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("metricName") String metricName,
            @RequestParam(value = "retentionPeriod", defaultValue = "86400000") long retentionPeriod) {
        
        int removedCount = telemetryService.cleanupHistory(sourceId, metricName, retentionPeriod);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sourceId", sourceId);
        response.put("metricName", metricName);
        response.put("retentionPeriod", retentionPeriod);
        response.put("removedCount", removedCount);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 清除所有历史数据
     */
    @DeleteMapping("/cleanup-all")
    public ResponseEntity<Map<String, Object>> cleanupAllHistory(
            @RequestParam(value = "retentionPeriod", defaultValue = "86400000") long retentionPeriod) {
        
        int totalRemoved = telemetryService.cleanupAllHistory(retentionPeriod);
        
        Map<String, Object> response = new HashMap<>();
        response.put("retentionPeriod", retentionPeriod);
        response.put("totalRemoved", totalRemoved);
        
        return ResponseEntity.ok(response);
    }
}