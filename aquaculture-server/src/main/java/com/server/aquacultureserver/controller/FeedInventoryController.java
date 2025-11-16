package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.FeedInventory;
import com.server.aquacultureserver.service.FeedInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 饲料库存控制器
 */
@RestController
@RequestMapping("/api/feed/inventory")
@CrossOrigin
public class FeedInventoryController {
    
    @Autowired
    private FeedInventoryService inventoryService;
    
    /**
     * 查询所有库存记录
     */
    @GetMapping("/all")
    public Result<List<FeedInventory>> getAllInventories() {
        List<FeedInventory> inventories = inventoryService.getAllInventories();
        return Result.success(inventories);
    }
    
    /**
     * 分页查询库存记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"feed:inventory:list"})
    public Result<Page<FeedInventory>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String feedName,
            @RequestParam(required = false) String feedType,
            @RequestParam(required = false) Integer status) {
        Page<FeedInventory> page = inventoryService.getPage(current, size, feedName, feedType, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询库存记录
     */
    @GetMapping("/{inventoryId}")
    @RequiresPermission({"feed:inventory:detail"})
    public Result<FeedInventory> getById(@PathVariable Long inventoryId) {
        FeedInventory inventory = inventoryService.getById(inventoryId);
        return Result.success(inventory);
    }
    
    /**
     * 新增库存记录
     */
    @PostMapping
    @RequiresPermission({"feed:inventory:add"})
    public Result<Boolean> saveInventory(@RequestBody FeedInventory inventory) {
        try {
            boolean success = inventoryService.saveInventory(inventory);
            return Result.success("新增成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新库存记录
     */
    @PutMapping
    @RequiresPermission({"feed:inventory:edit"})
    public Result<Boolean> updateInventory(@RequestBody FeedInventory inventory) {
        try {
            boolean success = inventoryService.updateInventory(inventory);
            return Result.success("更新成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除库存记录
     */
    @DeleteMapping("/{inventoryId}")
    @RequiresPermission({"feed:inventory:delete"})
    public Result<Boolean> deleteInventory(@PathVariable Long inventoryId) {
        try {
            boolean success = inventoryService.deleteInventory(inventoryId);
            return Result.success("删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

