package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.MedicineInventory;
import com.server.aquacultureserver.mapper.MedicineInventoryMapper;
import com.server.aquacultureserver.service.MedicineInventoryService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 药品库存服务实现类
 */
@Service
public class MedicineInventoryServiceImpl implements MedicineInventoryService {
    
    @Autowired
    private MedicineInventoryMapper inventoryMapper;
    
    /**
     * 查询所有有效的药品库存记录
     * @return 药品库存记录列表
     */
    @Override
    public List<MedicineInventory> getAllInventories() {
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        // 只查询状态为正常（1）的记录
        wrapper.eq(MedicineInventory::getStatus, 1);
        // 按创建时间倒序排列
        wrapper.orderByDesc(MedicineInventory::getCreateTime);
        return inventoryMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询药品库存记录
     * @param inventoryId 库存记录ID
     * @return 药品库存记录
     */
    @Override
    public MedicineInventory getById(Long inventoryId) {
        return inventoryMapper.selectById(inventoryId);
    }
    
    /**
     * 分页查询药品库存记录
     * @param current 当前页码
     * @param size 每页大小
     * @param medicineName 药品名称（模糊查询）
     * @param medicineType 药品类型（精确查询）
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
    @Override
    public Page<MedicineInventory> getPage(Integer current, Integer size, String medicineName, String medicineType, Integer status) {
        Page<MedicineInventory> page = new Page<>(current, size);
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        
        // 药品名称模糊查询
        if (medicineName != null && !medicineName.isEmpty()) {
            wrapper.like(MedicineInventory::getMedicineName, medicineName);
        }
        // 药品类型精确查询
        if (medicineType != null && !medicineType.isEmpty()) {
            wrapper.eq(MedicineInventory::getMedicineType, medicineType);
        }
        // 状态查询，如果未指定则默认查询正常状态
        if (status != null) {
            wrapper.eq(MedicineInventory::getStatus, status);
        } else {
            wrapper.eq(MedicineInventory::getStatus, 1);
        }
        
        // 按创建时间倒序排列
        wrapper.orderByDesc(MedicineInventory::getCreateTime);
        return inventoryMapper.selectPage(page, wrapper);
    }
    
    /**
     * 新增药品库存记录
     * 注意：库存通常由采购记录自动创建，不建议手动创建
     * @param inventory 药品库存记录
     * @return 是否成功
     */
    @Override
    public boolean saveInventory(MedicineInventory inventory) {
        // 设置默认状态为正常（1）
        if (inventory.getStatus() == null) {
            inventory.setStatus(1);
        }
        // 设置创建时间
        if (inventory.getCreateTime() == null) {
            inventory.setCreateTime(LocalDateTime.now());
        }
        // 设置创建人ID
        if (inventory.getCreatorId() == null) {
            inventory.setCreatorId(UserContext.getCurrentUserId());
        }
        return inventoryMapper.insert(inventory) > 0;
    }
    
    /**
     * 更新药品库存记录
     * @param inventory 药品库存记录
     * @return 是否成功
     */
    @Override
    public boolean updateInventory(MedicineInventory inventory) {
        return inventoryMapper.updateById(inventory) > 0;
    }
    
    /**
     * 删除药品库存记录（软删除）
     * @param inventoryId 库存记录ID
     * @return 是否成功
     */
    @Override
    public boolean deleteInventory(Long inventoryId) {
        MedicineInventory inventory = getById(inventoryId);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }
        // 软删除，将状态设为0
        inventory.setStatus(0);
        return inventoryMapper.updateById(inventory) > 0;
    }
    
    /**
     * 根据药品名称和类型查询库存记录
     * 用于采购和使用时查找对应的库存（库存按药品名称+类型聚合，不按批次号）
     * @param medicineName 药品名称
     * @param medicineType 药品类型
     * @return 药品库存记录
     */
    @Override
    public MedicineInventory getByMedicineNameAndType(String medicineName, String medicineType) {
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineInventory::getMedicineName, medicineName);
        wrapper.eq(MedicineInventory::getMedicineType, medicineType);
        wrapper.eq(MedicineInventory::getStatus, 1);
        return inventoryMapper.selectOne(wrapper);
    }
    
    /**
     * 统计有效的药品库存记录数量
     * @return 记录数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineInventory::getStatus, 1);
        return inventoryMapper.selectCount(wrapper);
    }
}

