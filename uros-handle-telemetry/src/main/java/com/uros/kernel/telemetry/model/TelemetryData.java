package com.uros.kernel.telemetry.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 遥测数据模型，用于存储和管理遥测数据
 */
public class TelemetryData implements Serializable {

    /** 当前值 */
    private double currentValue;
    
    /** 时间戳（毫秒） */
    private long timestamp;
    
    /** 历史数据点 */
    private List<DataPoint> history;
    
    /** 统计信息 */
    private Statistics statistics;
    
    /** 数据单位 */
    private String unit;
    
    /** 采样频率（毫秒） */
    private long sampleRate;
    
    /** 读写锁，保证线程安全 */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * 默认构造函数
     */
    public TelemetryData() {
        this.history = new ArrayList<>();
        this.statistics = new Statistics();
        this.unit = "";
        this.sampleRate = 1000; // 默认1秒
    }
    
    /**
     * 带参数的构造函数
     * 
     * @param currentValue 当前值
     * @param timestamp 时间戳
     * @param unit 单位
     * @param sampleRate 采样频率
     */
    public TelemetryData(double currentValue, long timestamp, String unit, long sampleRate) {
        this.currentValue = currentValue;
        this.timestamp = timestamp;
        this.history = new ArrayList<>();
        this.statistics = new Statistics();
        this.unit = unit;
        this.sampleRate = sampleRate;
        
        // 添加第一个数据点
        addDataPoint(currentValue, timestamp);
    }
    
    /**
     * 添加数据点
     * 
     * @param value 数据值
     * @param timestamp 时间戳
     */
    public void addDataPoint(double value, long timestamp) {
        try {
            lock.writeLock().lock();
            DataPoint dataPoint = new DataPoint(value, timestamp);
            history.add(dataPoint);
            updateStatistics();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 更新当前值和时间戳
     * 
     * @param value 新的数据值
     * @param timestamp 新的时间戳
     */
    public void updateCurrentValue(double value, long timestamp) {
        try {
            lock.writeLock().lock();
            this.currentValue = value;
            this.timestamp = timestamp;
            addDataPoint(value, timestamp);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 更新统计信息
     */
    private void updateStatistics() {
        if (history.isEmpty()) {
            return;
        }
        
        double sum = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (DataPoint point : history) {
            double value = point.getValue();
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        
        double avg = sum / history.size();
        
        // 计算标准差
        double sumSquaredDiff = 0;
        for (DataPoint point : history) {
            double diff = point.getValue() - avg;
            sumSquaredDiff += diff * diff;
        }
        double stdDev = Math.sqrt(sumSquaredDiff / history.size());
        
        statistics.setAvg(avg);
        statistics.setMin(min);
        statistics.setMax(max);
        statistics.setStdDev(stdDev);
        statistics.setCount(history.size());
    }
    
    /**
     * 分析趋势
     * 
     * @param period 分析周期（毫秒）
     * @return 趋势类型："rising"（上升）, "falling"（下降）, "stable"（稳定）
     */
    public String analyzeTrend(long period) {
        try {
            lock.readLock().lock();
            if (history.size() < 2) {
                return "stable";
            }
            
            long currentTime = System.currentTimeMillis();
            long startTime = currentTime - period;
            
            List<DataPoint> periodData = new ArrayList<>();
            for (DataPoint point : history) {
                if (point.getTimestamp() >= startTime && point.getTimestamp() <= currentTime) {
                    periodData.add(point);
                }
            }
            
            if (periodData.size() < 2) {
                return "stable";
            }
            
            // 简单线性回归分析趋势
            double sumX = 0;
            double sumY = 0;
            double sumXY = 0;
            double sumXX = 0;
            
            for (int i = 0; i < periodData.size(); i++) {
                double x = i;
                double y = periodData.get(i).getValue();
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumXX += x * x;
            }
            
            int n = periodData.size();
            double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
            
            // 判断趋势
            double threshold = 0.001; // 趋势判断阈值
            if (Math.abs(slope) < threshold) {
                return "stable";
            } else if (slope > 0) {
                return "rising";
            } else {
                return "falling";
            }
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 清除超过指定时间的历史数据
     * 
     * @param retentionPeriod 保留周期（毫秒）
     * @return 清除的数据点数量
     */
    public int cleanupHistory(long retentionPeriod) {
        try {
            lock.writeLock().lock();
            long cutoffTime = System.currentTimeMillis() - retentionPeriod;
            int initialSize = history.size();
            
            history.removeIf(point -> point.getTimestamp() < cutoffTime);
            
            // 更新统计信息
            updateStatistics();
            
            return initialSize - history.size();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 限制历史数据点的最大数量
     * 
     * @param maxSize 最大数量
     * @return 清除的数据点数量
     */
    public int limitHistorySize(int maxSize) {
        try {
            lock.writeLock().lock();
            if (history.size() <= maxSize) {
                return 0;
            }
            
            int removeCount = history.size() - maxSize;
            history = new ArrayList<>(history.subList(removeCount, history.size()));
            
            // 更新统计信息
            updateStatistics();
            
            return removeCount;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // Getter 和 Setter 方法
    
    public double getCurrentValue() {
        try {
            lock.readLock().lock();
            return currentValue;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public long getTimestamp() {
        try {
            lock.readLock().lock();
            return timestamp;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public List<DataPoint> getHistory() {
        try {
            lock.readLock().lock();
            return new ArrayList<>(history);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public Statistics getStatistics() {
        try {
            lock.readLock().lock();
            return statistics;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public long getSampleRate() {
        return sampleRate;
    }
    
    public void setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
    }
    
    /**
     * 数据点内部类
     */
    public static class DataPoint implements Serializable {
        private double value;
        private long timestamp;
        
        public DataPoint(double value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
        
        public double getValue() {
            return value;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
    
    /**
     * 统计信息内部类
     */
    public static class Statistics implements Serializable {
        private double avg;
        private double min;
        private double max;
        private double stdDev;
        private int count;
        
        public Statistics() {
            this.avg = 0;
            this.min = 0;
            this.max = 0;
            this.stdDev = 0;
            this.count = 0;
        }
        
        public double getAvg() {
            return avg;
        }
        
        public void setAvg(double avg) {
            this.avg = avg;
        }
        
        public double getMin() {
            return min;
        }
        
        public void setMin(double min) {
            this.min = min;
        }
        
        public double getMax() {
            return max;
        }
        
        public void setMax(double max) {
            this.max = max;
        }
        
        public double getStdDev() {
            return stdDev;
        }
        
        public void setStdDev(double stdDev) {
            this.stdDev = stdDev;
        }
        
        public int getCount() {
            return count;
        }
        
        public void setCount(int count) {
            this.count = count;
        }
    }
}