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
    
    /**
     * 查询所有有效的饲料使用记录
     * 根据用户角色进行权限过滤
     * @return 使用记录列表
     */
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
    
    /**
     * 根据ID查询使用记录
     * @param usageId 使用记录ID
     * @return 使用记录
     */
    @Override
    public FeedUsage getById(Long usageId) {
        return usageMapper.selectById(usageId);
    }
    
    /**
     * 分页查询饲料使用记录
     * 根据用户角色进行权限过滤
     * @param current 当前页码
     * @param size 每页大小
     * @param planId 养殖计划ID
     * @param areaId 区域ID
     * @param feedName 饲料名称（模糊查询）
     * @param feedType 饲料类型（精确查询）
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
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
    
    /**
     * 新增饲料使用记录
     * 新增使用后会自动从饲料库存中扣减对应数量
     * 根据用户角色进行权限控制
     * @param usage 使用记录
     * @return 是否成功
     */
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
        
        // 计算总成本 = 使用数量 × 单价
        if (usage.getUsageAmount() != null && usage.getUnitPrice() != null) {
            usage.setTotalCost(usage.getUsageAmount().multiply(usage.getUnitPrice()));
        }
        
        // 根据饲料名称和类型查找库存并自动扣减库存（不按批次号）
        if (usage.getFeedName() != null && usage.getFeedType() != null && usage.getUsageAmount() != null) {
            FeedInventory inventory = inventoryService.getByFeedNameAndType(
                usage.getFeedName(), 
                usage.getFeedType()
            );
            if (inventory != null) {
                // 计算新库存 = 当前库存 - 使用数量
                BigDecimal newStock = inventory.getCurrentStock().subtract(usage.getUsageAmount());
                // 检查库存是否充足
                if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("库存不足，当前库存：" + inventory.getCurrentStock() + "公斤");
                }
                // 更新库存
                inventory.setCurrentStock(newStock);
                inventory.setUpdateTime(LocalDateTime.now());
                inventoryService.updateInventory(inventory);
            } else {
                throw new RuntimeException("未找到对应的库存记录，请检查饲料名称和类型");
            }
        }
        
        // 保存使用记录
        return usageMapper.insert(usage) > 0;
    }
    
    /**
     * 更新饲料使用记录
     * 如果使用数量、饲料名称或类型发生变化，会自动更新库存：
     * - 先恢复旧记录的库存数量
     * - 再扣减新记录的库存数量
     * @param usage 使用记录
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean updateUsage(FeedUsage usage) {
        // 获取原始使用记录
        FeedUsage oldUsage = getById(usage.getUsageId());
        if (oldUsage == null) {
            throw new RuntimeException("使用记录不存在");
        }
        
        // 计算总成本 = 使用数量 × 单价
        if (usage.getUsageAmount() != null && usage.getUnitPrice() != null) {
            usage.setTotalCost(usage.getUsageAmount().multiply(usage.getUnitPrice()));
        }
        
        // 判断是否需要更新库存（如果使用数量、饲料名称或类型发生变化，则需要更新库存）
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
            // 先恢复旧记录的库存（按饲料名称+类型）
            if (oldUsage.getFeedName() != null && oldUsage.getFeedType() != null && oldUsage.getUsageAmount() != null) {
                FeedInventory oldInventory = inventoryService.getByFeedNameAndType(
                    oldUsage.getFeedName(), 
                    oldUsage.getFeedType()
                );
                if (oldInventory != null) {
                    // 恢复库存 = 当前库存 + 旧使用数量
                    BigDecimal restoredStock = oldInventory.getCurrentStock().add(oldAmount);
                    oldInventory.setCurrentStock(restoredStock);
                    oldInventory.setUpdateTime(LocalDateTime.now());
                    inventoryService.updateInventory(oldInventory);
                }
            }
            
            // 再扣减新记录的库存（按饲料名称+类型）
            if (usage.getFeedName() != null && usage.getFeedType() != null && usage.getUsageAmount() != null) {
                FeedInventory newInventory = inventoryService.getByFeedNameAndType(
                    usage.getFeedName(), 
                    usage.getFeedType()
                );
                if (newInventory != null) {
                    // 计算新库存 = 当前库存 - 新使用数量
                    BigDecimal newStock = newInventory.getCurrentStock().subtract(newAmount);
                    // 检查库存是否充足
                    if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                        throw new RuntimeException("库存不足，当前库存：" + newInventory.getCurrentStock() + "公斤");
                    }
                    // 更新库存
                    newInventory.setCurrentStock(newStock);
                    newInventory.setUpdateTime(LocalDateTime.now());
                    inventoryService.updateInventory(newInventory);
                } else {
                    throw new RuntimeException("未找到对应的库存记录，请检查饲料名称和类型");
                }
            }
        }
        
        // 更新使用记录
        usage.setUpdateTime(LocalDateTime.now());
        return usageMapper.updateById(usage) > 0;
    }
    
    /**
     * 删除饲料使用记录（软删除）
     * 删除使用记录后会自动恢复对应饲料的库存数量
     * 根据用户角色进行权限控制
     * @param usageId 使用记录ID
     * @return 是否成功
     */
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
        
        // 恢复库存（按饲料名称+类型），只有正常状态的记录才需要恢复库存
        if (usage.getFeedName() != null && usage.getFeedType() != null && usage.getUsageAmount() != null && usage.getStatus() == 1) {
            FeedInventory inventory = inventoryService.getByFeedNameAndType(
                usage.getFeedName(), 
                usage.getFeedType()
            );
            if (inventory != null) {
                // 恢复库存 = 当前库存 + 使用数量
                BigDecimal restoredStock = inventory.getCurrentStock().add(usage.getUsageAmount());
                inventory.setCurrentStock(restoredStock);
                inventory.setUpdateTime(LocalDateTime.now());
                inventoryService.updateInventory(inventory);
            }
        }
        
        // 软删除，将状态设为0
        usage.setStatus(0);
        usage.setUpdateTime(LocalDateTime.now());
        return usageMapper.updateById(usage) > 0;
    }
    
    /**
     * 统计有效的饲料使用记录数量
     * @return 记录数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<FeedUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedUsage::getStatus, 1);
        return usageMapper.selectCount(wrapper);
    }
    
    /**
     * 根据养殖计划ID计算饲料使用总成本
     * @param planId 养殖计划ID
     * @return 总成本
     */
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

