package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.DailyInspection;

import java.util.List;

/**
 * 日常检查记录服务接口
 */
public interface DailyInspectionService {
    
    /**
     * 查询所有检查记录
     */
    List<DailyInspection> getAllInspections();
    
    /**
     * 根据ID查询检查记录
     */
    DailyInspection getById(Long inspectionId);
    
    /**
     * 分页查询检查记录列表
     */
    Page<DailyInspection> getPage(Integer current, Integer size, Long planId, Long areaId, String inspectionType, String inspectionResult, Integer status);
    
    /**
     * 新增检查记录
     */
    boolean saveInspection(DailyInspection inspection);
    
    /**
     * 更新检查记录
     */
    boolean updateInspection(DailyInspection inspection);
    
    /**
     * 删除检查记录
     */
    boolean deleteInspection(Long inspectionId);
    
    /**
     * 统计检查记录总数
     */
    long count();
}

