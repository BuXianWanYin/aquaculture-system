package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseArea;

import java.util.List;

/**
 * 养殖区域服务接口
 */
public interface BaseAreaService {
    
    /**
     * 查询所有区域
     */
    List<BaseArea> getAllAreas();
    
    /**
     * 根据ID查询区域
     */
    BaseArea getById(Long areaId);
    
    /**
     * 分页查询区域列表
     */
    Page<BaseArea> getPage(Integer current, Integer size, String areaName, Long departmentId, Integer status);
    
    /**
     * 新增区域
     */
    boolean saveArea(BaseArea area);
    
    /**
     * 更新区域
     */
    boolean updateArea(BaseArea area);
    
    /**
     * 删除区域
     */
    boolean deleteArea(Long areaId);
}

