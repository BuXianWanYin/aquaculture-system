package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.FeedInventory;
import com.server.aquacultureserver.mapper.FeedInventoryMapper;
import com.server.aquacultureserver.service.FeedInventoryService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 饲料库存服务实现类
 */
@Service
public class FeedInventoryServiceImpl implements FeedInventoryService {
    
    @Autowired
    private FeedInventoryMapper inventoryMapper;
    
    @Override
    public List<FeedInventory> getAllInventories() {
        LambdaQueryWrapper<FeedInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedInventory::getStatus, 1);
        wrapper.orderByDesc(FeedInventory::getCreateTime);
        return inventoryMapper.selectList(wrapper);
    }
    
    @Override
    public FeedInventory getById(Long inventoryId) {
        return inventoryMapper.selectById(inventoryId);
    }
    
    @Override
    public Page<FeedInventory> getPage(Integer current, Integer size, String feedName, String feedType, Integer status) {
        Page<FeedInventory> page = new Page<>(current, size);
        LambdaQueryWrapper<FeedInventory> wrapper = new LambdaQueryWrapper<>();
        
        if (feedName != null && !feedName.isEmpty()) {
            wrapper.like(FeedInventory::getFeedName, feedName);
        }
        if (feedType != null && !feedType.isEmpty()) {
            wrapper.eq(FeedInventory::getFeedType, feedType);
        }
        if (status != null) {
            wrapper.eq(FeedInventory::getStatus, status);
        } else {
            wrapper.eq(FeedInventory::getStatus, 1);
        }
        
        wrapper.orderByDesc(FeedInventory::getCreateTime);
        return inventoryMapper.selectPage(page, wrapper);
    }
    
    @Override
    public boolean saveInventory(FeedInventory inventory) {
        // 新记录默认状态为正常
        if (inventory.getStatus() == null) {
            inventory.setStatus(1);
        }
        
        // 设置创建时间和创建人
        if (inventory.getCreateTime() == null) {
            inventory.setCreateTime(LocalDateTime.now());
        }
        if (inventory.getCreatorId() == null) {
            inventory.setCreatorId(UserContext.getCurrentUserId());
        }
        
        return inventoryMapper.insert(inventory) > 0;
    }
    
    @Override
    public boolean updateInventory(FeedInventory inventory) {
        return inventoryMapper.updateById(inventory) > 0;
    }
    
    @Override
    public boolean deleteInventory(Long inventoryId) {
        // 软删除，将状态设置为0
        FeedInventory inventory = getById(inventoryId);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }
        inventory.setStatus(0);
        return inventoryMapper.updateById(inventory) > 0;
    }
    
    @Override
    public FeedInventory getByFeedNameAndType(String feedName, String feedType) {
        LambdaQueryWrapper<FeedInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedInventory::getFeedName, feedName);
        wrapper.eq(FeedInventory::getFeedType, feedType);
        wrapper.eq(FeedInventory::getStatus, 1);
        return inventoryMapper.selectOne(wrapper);
    }
    
    @Override
    public long count() {
        LambdaQueryWrapper<FeedInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedInventory::getStatus, 1);
        return inventoryMapper.selectCount(wrapper);
    }
}

