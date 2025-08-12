package com.uros.kernel.telemetry.service.impl;

import com.uros.kernel.telemetry.model.TelemetryData;
import com.uros.kernel.telemetry.service.TelemetryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 遥测服务实现类
 */
@Service
public class TelemetryServiceImpl implements TelemetryService {

    private static final Logger logger = LoggerFactory.getLogger(TelemetryServiceImpl.class);
    
    // 存储所有遥测数据，key为sourceId:metricName
    private final Map<String, TelemetryData> telemetryDataMap = new ConcurrentHashMap<>();
    
    // 生成数据存储的key
    private String generateKey(String sourceId, String metricName) {
        return sourceId + ":" + metricName;
    }
    
    // 获取或创建遥测数据对象
    private TelemetryData getOrCreateTelemetryData(String sourceId, String metricName) {
        String key = generateKey(sourceId, metricName);
        return telemetryDataMap.computeIfAbsent(key, k -> new TelemetryData());
    }

    @Override
    public boolean recordMetric(String sourceId, String metricName, double value) {
        return recordMetric(sourceId, metricName, value, System.currentTimeMillis());
    }

    @Override
    public boolean recordMetric(String sourceId, String metricName, double value, long timestamp) {
        try {
            TelemetryData data = getOrCreateTelemetryData(sourceId, metricName);
            data.addDataPoint(value, timestamp);
            return true;
        } catch (Exception e) {
            logger.error("Failed to record metric: {}.{} = {} @ {}", sourceId, metricName, value, timestamp, e);
            return false;
        }
    }

    @Override
    public int recordMetrics(String sourceId, Map<String, Double> metrics) {
        return recordMetrics(sourceId, metrics, System.currentTimeMillis());
    }

    @Override
    public int recordMetrics(String sourceId, Map<String, Double> metrics, long timestamp) {
        int successCount = 0;
        for (Map.Entry<String, Double> entry : metrics.entrySet()) {
            if (recordMetric(sourceId, entry.getKey(), entry.getValue(), timestamp)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public Map<String, Object> getLatestMetric(String sourceId, String metricName) {
        String key = generateKey(sourceId, metricName);
        TelemetryData data = telemetryDataMap.get(key);
        
        if (data == null) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("sourceId", sourceId);
        result.put("metricName", metricName);
        result.put("value", data.getCurrentValue());
        result.put("timestamp", data.getTimestamp());
        return result;
    }

    @Override
    public List<Map<String, Object>> queryMetrics(String sourceId, String metricName, long startTime, long endTime) {
        String key = generateKey(sourceId, metricName);
        TelemetryData data = telemetryDataMap.get(key);
        
        if (data == null) {
            return Collections.emptyList();
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        List<TelemetryData.DataPoint> dataPoints = data.getHistory();
        
        for (TelemetryData.DataPoint point : dataPoints) {
            if (point.getTimestamp() >= startTime && point.getTimestamp() <= endTime) {
                Map<String, Object> pointMap = new HashMap<>();
                pointMap.put("sourceId", sourceId);
                pointMap.put("metricName", metricName);
                pointMap.put("value", point.getValue());
                pointMap.put("timestamp", point.getTimestamp());
                result.add(pointMap);
            }
        }
        
        return result;
    }

    @Override
    public Map<String, Object> calculateStatistics(String sourceId, String metricName) {
        String key = generateKey(sourceId, metricName);
        TelemetryData data = telemetryDataMap.get(key);
        
        if (data == null) {
            return Collections.emptyMap();
        }
        
        TelemetryData.Statistics stats = data.getStatistics();
        Map<String, Object> result = new HashMap<>();
        result.put("sourceId", sourceId);
        result.put("metricName", metricName);
        result.put("count", stats.getCount());
        result.put("min", stats.getMin());
        result.put("max", stats.getMax());
        result.put("sum", 0); // TelemetryData.Statistics没有sum方法
        result.put("average", stats.getAvg());
        result.put("standardDeviation", stats.getStdDev());
        result.put("lastUpdated", 0); // TelemetryData.Statistics没有lastUpdated方法
        
        return result;
    }

    @Override
    public String analyzeTrend(String sourceId, String metricName, long period) {
        String key = generateKey(sourceId, metricName);
        TelemetryData data = telemetryDataMap.get(key);
        
        if (data == null || data.getHistory().size() < 2) {
            return "stable";
        }
        
        long currentTime = System.currentTimeMillis();
        long cutoffTime = currentTime - period;
        
        List<TelemetryData.DataPoint> relevantPoints = new ArrayList<>();
        for (TelemetryData.DataPoint point : data.getHistory()) {
            if (point.getTimestamp() >= cutoffTime) {
                relevantPoints.add(point);
            }
        }
        
        if (relevantPoints.size() < 2) {
            return "stable";
        }
        
        // 使用简单线性回归计算趋势
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;
        int n = relevantPoints.size();
        
        for (int i = 0; i < n; i++) {
            double x = relevantPoints.get(i).getTimestamp() - cutoffTime;
            double y = relevantPoints.get(i).getValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        
        // 判断趋势
        double threshold = 0.001; // 趋势判断阈值
        if (slope > threshold) {
            return "rising";
        } else if (slope < -threshold) {
            return "falling";
        } else {
            return "stable";
        }
    }

    @Override
    public int cleanupHistory(String sourceId, String metricName, long retentionPeriod) {
        String key = generateKey(sourceId, metricName);
        TelemetryData data = telemetryDataMap.get(key);
        
        if (data == null) {
            return 0;
        }
        
        long cutoffTime = System.currentTimeMillis() - retentionPeriod;
        return data.cleanupHistory(cutoffTime);
    }

    @Override
    public int cleanupAllHistory(long retentionPeriod) {
        int totalRemoved = 0;
        long cutoffTime = System.currentTimeMillis() - retentionPeriod;
        
        for (Map.Entry<String, TelemetryData> entry : telemetryDataMap.entrySet()) {
            totalRemoved += entry.getValue().cleanupHistory(cutoffTime);
        }
        
        return totalRemoved;
    }
}