package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.FeedUsage;

import java.util.List;

/**
 * 饲料使用记录服务接口
 */
public interface FeedUsageService {
    
    /**
     * 查询所有使用记录
     */
    List<FeedUsage> getAllUsages();
    
    /**
     * 根据ID查询使用记录
     */
    FeedUsage getById(Long usageId);
    
    /**
     * 分页查询使用记录列表
     */
    Page<FeedUsage> getPage(Integer current, Integer size, Long planId, Long areaId, String feedName, String feedType, Integer status);
    
    /**
     * 新增使用记录
     */
    boolean saveUsage(FeedUsage usage);
    
    /**
     * 更新使用记录
     */
    boolean updateUsage(FeedUsage usage);
    
    /**
     * 删除使用记录
     */
    boolean deleteUsage(Long usageId);
    
    /**
     * 统计使用记录总数
     */
    long count();
}

