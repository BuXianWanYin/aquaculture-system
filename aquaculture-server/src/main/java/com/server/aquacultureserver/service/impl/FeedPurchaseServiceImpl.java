package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.FeedPurchase;
import com.server.aquacultureserver.domain.FeedInventory;
import com.server.aquacultureserver.mapper.FeedPurchaseMapper;
import com.server.aquacultureserver.service.FeedPurchaseService;
import com.server.aquacultureserver.service.FeedInventoryService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 饲料采购服务实现类
 */
@Service
public class FeedPurchaseServiceImpl implements FeedPurchaseService {
    
    @Autowired
    private FeedPurchaseMapper purchaseMapper;
    
    @Autowired
    private FeedInventoryService inventoryService;
    
    /**
     * 查询所有有效的饲料采购记录
     * 采购和库存是公共资源，所有用户都可以查看
     * @return 采购记录列表
     */
    @Override
    public List<FeedPurchase> getAllPurchases() {
        LambdaQueryWrapper<FeedPurchase> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员和部门管理员都可以查看所有采购记录（采购和库存是公共资源）

        wrapper.eq(FeedPurchase::getStatus, 1);
        wrapper.orderByDesc(FeedPurchase::getCreateTime);
        return purchaseMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID查询采购记录
     * @param purchaseId 采购记录ID
     * @return 采购记录
     */
    @Override
    public FeedPurchase getById(Long purchaseId) {
        return purchaseMapper.selectById(purchaseId);
    }
    
    /**
     * 分页查询饲料采购记录
     * @param current 当前页码
     * @param size 每页大小
     * @param feedName 饲料名称（模糊查询）
     * @param feedType 饲料类型（精确查询）
     * @param supplier 供应商（模糊查询）
     * @param status 状态（1-正常，0-已删除）
     * @return 分页结果
     */
    @Override
    public Page<FeedPurchase> getPage(Integer current, Integer size, String feedName, String feedType, String supplier, Integer status) {
        Page<FeedPurchase> page = new Page<>(current, size);
        LambdaQueryWrapper<FeedPurchase> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员和部门管理员都可以查看所有采购记录（采购和库存是公共资源）
        
        if (feedName != null && !feedName.isEmpty()) {
            wrapper.like(FeedPurchase::getFeedName, feedName);
        }
        if (feedType != null && !feedType.isEmpty()) {
            wrapper.eq(FeedPurchase::getFeedType, feedType);
        }
        if (supplier != null && !supplier.isEmpty()) {
            wrapper.like(FeedPurchase::getSupplier, supplier);
        }
        if (status != null) {
            wrapper.eq(FeedPurchase::getStatus, status);
        } else {
            wrapper.eq(FeedPurchase::getStatus, 1);
        }
        
        wrapper.orderByDesc(FeedPurchase::getCreateTime);
        return purchaseMapper.selectPage(page, wrapper);
    }
    
    /**
     * 新增饲料采购记录
     * 新增采购后会自动更新饲料库存（如果为新饲料则创建库存，如果为已有饲料则累加库存）
     * @param purchase 采购记录
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean savePurchase(FeedPurchase purchase) {
        // 新记录默认状态为正常
        if (purchase.getStatus() == null) {
            purchase.setStatus(1);
        }
        
        // 设置创建时间和创建人
        if (purchase.getCreateTime() == null) {
            purchase.setCreateTime(LocalDateTime.now());
        }
        if (purchase.getCreatorId() == null) {
            purchase.setCreatorId(UserContext.getCurrentUserId());
        }
        
        // 计算总价 = 采购数量 × 单价
        if (purchase.getPurchaseAmount() != null && purchase.getUnitPrice() != null) {
            purchase.setTotalPrice(purchase.getPurchaseAmount().multiply(purchase.getUnitPrice()));
        }
        
        boolean result = purchaseMapper.insert(purchase) > 0;
        
        // 如果采购成功且状态为正常，则自动更新库存
        if (result && purchase.getStatus() == 1) {
            updateInventoryFromPurchase(purchase);
        }
        
        return result;
    }
    
    /**
     * 根据采购记录更新库存
     * 库存按饲料名称+类型聚合，不按批次号区分
     * 如果该饲料已有库存，则累加数量；如果为新饲料，则创建新库存记录
     * @param purchase 采购记录
     */
    private void updateInventoryFromPurchase(FeedPurchase purchase) {
        if (purchase.getFeedName() == null || purchase.getPurchaseAmount() == null) {
            return;
        }
        
        // 查找是否有相同的饲料名称和类型的库存（不按批次号）
        FeedInventory inventory = inventoryService.getByFeedNameAndType(
            purchase.getFeedName(), 
            purchase.getFeedType()
        );
        
        if (inventory != null) {
            // 如果存在，则累加库存
            inventory.setCurrentStock(inventory.getCurrentStock().add(purchase.getPurchaseAmount()));
            // 更新单价为最新采购的单价（或可以计算平均单价）
            inventory.setUnitPrice(purchase.getUnitPrice());
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryService.updateInventory(inventory);
        } else {
            // 如果不存在，则创建新库存
            FeedInventory newInventory = new FeedInventory();
            newInventory.setFeedName(purchase.getFeedName());
            newInventory.setFeedType(purchase.getFeedType());
            newInventory.setCurrentStock(purchase.getPurchaseAmount());
            newInventory.setUnitPrice(purchase.getUnitPrice());
            // 库存表不再存储批次号和保质期，因为同一饲料可能有多个批次
            newInventory.setBatchNumber(null);
            newInventory.setExpiryDate(null);
            newInventory.setStatus(1);
            newInventory.setCreatorId(purchase.getCreatorId());
            newInventory.setCreateTime(LocalDateTime.now());
            inventoryService.saveInventory(newInventory);
        }
    }
    
    /**
     * 更新饲料采购记录
     * 如果采购数量、饲料名称、类型或状态发生变化，会自动更新库存
     * @param purchase 采购记录
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean updatePurchase(FeedPurchase purchase) {
        // 获取原始采购记录
        FeedPurchase oldPurchase = getById(purchase.getPurchaseId());
        if (oldPurchase == null) {
            throw new RuntimeException("采购记录不存在");
        }
        
        // 计算总价 = 采购数量 × 单价
        if (purchase.getPurchaseAmount() != null && purchase.getUnitPrice() != null) {
            purchase.setTotalPrice(purchase.getPurchaseAmount().multiply(purchase.getUnitPrice()));
        }
        
        // 判断是否需要更新库存：只有采购数量、饲料名称、类型或状态发生变化时才更新
        boolean needUpdateInventory = false;
        boolean oldStatus = (oldPurchase.getStatus() != null && oldPurchase.getStatus() == 1);
        boolean newStatus = (purchase.getStatus() != null && purchase.getStatus() == 1);
        
        // 状态变化：从正常变为删除，或从删除变为正常
        if (oldStatus != newStatus) {
            needUpdateInventory = true;
        }
        
        // 采购数量变化
        if (oldPurchase.getPurchaseAmount() != null && purchase.getPurchaseAmount() != null) {
            if (oldPurchase.getPurchaseAmount().compareTo(purchase.getPurchaseAmount()) != 0) {
                needUpdateInventory = true;
            }
        }
        
        // 饲料名称或类型变化
        String oldFeedName = oldPurchase.getFeedName() == null ? "" : oldPurchase.getFeedName();
        String newFeedName = purchase.getFeedName() == null ? "" : purchase.getFeedName();
        String oldFeedType = oldPurchase.getFeedType() == null ? "" : oldPurchase.getFeedType();
        String newFeedType = purchase.getFeedType() == null ? "" : purchase.getFeedType();
        
        if (!oldFeedName.equals(newFeedName) || !oldFeedType.equals(newFeedType)) {
            needUpdateInventory = true;
        }
        
        boolean result = purchaseMapper.updateById(purchase) > 0;
        
        // 只有在需要更新库存时才执行库存更新逻辑
        if (result && needUpdateInventory) {
            // 如果原记录是正常状态，需要恢复库存
            if (oldStatus && oldPurchase.getFeedName() != null && oldPurchase.getPurchaseAmount() != null) {
                FeedInventory oldInventory = inventoryService.getByFeedNameAndType(oldFeedName, oldFeedType);
                if (oldInventory != null) {
                    // 先软删除当前采购记录（用于检查是否还有其他采购记录）
                    // 注意：这里只是临时检查，实际删除在下面处理
                    
                    // 检查该饲料是否还有其他采购记录（排除当前正在更新的这条）
                    LambdaQueryWrapper<FeedPurchase> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(FeedPurchase::getFeedName, oldFeedName);
                    wrapper.eq(FeedPurchase::getFeedType, oldFeedType);
                    wrapper.eq(FeedPurchase::getStatus, 1);
                    wrapper.ne(FeedPurchase::getPurchaseId, purchase.getPurchaseId());
                    long remainingPurchaseCount = purchaseMapper.selectCount(wrapper);
                    
                    if (remainingPurchaseCount == 0) {
                        // 如果该饲料没有其他采购记录了，删除库存记录
                        inventoryService.deleteInventory(oldInventory.getInventoryId());
                    } else {
                        // 如果还有其他采购记录，只更新库存（减少对应数量）
                        BigDecimal restoredStock = oldInventory.getCurrentStock().subtract(oldPurchase.getPurchaseAmount());
                        if (restoredStock.compareTo(BigDecimal.ZERO) < 0) {
                            restoredStock = BigDecimal.ZERO;
                        }
                        oldInventory.setCurrentStock(restoredStock);
                        oldInventory.setUpdateTime(LocalDateTime.now());
                        inventoryService.updateInventory(oldInventory);
                    }
                }
            }
            
            // 如果新记录是正常状态，需要增加库存
            if (newStatus) {
                updateInventoryFromPurchase(purchase);
            }
        }
        
        return result;
    }
    
    /**
     * 删除饲料采购记录（软删除）
     * 删除采购记录后会自动更新库存：
     * - 如果该饲料还有其他采购记录，只减少对应库存数量
     * - 如果该饲料的所有采购记录都被删除，则删除对应的库存记录
     * @param purchaseId 采购记录ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean deletePurchase(Long purchaseId) {
        // 软删除，将状态设置为0，并减少库存
        FeedPurchase purchase = getById(purchaseId);
        if (purchase == null) {
            throw new RuntimeException("采购记录不存在");
        }
        
        String feedName = purchase.getFeedName();
        String feedType = purchase.getFeedType();
        
        // 如果采购记录状态为正常，删除时需要减少库存
        if (purchase.getStatus() == 1 && feedName != null && purchase.getPurchaseAmount() != null) {
            FeedInventory inventory = inventoryService.getByFeedNameAndType(feedName, feedType);
            if (inventory != null) {
                // 先软删除采购记录
                purchase.setStatus(0);
                purchaseMapper.updateById(purchase);
                
                // 检查该饲料是否还有其他采购记录
                LambdaQueryWrapper<FeedPurchase> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(FeedPurchase::getFeedName, feedName);
                wrapper.eq(FeedPurchase::getFeedType, feedType);
                wrapper.eq(FeedPurchase::getStatus, 1);
                long remainingPurchaseCount = purchaseMapper.selectCount(wrapper);
                
                if (remainingPurchaseCount == 0) {
                    // 如果该饲料的所有采购记录都被删除了，删除库存记录
                    inventoryService.deleteInventory(inventory.getInventoryId());
                } else {
                    // 如果还有其他采购记录，只更新库存（减少对应数量）
                    BigDecimal newStock = inventory.getCurrentStock().subtract(purchase.getPurchaseAmount());
                    if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                        newStock = BigDecimal.ZERO; // 不允许负数，设为0
                    }
                    inventory.setCurrentStock(newStock);
                    inventory.setUpdateTime(LocalDateTime.now());
                    inventoryService.updateInventory(inventory);
                }
                
                return true;
            }
        }
        
        // 如果库存不存在或采购记录状态不是正常，只软删除采购记录
        purchase.setStatus(0);
        return purchaseMapper.updateById(purchase) > 0;
    }
    
    /**
     * 统计有效的饲料采购记录数量
     * @return 记录数量
     */
    @Override
    public long count() {
        LambdaQueryWrapper<FeedPurchase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedPurchase::getStatus, 1);
        return purchaseMapper.selectCount(wrapper);
    }
    
    /**
     * 根据饲料名称和类型查询采购记录
     * 用于饲料使用中选择批次号时获取该饲料的所有采购记录
     * @param feedName 饲料名称
     * @param feedType 饲料类型
     * @return 采购记录列表（按采购日期倒序）
     */
    @Override
    public List<FeedPurchase> getByFeedNameAndType(String feedName, String feedType) {
        LambdaQueryWrapper<FeedPurchase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedPurchase::getFeedName, feedName);
        wrapper.eq(FeedPurchase::getFeedType, feedType);
        wrapper.eq(FeedPurchase::getStatus, 1);
        wrapper.orderByDesc(FeedPurchase::getPurchaseDate);
        return purchaseMapper.selectList(wrapper);
    }
}

