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
    
    @Override
    public List<MedicineInventory> getAllInventories() {
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineInventory::getStatus, 1);
        wrapper.orderByDesc(MedicineInventory::getCreateTime);
        return inventoryMapper.selectList(wrapper);
    }
    
    @Override
    public MedicineInventory getById(Long inventoryId) {
        return inventoryMapper.selectById(inventoryId);
    }
    
    @Override
    public Page<MedicineInventory> getPage(Integer current, Integer size, String medicineName, String medicineType, Integer status) {
        Page<MedicineInventory> page = new Page<>(current, size);
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        
        if (medicineName != null && !medicineName.isEmpty()) {
            wrapper.like(MedicineInventory::getMedicineName, medicineName);
        }
        if (medicineType != null && !medicineType.isEmpty()) {
            wrapper.eq(MedicineInventory::getMedicineType, medicineType);
        }
        if (status != null) {
            wrapper.eq(MedicineInventory::getStatus, status);
        } else {
            wrapper.eq(MedicineInventory::getStatus, 1);
        }
        
        wrapper.orderByDesc(MedicineInventory::getCreateTime);
        return inventoryMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveInventory(MedicineInventory inventory) {
        if (inventory.getStatus() == null) {
            inventory.setStatus(1);
        }
        if (inventory.getCreateTime() == null) {
            inventory.setCreateTime(LocalDateTime.now());
        }
        if (inventory.getCreatorId() == null) {
            inventory.setCreatorId(UserContext.getCurrentUserId());
        }
        return inventoryMapper.insert(inventory) > 0;
    }
    
    @Override
    public boolean updateInventory(MedicineInventory inventory) {
        return inventoryMapper.updateById(inventory) > 0;
    }
    
    @Override
    public boolean deleteInventory(Long inventoryId) {
        MedicineInventory inventory = getById(inventoryId);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }
        inventory.setStatus(0);
        return inventoryMapper.updateById(inventory) > 0;
    }
    
    @Override
    public MedicineInventory getByMedicineNameAndType(String medicineName, String medicineType) {
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineInventory::getMedicineName, medicineName);
        wrapper.eq(MedicineInventory::getMedicineType, medicineType);
        wrapper.eq(MedicineInventory::getStatus, 1);
        return inventoryMapper.selectOne(wrapper);
    }
    
    @Override
    public long count() {
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineInventory::getStatus, 1);
        return inventoryMapper.selectCount(wrapper);
    }
}

