package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.FeedingRecord;

import java.util.List;

/**
 * 投喂记录服务接口
 */
public interface FeedingRecordService {
    
    /**
     * 查询所有投喂记录
     */
    List<FeedingRecord> getAllRecords();
    
    /**
     * 根据ID查询投喂记录
     */
    FeedingRecord getById(Long recordId);
    
    /**
     * 分页查询投喂记录列表
     */
    Page<FeedingRecord> getPage(Integer current, Integer size, Long planId, Long areaId, String feedName, Integer status);
    
    /**
     * 新增投喂记录
     */
    boolean saveRecord(FeedingRecord record);
    
    /**
     * 更新投喂记录
     */
    boolean updateRecord(FeedingRecord record);
    
    /**
     * 删除投喂记录
     */
    boolean deleteRecord(Long recordId);
    
    /**
     * 统计投喂记录总数
     */
    long count();
}

