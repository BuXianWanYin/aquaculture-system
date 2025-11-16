package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.YieldStatistics;

import java.util.List;

/**
 * 产量统计服务接口
 */
public interface YieldStatisticsService {
    
    /**
     * 查询所有产量统计
     */
    List<YieldStatistics> getAllStatistics();
    
    /**
     * 根据ID查询产量统计
     */
    YieldStatistics getById(Long yieldId);
    
    /**
     * 分页查询产量统计列表
     */
    Page<YieldStatistics> getPage(Integer current, Integer size, Long planId, Long areaId, Long breedId, Integer status);
    
    /**
     * 新增产量统计
     */
    boolean saveStatistics(YieldStatistics statistics);
    
    /**
     * 更新产量统计
     */
    boolean updateStatistics(YieldStatistics statistics);
    
    /**
     * 删除产量统计
     */
    boolean deleteStatistics(Long yieldId);
    
    /**
     * 审核产量统计
     */
    boolean auditStatistics(Long yieldId, Long auditorId, String auditOpinion, Integer status);
    
    /**
     * 统计产量记录总数
     */
    long count();
}

