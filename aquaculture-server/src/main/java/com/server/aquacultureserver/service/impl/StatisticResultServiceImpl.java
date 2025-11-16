package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.AquaculturePlan;
import com.server.aquacultureserver.domain.StatisticResult;
import com.server.aquacultureserver.domain.YieldStatistics;
import com.server.aquacultureserver.mapper.AquaculturePlanMapper;
import com.server.aquacultureserver.mapper.StatisticResultMapper;
import com.server.aquacultureserver.mapper.YieldStatisticsMapper;
import com.server.aquacultureserver.service.StatisticResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计结果服务实现类
 */
@Service
public class StatisticResultServiceImpl implements StatisticResultService {
    
    @Autowired
    private StatisticResultMapper statisticMapper;
    
    @Autowired
    private YieldStatisticsMapper yieldStatisticsMapper;
    
    @Autowired
    private AquaculturePlanMapper planMapper;
    
    @Override
    public Page<StatisticResult> getPage(Integer current, Integer size, 
                                         String statisticName, String statisticDimension,
                                         String dimensionValue, LocalDate startDate, LocalDate endDate) {
        Page<StatisticResult> page = new Page<>(current, size);
        LambdaQueryWrapper<StatisticResult> wrapper = new LambdaQueryWrapper<>();
        
        if (statisticName != null && !statisticName.isEmpty()) {
            wrapper.like(StatisticResult::getStatisticName, statisticName);
        }
        if (statisticDimension != null && !statisticDimension.isEmpty()) {
            wrapper.eq(StatisticResult::getStatisticDimension, statisticDimension);
        }
        if (dimensionValue != null && !dimensionValue.isEmpty()) {
            wrapper.eq(StatisticResult::getDimensionValue, dimensionValue);
        }
        if (startDate != null) {
            wrapper.ge(StatisticResult::getStartDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(StatisticResult::getEndDate, endDate);
        }
        
        wrapper.orderByDesc(StatisticResult::getCalculateTime);
        return statisticMapper.selectPage(page, wrapper);
    }
    
    @Override
    public StatisticResult getById(Long statisticId) {
        return statisticMapper.selectById(statisticId);
    }
    
    @Override
    public boolean saveStatistic(StatisticResult statistic) {
        if (statistic.getCalculateTime() == null) {
            statistic.setCalculateTime(LocalDateTime.now());
        }
        return statisticMapper.insert(statistic) > 0;
    }
    
    @Override
    public boolean updateStatistic(StatisticResult statistic) {
        statistic.setUpdateTime(LocalDateTime.now());
        return statisticMapper.updateById(statistic) > 0;
    }
    
    @Override
    public boolean deleteStatistic(Long statisticId) {
        return statisticMapper.deleteById(statisticId) > 0;
    }
    
    @Override
    public boolean deleteBatch(Long[] statisticIds) {
        for (Long statisticId : statisticIds) {
            statisticMapper.deleteById(statisticId);
        }
        return true;
    }
    
    @Override
    public List<Map<String, Object>> getMonthlyYieldTrend(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 查询产量统计数据
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YieldStatistics::getStatus, 1); // 已通过审核的产量
        if (startDate != null) {
            wrapper.ge(YieldStatistics::getStatisticsDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(YieldStatistics::getStatisticsDate, endDate);
        }
        
        List<YieldStatistics> statistics = yieldStatisticsMapper.selectList(wrapper);
        
        // 按月份分组统计
        Map<String, BigDecimal> monthlyData = new HashMap<>();
        for (YieldStatistics stat : statistics) {
            if (stat.getStatisticsDate() != null && stat.getActualYield() != null) {
                String monthKey = stat.getStatisticsDate().toString().substring(0, 7); // YYYY-MM
                monthlyData.put(monthKey, 
                    monthlyData.getOrDefault(monthKey, BigDecimal.ZERO).add(stat.getActualYield()));
            }
        }
        
        // 转换为列表格式
        for (Map.Entry<String, BigDecimal> entry : monthlyData.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", entry.getKey());
            item.put("yield", entry.getValue());
            result.add(item);
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getBreedYieldRatio(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 查询产量统计数据
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YieldStatistics::getStatus, 1); // 已通过审核的产量
        if (startDate != null) {
            wrapper.ge(YieldStatistics::getStatisticsDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(YieldStatistics::getStatisticsDate, endDate);
        }
        
        List<YieldStatistics> statistics = yieldStatisticsMapper.selectList(wrapper);
        
        // 按品种分组统计（这里简化处理，实际应该关联品种表）
        Map<Long, BigDecimal> breedData = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        
        for (YieldStatistics stat : statistics) {
            if (stat.getBreedId() != null && stat.getActualYield() != null) {
                breedData.put(stat.getBreedId(), 
                    breedData.getOrDefault(stat.getBreedId(), BigDecimal.ZERO).add(stat.getActualYield()));
                total = total.add(stat.getActualYield());
            }
        }
        
        // 转换为列表格式
        for (Map.Entry<Long, BigDecimal> entry : breedData.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("breedId", entry.getKey());
            item.put("yield", entry.getValue());
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                item.put("ratio", entry.getValue().divide(total, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100)).doubleValue());
            } else {
                item.put("ratio", 0.0);
            }
            result.add(item);
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getAreaYieldComparison(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 查询产量统计数据
        LambdaQueryWrapper<YieldStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YieldStatistics::getStatus, 1); // 已通过审核的产量
        if (startDate != null) {
            wrapper.ge(YieldStatistics::getStatisticsDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(YieldStatistics::getStatisticsDate, endDate);
        }
        
        List<YieldStatistics> statistics = yieldStatisticsMapper.selectList(wrapper);
        
        // 按区域分组统计
        Map<Long, BigDecimal> areaData = new HashMap<>();
        
        for (YieldStatistics stat : statistics) {
            if (stat.getAreaId() != null && stat.getActualYield() != null) {
                areaData.put(stat.getAreaId(), 
                    areaData.getOrDefault(stat.getAreaId(), BigDecimal.ZERO).add(stat.getActualYield()));
            }
        }
        
        // 转换为列表格式
        for (Map.Entry<Long, BigDecimal> entry : areaData.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("areaId", entry.getKey());
            item.put("yield", entry.getValue());
            result.add(item);
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getPlanCompletionStats() {
        Map<String, Object> result = new HashMap<>();
        
        // 查询所有计划
        List<AquaculturePlan> allPlans = planMapper.selectList(null);
        
        long totalCount = allPlans.size();
        long completedCount = 0;
        long inProgressCount = 0;
        long pendingCount = 0;
        
        for (AquaculturePlan plan : allPlans) {
            if (plan.getStatus() != null) {
                if (plan.getStatus() == 4) { // 已完成
                    completedCount++;
                } else if (plan.getStatus() == 3) { // 执行中
                    inProgressCount++;
                } else if (plan.getStatus() == 0) { // 待审批
                    pendingCount++;
                }
            }
        }
        
        result.put("totalCount", totalCount);
        result.put("completedCount", completedCount);
        result.put("inProgressCount", inProgressCount);
        result.put("pendingCount", pendingCount);
        
        if (totalCount > 0) {
            result.put("completionRate", (double) completedCount / totalCount * 100);
        } else {
            result.put("completionRate", 0.0);
        }
        
        return result;
    }
}

