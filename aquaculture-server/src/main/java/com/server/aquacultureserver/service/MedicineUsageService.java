package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.MedicineUsage;

import java.util.List;

/**
 * 用药记录服务接口
 */
public interface MedicineUsageService {
    
    /**
     * 查询所有用药记录
     */
    List<MedicineUsage> getAllUsages();
    
    /**
     * 根据ID查询用药记录
     */
    MedicineUsage getById(Long usageId);
    
    /**
     * 分页查询用药记录列表
     */
    Page<MedicineUsage> getPage(Integer current, Integer size, Long recordId, Long preventionId, Long planId, Long areaId, String medicineName, Integer status);
    
    /**
     * 根据病害记录ID查询用药记录列表
     */
    List<MedicineUsage> getByRecordId(Long recordId);
    
    /**
     * 根据防治记录ID查询用药记录列表
     */
    List<MedicineUsage> getByPreventionId(Long preventionId);
    
    /**
     * 新增用药记录
     */
    boolean saveUsage(MedicineUsage usage);
    
    /**
     * 更新用药记录
     */
    boolean updateUsage(MedicineUsage usage);
    
    /**
     * 删除用药记录
     */
    boolean deleteUsage(Long usageId);
    
    /**
     * 统计用药记录总数
     */
    long count();
}

