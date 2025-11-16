package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.FeedUsage;
import com.server.aquacultureserver.domain.FeedInventory;
import com.server.aquacultureserver.mapper.FeedUsageMapper;
import com.server.aquacultureserver.service.FeedUsageService;
import com.server.aquacultureserver.service.FeedInventoryService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 饲料使用记录服务实现类
 */
@Service
public class FeedUsageServiceImpl implements FeedUsageService {
    
    @Autowired
    private FeedUsageMapper usageMapper;
    
    @Autowired
    private FeedInventoryService inventoryService;
    
    @Override
    public List<FeedUsage> getAllUsages() {
        LambdaQueryWrapper<FeedUsage> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的使用记录
        if (UserContext.isOperator()) {
            Long areaId = UserContext.getCurrentUserAreaId();
            if (areaId != null) {
                wrapper.eq(FeedUsage::getAreaId, areaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能查看其部门下所有区域的使用记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(FeedUsage::getAreaId, areaIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.eq(FeedUsage::getStatus, 1);
        wrapper.orderByDesc(FeedUsage::getCreateTime);
        return usageMapper.selectList(wrapper);
    }
    
    @Override
    public FeedUsage getById(Long usageId) {
        return usageMapper.selectById(usageId);
    }
    
    @Override
    public Page<FeedUsage> getPage(Integer current, Integer size, Long planId, Long areaId, String feedName, String feedType, Integer status) {
        Page<FeedUsage> page = new Page<>(current, size);
        LambdaQueryWrapper<FeedUsage> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己区域的使用记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(FeedUsage::getAreaId, userAreaId);
            } else {
                return page;
            }
        }
        // 部门管理员只能查看其部门下所有区域的使用记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds != null && !areaIds.isEmpty()) {
                wrapper.in(FeedUsage::getAreaId, areaIds);
            } else {
                return page;
            }
        }
        
        if (planId != null) {
            wrapper.eq(FeedUsage::getPlanId, planId);
        }
        if (areaId != null) {
            wrapper.eq(FeedUsage::getAreaId, areaId);
        }
        if (feedName != null && !feedName.isEmpty()) {
            wrapper.like(FeedUsage::getFeedName, feedName);
        }
        if (feedType != null && !feedType.isEmpty()) {
            wrapper.eq(FeedUsage::getFeedType, feedType);
        }
        if (status != null) {
            wrapper.eq(FeedUsage::getStatus, status);
        } else {
            wrapper.eq(FeedUsage::getStatus, 1);
        }
        
        wrapper.orderByDesc(FeedUsage::getCreateTime);
        return usageMapper.selectPage(page, wrapper);
    }
    
    @Override
    @Transactional
    public boolean saveUsage(FeedUsage usage) {
        // 普通操作员只能创建自己区域的使用记录
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId == null) {
                throw new RuntimeException("您尚未分配区域，无法创建使用记录");
            }
            usage.setAreaId(userAreaId);
        }
        // 部门管理员只能创建其部门下区域的使用记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || areaIds.isEmpty()) {
                throw new RuntimeException("您尚未分配部门，无法创建使用记录");
            }
            if (usage.getAreaId() == null || !areaIds.contains(usage.getAreaId())) {
                throw new RuntimeException("您只能创建本部门下区域的使用记录");
            }
        }
        
        // 新记录默认状态为正常
        if (usage.getStatus() == null) {
            usage.setStatus(1);
        }
        
        // 设置创建时间和创建人
        if (usage.getCreateTime() == null) {
            usage.setCreateTime(LocalDateTime.now());
        }
        if (usage.getCreatorId() == null) {
            usage.setCreatorId(UserContext.getCurrentUserId());
        }
        
        // 计算总成本
        if (usage.getUsageAmount() != null && usage.getUnitPrice() != null) {
            usage.setTotalCost(usage.getUsageAmount().multiply(usage.getUnitPrice()));
        }
        
        // 如果有库存，更新库存数量
        if (usage.getFeedName() != null && usage.getFeedType() != null) {
            FeedInventory inventory = inventoryService.getByFeedNameAndType(usage.getFeedName(), usage.getFeedType());
            if (inventory != null && usage.getUsageAmount() != null) {
                BigDecimal newStock = inventory.getCurrentStock().subtract(usage.getUsageAmount());
                if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("库存不足，当前库存：" + inventory.getCurrentStock() + "公斤");
                }
                inventory.setCurrentStock(newStock);
                inventoryService.updateInventory(inventory);
            }
        }
        
        return usageMapper.insert(usage) > 0;
    }
    
    @Override
    public boolean updateUsage(FeedUsage usage) {
        // 计算总成本
        if (usage.getUsageAmount() != null && usage.getUnitPrice() != null) {
            usage.setTotalCost(usage.getUsageAmount().multiply(usage.getUnitPrice()));
        }
        return usageMapper.updateById(usage) > 0;
    }
    
    @Override
    public boolean deleteUsage(Long usageId) {
        // 普通操作员不能删除使用记录
        if (UserContext.isOperator()) {
            throw new RuntimeException("普通操作员无权删除使用记录");
        }
        // 部门管理员只能删除其部门下区域的使用记录
        if (UserContext.isDepartmentManager()) {
            FeedUsage usage = getById(usageId);
            if (usage == null) {
                throw new RuntimeException("使用记录不存在");
            }
            List<Long> areaIds = UserContext.getDepartmentManagerAreaIds();
            if (areaIds == null || !areaIds.contains(usage.getAreaId())) {
                throw new RuntimeException("您只能删除本部门下区域的使用记录");
            }
        }
        // 软删除，将状态设置为0
        FeedUsage usage = getById(usageId);
        if (usage == null) {
            throw new RuntimeException("使用记录不存在");
        }
        usage.setStatus(0);
        return usageMapper.updateById(usage) > 0;
    }
    
    @Override
    public long count() {
        LambdaQueryWrapper<FeedUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedUsage::getStatus, 1);
        return usageMapper.selectCount(wrapper);
    }
}

