package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.FeedPurchase;

import java.util.List;

/**
 * 饲料采购服务接口
 */
public interface FeedPurchaseService {
    
    /**
     * 查询所有采购记录
     */
    List<FeedPurchase> getAllPurchases();
    
    /**
     * 根据ID查询采购记录
     */
    FeedPurchase getById(Long purchaseId);
    
    /**
     * 分页查询采购记录列表
     */
    Page<FeedPurchase> getPage(Integer current, Integer size, String feedName, String feedType, String supplier, Integer status);
    
    /**
     * 新增采购记录
     */
    boolean savePurchase(FeedPurchase purchase);
    
    /**
     * 更新采购记录
     */
    boolean updatePurchase(FeedPurchase purchase);
    
    /**
     * 删除采购记录
     */
    boolean deletePurchase(Long purchaseId);
    
    /**
     * 统计采购记录总数
     */
    long count();
}

