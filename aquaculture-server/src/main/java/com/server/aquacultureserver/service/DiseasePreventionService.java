package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.DiseasePrevention;

import java.util.List;

/**
 * 病害防治记录服务接口
 */
public interface DiseasePreventionService {
    
    /**
     * 查询所有防治记录
     */
    List<DiseasePrevention> getAllPreventions();
    
    /**
     * 根据ID查询防治记录
     */
    DiseasePrevention getById(Long preventionId);
    
    /**
     * 分页查询防治记录列表
     */
    Page<DiseasePrevention> getPage(Integer current, Integer size, Long recordId, Long planId, Long areaId, Integer status);
    
    /**
     * 根据病害记录ID查询防治记录列表
     */
    List<DiseasePrevention> getByRecordId(Long recordId);
    
    /**
     * 新增防治记录
     */
    boolean savePrevention(DiseasePrevention prevention);
    
    /**
     * 更新防治记录
     */
    boolean updatePrevention(DiseasePrevention prevention);
    
    /**
     * 删除防治记录
     */
    boolean deletePrevention(Long preventionId);
    
    /**
     * 统计防治记录总数
     */
    long count();
}

