package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SalesRecord;

import java.util.List;
import java.util.Map;

/**
 * 销售记录服务接口
 */
public interface SalesRecordService {
    
    /**
     * 查询所有销售记录
     */
    List<SalesRecord> getAllRecords();
    
    /**
     * 根据ID查询销售记录
     */
    SalesRecord getById(Long salesId);
    
    /**
     * 分页查询销售记录列表
     */
    Page<SalesRecord> getPage(Integer current, Integer size, Long planId, Long areaId, Long breedId, Long customerId, Integer paymentStatus, Integer status);
    
    /**
     * 新增销售记录
     */
    boolean saveRecord(SalesRecord record);
    
    /**
     * 更新销售记录
     */
    boolean updateRecord(SalesRecord record);
    
    /**
     * 删除销售记录
     */
    boolean deleteRecord(Long salesId);
    
    /**
     * 统计销售记录总数
     */
    long count();
    
    /**
     * 统计销售收入（按时间段）
     */
    Map<String, Object> getSalesStatistics(String startDate, String endDate, Long areaId, Long breedId);
}

