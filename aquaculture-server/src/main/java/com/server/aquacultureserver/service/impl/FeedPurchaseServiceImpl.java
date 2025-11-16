package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.FeedPurchase;
import com.server.aquacultureserver.mapper.FeedPurchaseMapper;
import com.server.aquacultureserver.service.FeedPurchaseService;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 饲料采购服务实现类
 */
@Service
public class FeedPurchaseServiceImpl implements FeedPurchaseService {
    
    @Autowired
    private FeedPurchaseMapper purchaseMapper;
    
    @Override
    public List<FeedPurchase> getAllPurchases() {
        LambdaQueryWrapper<FeedPurchase> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己创建的采购记录
        if (UserContext.isOperator()) {
            Long userId = UserContext.getCurrentUserId();
            if (userId != null) {
                wrapper.eq(FeedPurchase::getCreatorId, userId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员可以查看其部门下所有用户创建的采购记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> userIds = UserContext.getDepartmentManagerUserIds();
            if (userIds != null && !userIds.isEmpty()) {
                wrapper.in(FeedPurchase::getCreatorId, userIds);
            } else {
                return Collections.emptyList();
            }
        }
        
        wrapper.eq(FeedPurchase::getStatus, 1);
        wrapper.orderByDesc(FeedPurchase::getCreateTime);
        return purchaseMapper.selectList(wrapper);
    }
    
    @Override
    public FeedPurchase getById(Long purchaseId) {
        return purchaseMapper.selectById(purchaseId);
    }
    
    @Override
    public Page<FeedPurchase> getPage(Integer current, Integer size, String feedName, String feedType, String supplier, Integer status) {
        Page<FeedPurchase> page = new Page<>(current, size);
        LambdaQueryWrapper<FeedPurchase> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己创建的采购记录
        if (UserContext.isOperator()) {
            Long userId = UserContext.getCurrentUserId();
            if (userId != null) {
                wrapper.eq(FeedPurchase::getCreatorId, userId);
            } else {
                return page;
            }
        }
        // 部门管理员可以查看其部门下所有用户创建的采购记录
        else if (UserContext.isDepartmentManager()) {
            List<Long> userIds = UserContext.getDepartmentManagerUserIds();
            if (userIds != null && !userIds.isEmpty()) {
                wrapper.in(FeedPurchase::getCreatorId, userIds);
            } else {
                return page;
            }
        }
        
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
    
    @Override
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
        
        // 计算总价
        if (purchase.getPurchaseAmount() != null && purchase.getUnitPrice() != null) {
            purchase.setTotalPrice(purchase.getPurchaseAmount().multiply(purchase.getUnitPrice()));
        }
        
        return purchaseMapper.insert(purchase) > 0;
    }
    
    @Override
    public boolean updatePurchase(FeedPurchase purchase) {
        // 计算总价
        if (purchase.getPurchaseAmount() != null && purchase.getUnitPrice() != null) {
            purchase.setTotalPrice(purchase.getPurchaseAmount().multiply(purchase.getUnitPrice()));
        }
        return purchaseMapper.updateById(purchase) > 0;
    }
    
    @Override
    public boolean deletePurchase(Long purchaseId) {
        // 软删除，将状态设置为0
        FeedPurchase purchase = getById(purchaseId);
        if (purchase == null) {
            throw new RuntimeException("采购记录不存在");
        }
        purchase.setStatus(0);
        return purchaseMapper.updateById(purchase) > 0;
    }
    
    @Override
    public long count() {
        LambdaQueryWrapper<FeedPurchase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeedPurchase::getStatus, 1);
        return purchaseMapper.selectCount(wrapper);
    }
}

