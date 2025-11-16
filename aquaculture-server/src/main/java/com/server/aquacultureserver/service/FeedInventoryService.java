package com.server.aquacultureserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.FeedInventory;

import java.util.List;

/**
 * 饲料库存服务接口
 */
public interface FeedInventoryService {
    
    /**
     * 查询所有库存记录
     */
    List<FeedInventory> getAllInventories();
    
    /**
     * 根据ID查询库存记录
     */
    FeedInventory getById(Long inventoryId);
    
    /**
     * 分页查询库存记录列表
     */
    Page<FeedInventory> getPage(Integer current, Integer size, String feedName, String feedType, Integer status);
    
    /**
     * 新增库存记录
     */
    boolean saveInventory(FeedInventory inventory);
    
    /**
     * 更新库存记录
     */
    boolean updateInventory(FeedInventory inventory);
    
    /**
     * 删除库存记录
     */
    boolean deleteInventory(Long inventoryId);
    
    /**
     * 根据饲料名称和类型查询库存
     */
    FeedInventory getByFeedNameAndType(String feedName, String feedType);
    
    /**
     * 统计库存记录总数
     */
    long count();
}

