package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.StatisticResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计结果服务接口
 */
public interface StatisticResultService {
    
    /**
     * 分页查询统计结果列表
     */
    Page<StatisticResult> getPage(Integer current, Integer size, 
                                   String statisticName, String statisticDimension,
                                   String dimensionValue, LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据ID查询统计结果详情
     */
    StatisticResult getById(Long statisticId);
    
    /**
     * 保存统计结果
     */
    boolean saveStatistic(StatisticResult statistic);
    
    /**
     * 更新统计结果
     */
    boolean updateStatistic(StatisticResult statistic);
    
    /**
     * 删除统计结果
     */
    boolean deleteStatistic(Long statisticId);
    
    /**
     * 批量删除统计结果
     */
    boolean deleteBatch(Long[] statisticIds);
    
    /**
     * 获取月度产量趋势数据
     */
    List<Map<String, Object>> getMonthlyYieldTrend(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取品种产量占比数据
     */
    List<Map<String, Object>> getBreedYieldRatio(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取区域产量对比数据
     */
    List<Map<String, Object>> getAreaYieldComparison(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取计划完成情况统计
     */
    Map<String, Object> getPlanCompletionStats();
}

