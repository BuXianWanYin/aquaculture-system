package com.server.aquacultureserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.annotation.RequiresPermission;
import com.server.aquacultureserver.common.Result;
import com.server.aquacultureserver.domain.MedicineInventory;
import com.server.aquacultureserver.service.MedicineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品库存控制器
 */
@RestController
@RequestMapping("/api/medicine/inventory")
@CrossOrigin
public class MedicineInventoryController {
    
    @Autowired
    private MedicineInventoryService inventoryService;
    
    /**
     * 查询所有库存记录
     */
    @GetMapping("/all")
    public Result<List<MedicineInventory>> getAllInventories() {
        List<MedicineInventory> inventories = inventoryService.getAllInventories();
        return Result.success(inventories);
    }
    
    /**
     * 分页查询库存记录列表
     */
    @GetMapping("/page")
    @RequiresPermission({"medicine:inventory:list"})
    public Result<Page<MedicineInventory>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String medicineName,
            @RequestParam(required = false) String medicineType,
            @RequestParam(required = false) Integer status) {
        Page<MedicineInventory> page = inventoryService.getPage(current, size, medicineName, medicineType, status);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询库存记录
     */
    @GetMapping("/{inventoryId}")
    @RequiresPermission({"medicine:inventory:detail"})
    public Result<MedicineInventory> getById(@PathVariable Long inventoryId) {
        MedicineInventory inventory = inventoryService.getById(inventoryId);
        return Result.success(inventory);
    }
}

