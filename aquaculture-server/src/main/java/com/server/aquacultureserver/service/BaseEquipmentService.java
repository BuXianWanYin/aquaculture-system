package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.BaseEquipment;

import java.util.List;

/**
 * 设备信息服务接口
 */
public interface BaseEquipmentService {
    
    /**
     * 查询所有设备
     */
    List<BaseEquipment> getAllEquipments();
    
    /**
     * 根据ID查询设备
     */
    BaseEquipment getById(Long equipmentId);
    
    /**
     * 分页查询设备列表
     */
    Page<BaseEquipment> getPage(Integer current, Integer size, String equipmentName, Long areaId, String equipmentType);
    
    /**
     * 新增设备
     */
    boolean saveEquipment(BaseEquipment equipment);
    
    /**
     * 更新设备
     */
    boolean updateEquipment(BaseEquipment equipment);
    
    /**
     * 删除设备
     */
    boolean deleteEquipment(Long equipmentId);
}

