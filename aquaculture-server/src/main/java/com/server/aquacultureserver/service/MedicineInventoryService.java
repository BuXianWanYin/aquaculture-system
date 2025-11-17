package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.MedicineInventory;

import java.util.List;

/**
 * 药品库存服务接口
 */
public interface MedicineInventoryService {
    
    /**
     * 查询所有库存记录
     */
    List<MedicineInventory> getAllInventories();
    
    /**
     * 根据ID查询库存记录
     */
    MedicineInventory getById(Long inventoryId);
    
    /**
     * 分页查询库存记录列表
     */
    Page<MedicineInventory> getPage(Integer current, Integer size, String medicineName, String medicineType, Integer status);
    
    /**
     * 新增库存记录
     */
    boolean saveInventory(MedicineInventory inventory);
    
    /**
     * 更新库存记录
     */
    boolean updateInventory(MedicineInventory inventory);
    
    /**
     * 删除库存记录
     */
    boolean deleteInventory(Long inventoryId);
    
    /**
     * 根据药品名称和类型查询库存
     */
    MedicineInventory getByMedicineNameAndType(String medicineName, String medicineType);
    
    /**
     * 统计库存记录总数
     */
    long count();
}

