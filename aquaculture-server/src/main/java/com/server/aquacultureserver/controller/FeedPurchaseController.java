package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.FeedPurchase;
import com.server.aquacultureserver.service.FeedPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 饲料采购控制器
 */
@RestController
@RequestMapping("/api/feed/purchase")
@CrossOrigin
public class FeedPurchaseController {
    
    @Autowired
    private FeedPurchaseService purchaseService;
    
    /**
     * 查询所有采购记录
     */
    @GetMapping("/all")
    public Result<List<FeedPurchase>> getAllPurchases() {
        List<FeedPurchase> purchases = purchaseService.getAllPurchases();
        return Result.success(purchases);
    }
    
    /**
     * 分页查询采购记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"feed:purchase:list"})
    public Result<Page<FeedPurchase>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String feedName,
            @RequestParam(required = false) String feedType,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) Integer status) {
        Page<FeedPurchase> page = purchaseService.getPage(current, size, feedName, feedType, supplier, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询采购记录
     */
    @GetMapping("/{purchaseId}")
    @RequiresPermission({"feed:purchase:detail"})
    public Result<FeedPurchase> getById(@PathVariable Long purchaseId) {
        FeedPurchase purchase = purchaseService.getById(purchaseId);
        return Result.success(purchase);
    }
    
    /**
     * 新增采购记录
     */
    @PostMapping
    @RequiresPermission({"feed:purchase:add"})
    public Result<Boolean> savePurchase(@RequestBody FeedPurchase purchase) {
        try {
            boolean success = purchaseService.savePurchase(purchase);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新采购记录
     */
    @PutMapping
    @RequiresPermission({"feed:purchase:edit"})
    public Result<Boolean> updatePurchase(@RequestBody FeedPurchase purchase) {
        try {
            boolean success = purchaseService.updatePurchase(purchase);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除采购记录
     */
    @DeleteMapping("/{purchaseId}")
    @RequiresPermission({"feed:purchase:delete"})
    public Result<Boolean> deletePurchase(@PathVariable Long purchaseId) {
        try {
            boolean success = purchaseService.deletePurchase(purchaseId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据饲料名称和类型查询采购记录
     */
    @GetMapping("/byFeed")
    public Result<List<FeedPurchase>> getByFeedNameAndType(
            @RequestParam String feedName,
            @RequestParam String feedType) {
        try {
            List<FeedPurchase> purchases = purchaseService.getByFeedNameAndType(feedName, feedType);
            return Result.success(purchases);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

