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
        
        // 根据饲料名称和类型查找库存并减少库存（不按批次号）
        if (usage.getFeedName() != null && usage.getFeedType() != null && usage.getUsageAmount() != null) {
            FeedInventory inventory = inventoryService.getByFeedNameAndType(
                usage.getFeedName(), 
                usage.getFeedType()
            );
            if (inventory != null) {
                BigDecimal newStock = inventory.getCurrentStock().subtract(usage.getUsageAmount());
                if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("库存不足，当前库存：" + inventory.getCurrentStock() + "公斤");
                }
                inventory.setCurrentStock(newStock);
                inventory.setUpdateTime(LocalDateTime.now());
                inventoryService.updateInventory(inventory);
            } else {
                throw new RuntimeException("未找到对应的库存记录，请检查饲料名称和类型");
            }
        }
        
        return usageMapper.insert(usage) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateUsage(FeedUsage usage) {
        // 获取原始使用记录
        FeedUsage oldUsage = getById(usage.getUsageId());
        if (oldUsage == null) {
            throw new RuntimeException("使用记录不存在");
        }
        
        // 计算总成本
        if (usage.getUsageAmount() != null && usage.getUnitPrice() != null) {
            usage.setTotalCost(usage.getUsageAmount().multiply(usage.getUnitPrice()));
        }
        
        // 如果使用数量、饲料名称或类型发生变化，需要更新库存
        boolean needUpdateInventory = false;
        BigDecimal oldAmount = oldUsage.getUsageAmount() != null ? oldUsage.getUsageAmount() : BigDecimal.ZERO;
        BigDecimal newAmount = usage.getUsageAmount() != null ? usage.getUsageAmount() : BigDecimal.ZERO;
        
        String oldFeedName = oldUsage.getFeedName() == null ? "" : oldUsage.getFeedName();
        String newFeedName = usage.getFeedName() == null ? "" : usage.getFeedName();
        String oldFeedType = oldUsage.getFeedType() == null ? "" : oldUsage.getFeedType();
        String newFeedType = usage.getFeedType() == null ? "" : usage.getFeedType();
        
        if (!oldAmount.equals(newAmount) || 
            !oldFeedName.equals(newFeedName) ||
            !oldFeedType.equals(newFeedType)) {
            needUpdateInventory = true;
        }
        
        if (needUpdateInventory) {
            // 恢复旧记录的库存（按饲料名称+类型）
            if (oldUsage.getFeedName() != null && oldUsage.getFeedType() != null && oldUsage.getUsageAmount() != null) {
                FeedInventory oldInventory = inventoryService.getByFeedNameAndType(
                    oldUsage.getFeedName(), 
                    oldUsage.getFeedType()
                );
                if (oldInventory != null) {
                    BigDecimal restoredStock = oldInventory.getCurrentStock().add(oldAmount);
                    oldInventory.setCurrentStock(restoredStock);
                    oldInventory.setUpdateTime(LocalDateTime.now());
                    inventoryService.updateInventory(oldInventory);
                }
            }
            
            // 减少新记录的库存（按饲料名称+类型）
            if (usage.getFeedName() != null && usage.getFeedType() != null && usage.getUsageAmount() != null) {
                FeedInventory newInventory = inventoryService.getByFeedNameAndType(
                    usage.getFeedName(), 
                    usage.getFeedType()
                );
                if (newInventory != null) {
                    BigDecimal newStock = newInventory.getCurrentStock().subtract(newAmount);
                    if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                        throw new RuntimeException("库存不足，当前库存：" + newInventory.getCurrentStock() + "公斤");
                    }
                    newInventory.setCurrentStock(newStock);
                    newInventory.setUpdateTime(LocalDateTime.now());
                    inventoryService.updateInventory(newInventory);
                } else {
                    throw new RuntimeException("未找到对应的库存记录，请检查饲料名称和类型");
                }
            }
        }
        
        usage.setUpdateTime(LocalDateTime.now());
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
        // 软删除，将状态设置为0，并恢复库存
        FeedUsage usage = getById(usageId);
        if (usage == null) {
            throw new RuntimeException("使用记录不存在");
        }
        
        // 恢复库存（按饲料名称+类型）
        if (usage.getFeedName() != null && usage.getFeedType() != null && usage.getUsageAmount() != null && usage.getStatus() == 1) {
            FeedInventory inventory = inventoryService.getByFeedNameAndType(
                usage.getFeedName(), 
                usage.getFeedType()
            );
            if (inventory != null) {
                BigDecimal restoredStock = inventory.getCurrentStock().add(usage.getUsageAmount());
                inventory.setCurrentStock(restoredStock);
                inventory.setUpdateTime(LocalDateTime.now());
                inventoryService.updateInventory(inventory);
            }
        }
        
        usage.setStatus(0);
        usage.setUpdateTime(LocalDateTime.now());
        return usageMapper.updateById(usage) > 0;
    }
    
    @Override
    public long count() {
        LambdaQueryWrapper<FeedUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedUsage::getStatus, 1);
        return usageMapper.selectCount(wrapper);
    }
    
    @Override
    public BigDecimal calculateTotalCostByPlanId(Long planId) {
        if (planId == null) {
            return BigDecimal.ZERO;
        }
        LambdaQueryWrapper<FeedUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedUsage::getPlanId, planId)
               .eq(FeedUsage::getStatus, 1); // 只统计正常状态的记录
        
        List<FeedUsage> usages = usageMapper.selectList(wrapper);
        BigDecimal totalCost = BigDecimal.ZERO;
        for (FeedUsage usage : usages) {
            if (usage.getTotalCost() != null) {
                totalCost = totalCost.add(usage.getTotalCost());
            }
        }
        return totalCost;
    }
}

