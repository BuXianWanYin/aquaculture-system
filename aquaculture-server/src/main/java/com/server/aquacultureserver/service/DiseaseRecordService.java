package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.DiseaseRecord;

import java.util.List;

/**
 * 病害记录服务接口
 */
public interface DiseaseRecordService {
    
    /**
     * 查询所有病害记录
     */
    List<DiseaseRecord> getAllRecords();
    
    /**
     * 根据ID查询病害记录
     */
    DiseaseRecord getById(Long recordId);
    
    /**
     * 分页查询病害记录列表
     */
    Page<DiseaseRecord> getPage(Integer current, Integer size, Long planId, Long areaId, String diseaseName, String diseaseType, Integer status);
    
    /**
     * 新增病害记录
     */
    boolean saveRecord(DiseaseRecord record);
    
    /**
     * 更新病害记录
     */
    boolean updateRecord(DiseaseRecord record);
    
    /**
     * 删除病害记录
     */
    boolean deleteRecord(Long recordId);
    
    /**
     * 统计病害记录总数
     */
    long count();
    
    /**
     * 根据计划ID查询病害记录列表
     */
    List<DiseaseRecord> getByPlanId(Long planId);
}

