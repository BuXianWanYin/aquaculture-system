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
    
    /**
     * 查询所有有效的饲料库存记录
     * 采购和库存是公共资源，所有用户都可以查看
     * @return 库存记录列表
     */
    @Override
    public List<FeedInventory> getAllInventories() {
        LambdaQueryWrapper<FeedInventory> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员和部门管理员都可以查看所有库存记录（采购和库存是公共资源）
        
        wrapper.eq(FeedInventory::getStatus, 1);
        wrapper.orderByDesc(FeedInventory::getCreateTime);
        return inventoryMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询库存记录
     * @param inventoryId 库存记录ID
     * @return 库存记录
     */
    @Override
    public FeedInventory getById(Long inventoryId) {
        return inventoryMapper.selectById(inventoryId);
    }
    
    /**
     * 分页查询饲料库存记录
     * @param current 当前页码
     * @param size 每页大小
     * @param feedName 饲料名称（模糊查询）
     * @param feedType 饲料类型（精确查询）
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
    @Override
    public Page<FeedInventory> getPage(Integer current, Integer size, String feedName, String feedType, Integer status) {
        Page<FeedInventory> page = new Page<>(current, size);
        LambdaQueryWrapper<FeedInventory> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员和部门管理员都可以查看所有库存记录（采购和库存是公共资源）
        
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
    
    /**
     * 新增饲料库存记录
     * 注意：库存通常由采购记录自动创建，不建议手动创建
     * @param inventory 库存记录
     * @return 是否成功
     */
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
    
    /**
     * 更新饲料库存记录
     * @param inventory 库存记录
     * @return 是否成功
     */
    @Override
    public boolean updateInventory(FeedInventory inventory) {
        return inventoryMapper.updateById(inventory) > 0;
    }
    
    /**
     * 删除饲料库存记录（软删除）
     * @param inventoryId 库存记录ID
     * @return 是否成功
     */
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
    
    /**
     * 根据饲料名称和类型查询库存记录
     * 用于采购和使用时查找对应的库存（库存按饲料名称+类型聚合，不按批次号）
     * @param feedName 饲料名称
     * @param feedType 饲料类型
     * @return 库存记录
     */
    @Override
    public FeedInventory getByFeedNameAndType(String feedName, String feedType) {
        LambdaQueryWrapper<FeedInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedInventory::getFeedName, feedName);
        wrapper.eq(FeedInventory::getFeedType, feedType);
        wrapper.eq(FeedInventory::getStatus, 1);
        return inventoryMapper.selectOne(wrapper);
    }
    
    /**
     * 根据饲料名称和批次号查询库存记录
     * @param feedName 饲料名称
     * @param batchNumber 批次号
     * @return 库存记录
     */
    @Override
    public FeedInventory getByFeedNameAndBatchNumber(String feedName, String batchNumber) {
        LambdaQueryWrapper<FeedInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedInventory::getFeedName, feedName);
        if (batchNumber != null && !batchNumber.isEmpty()) {
            wrapper.eq(FeedInventory::getBatchNumber, batchNumber);
        } else {
            wrapper.and(w -> w.isNull(FeedInventory::getBatchNumber).or().eq(FeedInventory::getBatchNumber, ""));
        }
        wrapper.eq(FeedInventory::getStatus, 1);
        return inventoryMapper.selectOne(wrapper);
    }
    
    /**
     * 统计有效的饲料库存记录数量
     * @return 记录数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<FeedInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedInventory::getStatus, 1);
        return inventoryMapper.selectCount(wrapper);
    }
}

